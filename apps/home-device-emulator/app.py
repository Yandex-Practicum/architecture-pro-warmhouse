import json
import os
import random
import sys
import threading
import time
import uuid

import pika

from emulator_model import DeviceEvent, DeviceState

mq_url = f"amqp://{os.environ.get('MQ_USER', 'rabbit')}:{os.environ.get('MQ_PASSWORD', 'password')}@{os.environ.get('MQ_HOST', 'localhost')}:{os.environ.get('MQ_PORT', '5672')}/"

device_commands_mq = "smart-home.device.commands"
telemetry_metrics_mq = "smart-home.telemetry.metrics"

device_events_topic = "topics.device.events"

device_states = {}
initial_value = 15.3
desired_value = None


def on_message_callback(ch, method, properties, body):
    print(f" [x] Received '{body.decode()}'")
    eventDict = json.loads(body.decode('utf-8'))
    event = DeviceEvent.model_validate(eventDict)
    device = event.device
    device_id = device.id

    global device_states, desired_value, initial_value
    if event.eventType == 'CREATED' or event.eventType == 'UPDATED':
        if event.eventType == 'CREATED':
            new_value = None if device.type == 'HEATER' else initial_value
        else:
            new_value = device_states[device_id].value
        device_states[device_id] = DeviceState(
            name=device.name,
            device_type=device.type,
            mac_address=device.macAddress,
            active=device.status == 'ACTIVE',
            value=new_value)
    elif event.eventType == 'SET_VALUE' and device.type == 'HEATER':
        desired_value = event.deviceValue.value
        device_states[device_id].value = event.deviceValue.value
    elif event.eventType == 'ACTIVATED':
        device_states[device_id].active = True
    elif event.eventType == 'DEACTIVATED':
        device_states[device_id].active = False
    elif event.eventType == 'DELETED':
        print(f'Устройство {device.id} : {device.name} было удалено.')
        device_states.pop(device_id)
        if len(device_states) < 1:
            print('Устройств не осталось. Останавливаем режим эмуляции')
            sys.exit(0)


def send_metrics_thread_function(unused, halt_event, interval=10):
    # Отправляем в очередь метрики от нашего устройства
    telemetry_metrics_mq_connection = pika.BlockingConnection(pika.URLParameters(mq_url))
    telemetry_metrics_mq_channel = telemetry_metrics_mq_connection.channel()
    telemetry_metrics_mq_channel.queue_declare(queue=telemetry_metrics_mq, durable=True)
    global device_states, desired_value
    try:
        while not halt_event.is_set():
            print(f' ... Ждем {interval} секунд...')
            time.sleep(interval)

            if not device_states:
                print(f' ... Ждем запуска приложения device-service...')

            for device_id, device_state in device_states.items():
                if device_state.active:
                    # HEATER отправляет метрику в виде выставленной(пользователем) на устройстве ЖЕЛАЕМОЙ температуры
                    # тут всегда то же значение, что и в desired_value выставляемом пользователем командой SET_VALUE
                    if device_state.device_type == 'TEMPERATURE_SENSOR':
                        # TEMPERATURE_SENSOR в качестве ЭМУЛЯЦИИ будет ИТЕРАЦИОННО (+-1 от текущего показания сенсора)
                        # пытаться приводить температуру к желаемой (выставленной на устройстве типа HEATER)
                        if desired_value and device_state.value < desired_value:
                            device_state.value = device_state.value + 1
                        elif desired_value and device_state.value > desired_value:
                            device_state.value = device_state.value - 1
                    print(f'     устройство {device_id} : {device_state.name} активно. Отправляет метрику:')
                    metric_message = {
                        "@type": "METRIC",
                        "macAddress": device_state.mac_address,
                        "value": device_state.value
                    }
                    send_message(telemetry_metrics_mq_channel, telemetry_metrics_mq, metric_message)
                    print(metric_message)
                    if random.randint(0, 1) == 1:
                        print(
                            f'   Для деактивации устройства выполните `curl -X POST http://localhost:8083/api/v1/devices/deactivate/{device_id}`')
                    else:
                        print(
                            f'   Для удаления устройства выполните    `curl -X DELETE http://localhost:8083/api/v1/devices/{device_id}`')
                else:
                    print(
                        f'     устройство {device_id} : {device_state.name} неактивно.\n   Для активации устройства выполните   `curl -X POST http://localhost:8083/api/v1/devices/activate/{device_id}`')
    finally:
        telemetry_metrics_mq_connection.close()


def send_message(channel, mq_queue, message):
    channel.basic_publish(
        exchange="",
        routing_key=mq_queue,
        body=json.dumps(message),
        properties=pika.BasicProperties(
            delivery_mode=2, content_type="application/json"
        ),
    )


def emulate_device(house_id, location):
    stop_event = threading.Event()

    # Отправляем в очередь событие подключения нового устройства, чтобы зарегистрировать его в SmartHome сервере
    device_commands_mq_connection = pika.BlockingConnection(pika.URLParameters(mq_url))
    device_commands_mq_channel = device_commands_mq_connection.channel()
    device_commands_mq_channel.queue_declare(queue=device_commands_mq, durable=True)

    # Слушаем из топика события от сервера, касающиеся нашего устройства, чтобы применять предназначенные ему команды
    topic_connection = pika.BlockingConnection(pika.URLParameters(mq_url))
    device_events_topic_channel = topic_connection.channel()
    device_events_topic_channel.exchange_declare(exchange=device_events_topic, exchange_type='topic', durable=True)
    events_queue = device_events_topic_channel.queue_declare('', durable=False, auto_delete=True)
    events_queue_name = events_queue.method.queue

    print("Эмулятор smartHome device запущен...\n")

    try:
        # отправку метрик запустим в отдельном потоке
        consumer_thread = threading.Thread(target=send_metrics_thread_function, args=('unused', stop_event))
        consumer_thread.start()

        # создаем новые устройства
        for device_type in ["TEMPERATURE_SENSOR", "HEATER", ]:
            mac_address = str(uuid.uuid4())
            new_device_message = {
                "@type": "REGISTER",
                "houseId": house_id,
                "name": f"{device_type} at {location}",
                "deviceType": device_type,
                "location": location,
                "macAddress": mac_address
            }
            send_message(device_commands_mq_channel, device_commands_mq, new_device_message)
            print(new_device_message)

            # для каждого нового устройства биндим топик с его MacAddress в routing_key, чтобы слушать его события
            device_events_topic_channel.queue_bind(exchange=device_events_topic, queue=events_queue_name,
                                                   routing_key=mac_address)

        # главный поток заблокируем на чтение событий из топика
        print(f' [*] Слушаем события для ваших устройств.')
        device_events_topic_channel.basic_consume(queue=events_queue_name, on_message_callback=on_message_callback,
                                                  auto_ack=True)
        device_events_topic_channel.start_consuming()

    except KeyboardInterrupt:
        print("Эмулятор smartHome device остановлен...\n")
    finally:
        device_commands_mq_connection.close()
        device_events_topic_channel.stop_consuming()
        topic_connection.close()
        stop_event.set()
        consumer_thread.join()


def get_user_choice(prompt, options):
    while True:
        user_input = input(f"{prompt}: {', '.join(f'{k} - для {v}' for k, v, in options.items())}\n> ")
        if user_input in options.keys():
            print(f"Выбран: {user_input} - {options[user_input]}")
            return options[user_input]
        else:
            print("Неверный выбор. Попробуйте снова.")


if __name__ == "__main__":
    house_id = int(input("Введите id вашего дома (целое число): "))

    # device_type_options = {
    #     "1": "TEMPERATURE_SENSOR",
    #     "2": "HEATER",
    # }
    # device_type = get_user_choice("Выберите тип устройства", device_type_options)
    # device_name = input("Введите имя нового устройства (строка): ")
    location = input("Введите название комнаты (строка): ")

    emulate_device(house_id, location)
