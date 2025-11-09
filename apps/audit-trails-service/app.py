import os

import pika

mq_url = f"amqp://{os.environ.get('MQ_USER', 'rabbit')}:{os.environ.get('MQ_PASSWORD', 'password')}@{os.environ.get('MQ_HOST', 'localhost')}:{os.environ.get('MQ_PORT', '5672')}/"

device_events_topic = "topics.device.events"


def on_message_callback(ch, method, properties, body):
    print(f" [x] Received '{body.decode()}'")


if __name__ == '__main__':
    topic_connection = pika.BlockingConnection(pika.URLParameters(mq_url))
    device_events_topic_channel = topic_connection.channel()
    try:
        # Слушаем из топика все события по всем устройствам и выводим их в консоль
        device_events_topic_channel.exchange_declare(exchange=device_events_topic, exchange_type='topic', durable=True)
        events_queue = device_events_topic_channel.queue_declare('', durable=False, auto_delete=True)
        events_queue_name = events_queue.method.queue

        device_events_topic_channel.queue_bind(exchange=device_events_topic, queue=events_queue_name,
                                               # слушаем вообще все события из топика
                                               routing_key='#')

        device_events_topic_channel.basic_consume(queue=events_queue_name, on_message_callback=on_message_callback,
                                                  auto_ack=True)
        device_events_topic_channel.start_consuming()

    except KeyboardInterrupt:
        print("stop logging...\n")
    finally:
        device_events_topic_channel.stop_consuming()
        topic_connection.close()
