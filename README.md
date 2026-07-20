# Smart Home — Умный дом (Проектная работа)

## Команда
- **Член команды:** VTSYBIN

## Ссылки
- **Исходный код:** [https://github.com/VladimirTsy/architecture-pro-warmhouse](https://github.com/VladimirTsy/architecture-pro-warmhouse) (ветка `warmhouse`)

---

# Задание 1. Анализ и планирование

### 1. Описание функциональности монолитного приложения

**Управление отоплением:**

- Пользователи могут удалённо включать и выключать отопление в отдельных комнатах.
- Пользователи могут устанавливать целевую температуру для каждого устройства.
- Пользователи могут переключать режимы работы: economy, comfort, night.
- Система поддерживает проверку бизнес-правил перед выполнением команды.
- Система поддерживает отправку команд устройствам с немедленным подтверждением.

**Мониторинг температуры:**

- Пользователи могут просматривать текущие показания температуры по комнатам.
- Система поддерживает регулярный опрос датчиков (pull-механизм).
- Система поддерживает хранение истории показаний в базе данных.
- Система поддерживает проверку пороговых значений и автоматические уведомления.

### 2. Анализ архитектуры монолитного приложения

- **Язык:** Go, **Фреймворк:** Gin, **БД:** PostgreSQL 16, **Драйвер:** pgx/v5
- **Структура:** `main.go`, `handlers/`, `services/`, `models/`, `db/`
- **Внешние вызовы:** HTTP к датчикам температуры

### 3. Определение доменов и границы контекстов

| Домен | Ключевые сущности |
|-------|-------------------|
| Heating Control | Device, DeviceState, HeatingMode |
| Temperature Monitoring | TelemetryData, Sensor |
| Notifications | NotificationSetting, NotificationLog |
| Users | User, UserHouse |

### 4. Проблемы монолитного решения

- Разный масштаб доменов (телеметрия vs пользователи)
- Риск каскадных отказов
- Технологические ограничения (один язык)
- Сложность независимого развёртывания
- Единая база данных

### 5. Визуализация контекста системы — диаграмма C4

[Диаграмма контекста (As-Is)](./as_is_arch.puml)

---

# Задание 2. Проектирование микросервисной архитектуры

**[Диаграмма контейнеров (To-Be)](./to_be_container.puml)** — 5 микросервисов с собственными БД, API Gateway, Message Broker

**[Диаграмма компонентов Heating Control](./to_be_component.puml)** — API Controller, Command Handler, Device State Manager, Event Publisher

**[Диаграмма последовательности](./to_be_code.puml)** — сценарий «Включение отопления»

---

# Задание 3. Разработка ER-диаграммы

[ER-диаграмма](./to_be_er.puml) — 11 сущностей: User, House, UserHouse, DeviceType, Device, DeviceState, HeatingMode, Module, TelemetryData, NotificationSetting, NotificationLog

---

# Задание 4. Создание и документирование API

### 1. Тип API

- **REST API (OpenAPI 3.0)** — синхронное: управление, команды, аутентификация
- **AsyncAPI (2.6)** — асинхронное: события через Message Broker

### 2. Документация API

- [docs/api-specification.md](./docs/api-specification.md) — полная спецификация
- [docs/api-specification.yaml](./docs/api-specification.yaml) — OpenAPI 3.0
- [docs/api-documentation.md](./docs/api-documentation.md) — краткая документация

---

# Задание 5. Работа с docker и docker-compose

**temperature-api (Go, порт 8081):** рандомная температура по запросу.

**PostgreSQL:** инициализация через `./smart_home/init.sql`.

**Проверка:** `POST /api/v1/sensors` → `GET /api/v1/sensors` (каждый вызов — разная температура).

```bash
cd apps
docker-compose up --build
```

---

# ✅ Задание 6. Разработка MVP

Созданы 2 микросервиса, интегрированных с монолитом через HTTP:

| Микросервис | Язык | Порт | БД |
|-------------|------|------|-----|
| device-service | Python/Flask | 8082 | SQLite |
| telemetry-service | Java/Spring Boot | 8083 | H2 |

Монолит (Go) при CRUD-операциях синхронизирует данные с микросервисами через HTTP-клиенты (`device_service.go`, `telemetry_client.go`). Ошибки не блокируют основной ответ.
