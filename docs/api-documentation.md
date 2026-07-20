# Smart Home Heating System — API Documentation

**Версия:** 1.0.0
**Базовый URL:** `https://api.smarthome.com/v1`
**Формат данных:** JSON
**Аутентификация:** JWT (Bearer Token) — требуется для всех эндпоинтов, кроме `/auth/login`.

> **Полная спецификация:** [api-specification.md](./api-specification.md)
> **OpenAPI 3.0 (YAML):** [api-specification.yaml](./api-specification.yaml)

---

## Сводка эндпоинтов

| # | Метод | Путь | Описание |
|---|-------|------|----------|
| 1 | `POST` | `/auth/login` | Аутентификация пользователя |
| 2 | `POST` | `/devices` | Регистрация нового устройства |
| 3 | `GET` | `/devices/{deviceId}/state` | Текущее состояние устройства |
| 4 | `POST` | `/devices/{deviceId}/commands` | Отправка команды устройству |
| 5 | `GET` | `/devices/{deviceId}/telemetry/current` | Последнее показание температуры |

## Асинхронные события (AsyncAPI)

| Событие | Канал | Описание |
|---------|-------|----------|
| `HeatingToggled` | `heating.toggled` | Отопление включено/выключено |
| `TemperatureSet` | `heating.temperature_set` | Целевая температура изменена |
| `TemperatureDrop` | `temperature.drop` | Температура ниже порога |
| `NewReading` | `temperature.new_reading` | Новое показание с датчика |

Для полного описания контрактов, схем данных и примеров запросов/ответов см. [api-specification.md](./api-specification.md).
