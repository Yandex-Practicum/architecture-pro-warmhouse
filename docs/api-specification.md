# Smart Home Heating System — Спецификация API (Задание 4)

**Версия:** 1.0.0
**Автор:** Команда Warmhouse
**Дата:** 2025-07-20

---

## 1. Тип API

### 1.1 Выбор типа API

Для системы «Умный дом» выбрано комбинированное взаимодействие:

| Тип API | Обоснование | Где используется |
|---------|-------------|------------------|
| **REST API** (OpenAPI 3.0) | Большинство операций управления отоплением требуют немедленного ответа: включение/выключение, установка температуры, запрос состояния устройства. Клиент (другой микросервис или UI) не может продолжить работу без подтверждения. | Все синхронные эндпоинты: аутентификация, управление устройствами, команды, чтение телеметрии |
| **AsyncAPI** (2.6) | Уведомления и события о изменении температуры не требуют немедленной доставки — достаточно, чтобы уведомление было доставлено в асинхронном режиме через брокер сообщений. | События: `HeatingToggled`, `TemperatureSet`, `TemperatureDrop`, `NewReading` |

---

## 2. Архитектура взаимодействия микросервисов

```
┌──────────────────────────────────────────────────────────────────┐
│                        API Gateway                               │
│                    (маршрутизация запросов)                      │
└────────┬──────────┬──────────────┬──────────────┬────────────────┘
         │          │              │              │
         ▼          ▼              ▼              ▼
  ┌──────────┐ ┌──────────┐  ┌──────────┐  ┌──────────┐
  │   User   │ │  Device  │  │  Heating │  │ Temp.    │
  │ Service  │ │Regist.   │  │ Control  │  │Monitor.  │
  │          │ │ Service  │  │ Service  │  │ Service  │
  └────┬─────┘ └────┬─────┘  └────┬─────┘  └────┬─────┘
       │            │             │              │
       │            │             │    ┌─────────┴─────────┐
       │            │             │    │  Message Broker   │
       │            │             └────┤  (RabbitMQ/Kafka) │
       │            │                  └─────────┬─────────┘
       │            │                            │
       │            │                    ┌───────┴───────┐
       │            │                    │  Notification │
       │            │                    │    Service    │
       │            │                    └───────────────┘
```

### Синхронные вызовы (REST):
- `Heating Control` → `Device Registration`: GET данные об устройстве
- `Heating Control` → `Temperature Monitoring`: GET текущая температура
- `User Service` → `Heating Control`: проверка прав

### Асинхронные события (Message Broker):
- `Heating Control` → `Broker`: `HeatingToggled`, `TemperatureSet`
- `Temperature Monitoring` → `Broker`: `TemperatureDrop`, `NewReading`
- `Broker` → `Notification Service`: подписка на события

---

## 3. REST API — OpenAPI 3.0

> Полная спецификация в формате YAML: `docs/api-specification.yaml`

**Базовый URL:** `https://api.smarthome.com/v1`
**Формат данных:** JSON
**Аутентификация:** JWT (Bearer Token) — требуется для всех эндпоинтов, кроме `/auth/login`.
Заголовок: `Authorization: Bearer <token>`

---

### 3.1. POST `/auth/login` — Аутентификация пользователя

**Микросервис:** User Service
**Описание:** Проверяет учётные данные пользователя и выдаёт JWT-токен доступа.

#### Запрос

**Headers:**
| Заголовок | Тип | Описание |
|-----------|-----|----------|
| `Content-Type` | `string` | `application/json` |

**Body:**
| Параметр | Тип | Обязательно | Описание |
|----------|-----|-------------|----------|
| `email` | `string` (email) | Да | Email пользователя |
| `password` | `string` | Да | Пароль пользователя |

```json
{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

#### Ответ

| Код | Описание |
|-----|----------|
| `200` | Успешный вход |
| `401` | Неверные учётные данные |
| `500` | Внутренняя ошибка сервера |

**Тело ответа (200):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

---

### 3.2. POST `/devices` — Регистрация нового устройства

**Микросервис:** Device Registration Service
**Описание:** Добавляет устройство в систему. Требуется роль «администратор» или «владелец».

#### Запрос

**Headers:**
| Заголовок | Тип | Описание |
|-----------|-----|----------|
| `Authorization` | `string` | `Bearer <token>` |
| `Content-Type` | `string` | `application/json` |

**Body:**
| Параметр | Тип | Обязательно | Описание |
|----------|-----|-------------|----------|
| `serial_number` | `string` | Да | Уникальный серийный номер устройства |
| `type_id` | `string` (UUID) | Да | ID типа устройства |
| `house_id` | `string` (UUID) | Да | ID дома, к которому привязано устройство |
| `firmware_version` | `string` | Нет | Версия прошивки |

```json
{
  "serial_number": "SN-12345-6789",
  "type_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "house_id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "firmware_version": "2.1.0"
}
```

#### Ответ

| Код | Описание |
|-----|----------|
| `201` | Устройство зарегистрировано |
| `400` | Некорректные данные или серийный номер уже существует |
| `403` | Недостаточно прав |
| `500` | Ошибка сервера |

**Тело ответа (201):**
```json
{
  "id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "serial_number": "SN-12345-6789",
  "registered_at": "2025-01-01T12:00:00Z"
}
```

---

### 3.3. GET `/devices/{deviceId}/state` — Состояние устройства

**Микросервис:** Heating Control Service
**Описание:** Возвращает текущее состояние устройства: включено/выключено, целевую температуру, режим.

#### Запрос

**Path Parameters:**
| Параметр | Тип | Описание |
|----------|-----|----------|
| `deviceId` | `string` (UUID) | ID устройства |

**Headers:**
| Заголовок | Тип | Описание |
|-----------|-----|----------|
| `Authorization` | `string` | `Bearer <token>` |

#### Ответ

| Код | Описание |
|-----|----------|
| `200` | Состояние устройства получено |
| `401` | Неавторизован |
| `404` | Устройство не найдено |
| `500` | Ошибка сервера |

**Тело ответа (200):**
```json
{
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "is_on": true,
  "target_temperature": 22.5,
  "current_mode": "comfort",
  "updated_at": "2025-01-01T12:00:00Z"
}
```

---

### 3.4. POST `/devices/{deviceId}/commands` — Команда устройству

**Микросервис:** Heating Control Service
**Описание:** Отправляет команду на включение/выключение отопления или изменение целевой температуры.

#### Запрос

**Path Parameters:**
| Параметр | Тип | Описание |
|----------|-----|----------|
| `deviceId` | `string` (UUID) | ID устройства |

**Headers:**
| Заголовок | Тип | Описание |
|-----------|-----|----------|
| `Authorization` | `string` | `Bearer <token>` |
| `Content-Type` | `string` | `application/json` |

**Body:**
| Параметр | Тип | Обязательно | Описание |
|----------|-----|-------------|----------|
| `command` | `string` | Да | Тип команды: `turn_on`, `turn_off`, `set_temperature` |
| `value` | `number` | Нет | Обязателен для `set_temperature`. Игнорируется для `turn_on`/`turn_off` |

**Пример 1 — Включить отопление:**
```json
{
  "command": "turn_on"
}
```

**Пример 2 — Установить температуру 23 °C:**
```json
{
  "command": "set_temperature",
  "value": 23.0
}
```

**Пример 3 — Выключить отопление:**
```json
{
  "command": "turn_off"
}
```

#### Ответ

| Код | Описание |
|-----|----------|
| `200` | Команда принята и выполнена |
| `400` | Некорректная команда или значение |
| `403` | Недостаточно прав |
| `404` | Устройство не найдено |
| `500` | Ошибка сервера |

**Тело ответа (200):**
```json
{
  "status": "success",
  "message": "Temperature set to 23.0 °C",
  "updated_state": {
    "is_on": true,
    "target_temperature": 23.0,
    "current_mode": "comfort",
    "updated_at": "2025-01-01T12:01:00Z"
  }
}
```

---

### 3.5. GET `/devices/{deviceId}/telemetry/current` — Последнее показание температуры

**Микросервис:** Temperature Monitoring Service
**Описание:** Возвращает самое свежее значение температуры от датчика устройства.

#### Запрос

**Path Parameters:**
| Параметр | Тип | Описание |
|----------|-----|----------|
| `deviceId` | `string` (UUID) | ID устройства |

**Headers:**
| Заголовок | Тип | Описание |
|-----------|-----|----------|
| `Authorization` | `string` | `Bearer <token>` |

#### Ответ

| Код | Описание |
|-----|----------|
| `200` | Текущее показание получено |
| `401` | Неавторизован |
| `404` | Устройство не найдено или нет данных |
| `500` | Ошибка сервера |

**Тело ответа (200):**
```json
{
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "value": 22.0,
  "unit": "°C",
  "timestamp": "2025-01-01T12:05:00Z"
}
```

---

## 4. AsyncAPI — Асинхронные события

> События публикуются в Message Broker (RabbitMQ / Kafka) и потребляются подписанными микросервисами.

### 4.1. Событие `HeatingToggled`

**Публикатор:** Heating Control Service
**Подписчики:** Notification Service

**Описание:** Публикуется при изменении состояния отопления (включение / выключение).

**Канал (Topic):** `heating.toggled`

**Schema сообщения:**

| Поле | Тип | Описание |
|------|-----|----------|
| `event_id` | `string` (UUID) | Уникальный ID события |
| `device_id` | `string` (UUID) | ID устройства |
| `status` | `string` | Новое состояние: `on` или `off` |
| `timestamp` | `string` (date-time) | Время события (ISO 8601) |

**Пример:**
```json
{
  "event_id": "e1a2b3c4-d5e6-7890-abcd-ef1234567890",
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "status": "on",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

---

### 4.2. Событие `TemperatureSet`

**Публикатор:** Heating Control Service
**Подписчики:** Notification Service

**Описание:** Публикуется при изменении целевой температуры.

**Канал (Topic):** `heating.temperature_set`

**Schema сообщения:**

| Поле | Тип | Описание |
|------|-----|----------|
| `event_id` | `string` (UUID) | Уникальный ID события |
| `device_id` | `string` (UUID) | ID устройства |
| `target_temperature` | `number` | Новая целевая температура |
| `unit` | `string` | Единица измерения |
| `timestamp` | `string` (date-time) | Время события |

**Пример:**
```json
{
  "event_id": "f2b3c4d5-e6f7-8901-bcde-f2345678901234",
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "target_temperature": 23.0,
  "unit": "°C",
  "timestamp": "2025-01-01T12:01:00Z"
}
```

---

### 4.3. Событие `TemperatureDrop`

**Публикатор:** Temperature Monitoring Service
**Подписчики:** Notification Service, Heating Control Service

**Описание:** Публикуется при падении температуры ниже заданного порога (например, ниже 18 °C).

**Канал (Topic):** `temperature.drop`

**Schema сообщения:**

| Поле | Тип | Описание |
|------|-----|----------|
| `event_id` | `string` (UUID) | Уникальный ID события |
| `device_id` | `string` (UUID) | ID устройства |
| `current_value` | `number` | Текущее значение температуры |
| `threshold` | `number` | Пороговое значение |
| `unit` | `string` | Единица измерения |
| `timestamp` | `string` (date-time) | Время события |

**Пример:**
```json
{
  "event_id": "a3c4d5e6-f7a8-9012-cdef-34567890123456",
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "current_value": 17.2,
  "threshold": 18.0,
  "unit": "°C",
  "timestamp": "2025-01-01T12:10:00Z"
}
```

---

### 4.4. Событие `NewReading`

**Публикатор:** Temperature Monitoring Service
**Подписчики:** Notification Service (опционально)

**Описание:** Публикуется при поступлении нового показания температуры с датчика.

**Канал (Topic):** `temperature.new_reading`

**Schema сообщения:**

| Поле | Тип | Описание |
|------|-----|----------|
| `event_id` | `string` (UUID) | Уникальный ID события |
| `device_id` | `string` (UUID) | ID устройства |
| `value` | `number` | Значение температуры |
| `unit` | `string` | Единица измерения |
| `timestamp` | `string` (date-time) | Время измерения |

**Пример:**
```json
{
  "event_id": "b4d5e6f7-a8b9-0123-defa-45678901234567",
  "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
  "value": 21.8,
  "unit": "°C",
  "timestamp": "2025-01-01T12:05:00Z"
}
```

---

## 5. Контракты взаимодействия между микросервисами

### 5.1 Сценарий: «Пользователь включает отопление»

```
Пользователь ──POST /devices/{id}/commands──▶ API Gateway ──▶ Heating Control Service
                                                         │
                                                         ├─GET /devices/{id}──▶ Device Registration Service
                                                         │
                                                         ├─GET /sensors/{id}/current──▶ Temperature Monitoring Service
                                                         │
                                                         └─publish HeatingToggled──▶ Message Broker ──▶ Notification Service
                                                                                                   │
                                                                                                   └──▶ Email / Push → Пользователь
```

**Шаги:**
1. Пользователь отправляет команду через API Gateway.
2. User Service проверяет права (JWT).
3. Heating Control запрашивает данные об устройстве у Device Registration Service.
4. Heating Control запрашивает текущую температуру у Temperature Monitoring Service.
5. Heating Control проверяет бизнес-правила (например, температура не выше порога).
6. Heating Control обновляет состояние устройства и публикует событие `HeatingToggled`.
7. Notification Service получает событие и отправляет уведомление пользователю.

### 5.2 Сценарий: «Температура упала ниже порога»

```
Датчик ──HTTP pull──▶ Temperature Monitoring Service
                      │
                      ├─сохранение в Monitoring DB
                      │
                      ├─publish NewReading──▶ Message Broker
                      │
                      └─publish TemperatureDrop──▶ Message Broker ──▶ Notification Service
                                                                  │
                                                                  ├──▶ Heating Control (автокоррекция)
                                                                  │
                                                                  └──▶ Email / Push → Пользователь
```

**Шаги:**
1. Temperature Monitoring опрашивает датчик (pull).
2. Если температура < порога, публикуется событие `TemperatureDrop`.
3. Notification Service формирует предупреждение для пользователя.
4. Heating Control может автоматически включить отопление или повысить целевую температуру.

---

## 6. Сводная таблица всех эндпоинтов

| # | Метод | Путь | Микросервис | Описание |
|---|-------|------|-------------|----------|
| 1 | `POST` | `/auth/login` | User Service | Аутентификация, получение JWT |
| 2 | `POST` | `/devices` | Device Registration Service | Регистрация устройства |
| 3 | `GET` | `/devices/{deviceId}/state` | Heating Control Service | Состояние устройства |
| 4 | `POST` | `/devices/{deviceId}/commands` | Heating Control Service | Команда устройству |
| 5 | `GET` | `/devices/{deviceId}/telemetry/current` | Temperature Monitoring Service | Текущая температура |

| # | Событие | Канал | Публикатор | Подписчики |
|---|---------|-------|------------|------------|
| 1 | `HeatingToggled` | `heating.toggled` | Heating Control | Notification |
| 2 | `TemperatureSet` | `heating.temperature_set` | Heating Control | Notification |
| 3 | `TemperatureDrop` | `temperature.drop` | Temperature Monitoring | Notification, Heating Control |
| 4 | `NewReading` | `temperature.new_reading` | Temperature Monitoring | Notification |

---

## 7. Модели данных (схемы)

### 7.1 DeviceState

```json
{
  "device_id": "string (UUID)",
  "is_on": "boolean",
  "target_temperature": "number (float)",
  "current_mode": "string (enum: economy | comfort | night)",
  "updated_at": "string (date-time, ISO 8601)"
}
```

### 7.2 TelemetryReading

```json
{
  "device_id": "string (UUID)",
  "value": "number (float)",
  "unit": "string (e.g., '°C')",
  "timestamp": "string (date-time, ISO 8601)"
}
```

### 7.3 DeviceRegistration

```json
{
  "id": "string (UUID)",
  "serial_number": "string",
  "type_id": "string (UUID)",
  "house_id": "string (UUID)",
  "firmware_version": "string",
  "registered_at": "string (date-time, ISO 8601)"
}
```

---

## 8. Обработка ошибок

Все ошибки возвращаются в едином формате:

```json
{
  "error": {
    "code": "DEVICE_NOT_FOUND",
    "message": "Устройство с указанным ID не найдено",
    "details": {
      "device_id": "c3d4e5f6-a7b8-9012-cdef-345678901234"
    }
  }
}
```

| HTTP-код | Сценарий |
|----------|----------|
| `200` | Успешное выполнение |
| `201` | Ресурс создан |
| `400` | Некорректные входные данные |
| `401` | Неавторизованный запрос |
| `403` | Недостаточно прав |
| `404` | Ресурс не найден |
| `409` | Конфликт (например, серийный номер уже существует) |
| `422` | Ошибка валидации данных |
| `429` | Превышен лимит запросов |
| `500` | Внутренняя ошибка сервера |
| `503` | Сервис временно недоступен |
