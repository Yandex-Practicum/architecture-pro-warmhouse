#### Запустить эмулятор

```bash
docker build -t home-device-emulator . && docker run --network host -it home-device-emulator
```

### Для активации|деактивации эмулируемых устройств

можно использовать `curl`

```bash
curl -X POST http://localhost:8083/api/v1/devices/activate/${DEVICE_ID?}
```

```bash
curl -X POST http://localhost:8083/api/v1/devices/deactivate/${DEVICE_ID?}
```

### Для задания желаемой температуры в комнате

установите желаемое значение на устройстве типа `HEATER` с помощью `curl`

```bash
curl -X POST -H 'Content-Type: application/json' -d '{"value": 28.0}' http://localhost:8083/api/v1/devices/setValue/${DEVICE_ID?}
```

после этого эмулятор начнет "привожить температуру" ближе к желаемой итерациями +-1 градус,
что можно будет увидеть в метриках устройства типа `TEMPERATURE_SENSOR`

### Для удаления устройств выполните

```bash
curl -X DELETE http://localhost:8083/api/v1/devices/${DEVICE_ID?}
```

После удаления всех устройств эмулятор завершит работу
