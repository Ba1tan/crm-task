# звонки и встречи


**Стек:** Java 21, Spring Boot 4.1, Spring Data JPA (Hibernate), PostgreSQL 16, Flyway, Lombok,
JUnit 5 + Testcontainers. Сборка: Maven

## Что нужно установить

| Для чего | Что |
|---|---|
| Запуск одной командой | Docker с Compose v2 (проверка: `docker compose version`) |
| Запуск приложения без контейнера, тесты | JDK 21 (`java -version`) и запущенный Docker |

При первом запуске нужен интернет: скачиваются образы и зависимости Maven.

## Запуск с нуля (в корне проекта)

```bash
docker compose up -d
```

Дождитесь строки `Started `. API доступен на `http://localhost:8080`.
Проверка:

```bash
curl http://localhost:8080/companies
# {"items":[],"page":0,"size":10,"totalElements":0,"totalPages":0}
```

Остановка: `docker compose down`. Удалить данные БД: `docker compose down -v`.

Если заняты порты 8080 или 5432, поменяйте левую часть в `ports:` в `docker-compose.yml`.

Схема БД создаётся сама при старте приложения миграциями Flyway
(`src/main/resources/db/migration`): `V1__init.sql` создаёт таблицы и ограничения,
`V2__seed_managers.sql` добавляет трёх менеджеров с id 1, 2, 3.

## Переменные окружения

| Переменная | По умолчанию | Назначение |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/crm` | JDBC-адрес PostgreSQL |
| `DB_USER` | `admin` | пользователь БД |
| `DB_PASSWORD` | `crm` | пароль БД |

В `docker-compose.yml` они уже заданы (`DB_URL=jdbc:postgresql://db:5432/crm`), ничего настраивать не нужно.

## Запуск приложения без Docker для приложения

Если нужно запустить приложение из IDE или командной строки, а базу взять из compose:

```bash
docker compose up -d db
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
mvn spring-boot:run             # если установлен maven на хосте
```

## Тесты

```bash
./mvnw test                     # Windows: mvnw.cmd test
```

Требования: запущенный Docker и JDK 21. Тесты сами поднимают отдельный контейнер PostgreSQL на случайном порту,
база из `docker compose` не нужна и не затрагивается. Первый запуск дольше (скачивается образ `postgres:16-alpine`).

Тесты идут на настоящем PostgreSQL, а не на H2

Если JDK на машине нет, тесты можно прогнать в контейнере (нужны только Docker и Linux или macOS):

```bash
docker compose --profile test run --rm tests
```

Контейнер `tests` (профиль `test`) использует `maven:3.9-eclipse-temurin-21`, монтирует проект, `~/.m2` и сокет Docker,
чтобы Testcontainers мог поднять PostgreSQL рядом. Команда `docker compose up` этот сервис не запускает.
Первый прогон долгий: скачиваются зависимости Maven.

| Тест | Что проверяет |
|---|---|
| `MeetingApiTest` · пересечение встреч у менеджера | 10:30 при встрече 10:00-11:00 даёт 409 `MANAGER_BUSY`, 11:00-12:00 встык разрешена |
| `WorkingHoursPolicyTest` · граница рабочего дня | 10 кейсов: 08:59, 09:00, 17:00, 17:01, 17:30, 18:00, 23:30, суббота, воскресенье |
| `WorkingHoursPolicyTest` · UTC на входе | вход в UTC, правило считается по Asia/Almaty (UTC+5) |
| `MeetingApiTest` · организация | одна организация с двумя менеджерами на одно время: 409 `COMPANY_BUSY` |
| `MeetingApiTest` · звонок без встречи | `meeting_set` без блока `meeting`: 422 `MEETING_REQUIRED` |
| `MeetingApiTest` · перенос | `initialStartAt` не меняется, в `history` есть `created` и `rescheduled` |

Всего 6 тестов (15 запусков).

## Примеры запросов

Три равнозначных способа, выберите любой:

1. **`requests.http`** в корне репозитория (IntelliJ IDEA HTTP Client или VS Code с расширением REST Client).
   Запросы выполняются сверху вниз на свежей БД: id в них заданы по порядку создания (с 1).
   Последний раздел файла содержит примеры ошибок.
2. **Коллекция Bruno** в папке `bruno-collections/` (см. ниже).
3. **curl** (набор ниже).

То же в curl:

```bash
# 1. создать организацию
curl -X POST localhost:8080/companies -H 'Content-Type: application/json' \
  -d '{"name":"ТОО Ромашка","bin":"123456789012","phone":"+77011234567"}'

# 2. звонок: договорились о встрече (встреча создаётся в той же транзакции; 10:00 Алматы = 05:00Z)
curl -X POST localhost:8080/calls -H 'Content-Type: application/json' \
  -d '{"companyId":1,"result":"meeting_set","meeting":{"managerId":1,"startAt":"2026-11-25T05:00:00Z"},"comment":"договорились на среду"}'

# 3. перенести встречу на 11:00 Алматы
curl -X PATCH localhost:8080/meetings/1 -H 'Content-Type: application/json' \
  -d '{"startAt":"2026-11-25T06:00:00Z"}'

# 4. расписание менеджера на день
curl "localhost:8080/managers/1/schedule?date=2026-11-25"

# 5. отменить встречу
curl -X PATCH localhost:8080/meetings/1 -H 'Content-Type: application/json' \
  -d '{"status":"cancelled","reason":"клиент передумал"}'
```

### Коллекция Bruno

Папка `bruno-collections/`: запросы со встроенными проверками (статус ответа, сохранение `initialStartAt` после переноса).

1. Запустите приложение (`docker compose up -d`).
2. В Bruno: **Open Collection**, выберите папку `bruno-collections`.
3. Выберите окружение: **Environments**, затем **local**, убедитесь, что оно активно (`baseUrl` = `http://localhost:8080`).
4. Запустите все запросы разом: правый клик по коллекции, **Run**. Папки идут по порядку 1-5, запросы внутри связаны
   переменными (`companyId`, `company2Id`, `meetingId`), поэтому порядок важен.

Даты `day`, `errDay`, `weekend` лежат в окружении (`2026-11-23` и `2026-11-24` это понедельник и вторник,
`2026-11-21` суббота), БИН при каждом запуске случайный. **Повторный запуск на той же базе** даст 409, потому что слоты
уже заняты: пересоздайте базу (`docker compose down -v`, затем `docker compose up -d db` или `docker compose up --build`)
либо поменяйте `day` и `errDay` на другие рабочие дни.

## API


| Метод | Путь | Тело / параметры | Успех |
|---|---|---|---|
| POST | `/companies` | `{name, bin (12 цифр), phone}` | 201 |
| GET | `/companies` | `?status=&page=0&size=10` (size 1..100) | 200, `{items, page, size, totalElements, totalPages}` |
| POST | `/calls` | `{companyId, result, meeting?: {managerId, startAt}, comment?}` | 201 |
| POST | `/meetings` | `{companyId, managerId, startAt}` | 201 |
| PATCH | `/meetings/{id}` | `{startAt}` (перенос) или `{status:"cancelled", reason?}` (отмена) | 200 |
| GET | `/meetings/{id}` | | 200, встреча с `initialStartAt` и `history` |
| GET | `/managers/{id}/schedule` | `?date=2026-11-25` (календарный день по Алматы) | 200 |

Статусы организации: `new`, `no_answer`, `refused`, `meeting_set`. Результат звонка меняет статус организации.

Ошибки возвращаются в формате `ProblemDetail` с полем `code`:

```json
{"status": 409, "code": "MANAGER_BUSY", "detail": "..."}
```

| HTTP | `code` | Когда |
|---|---|---|
| 400 | `VALIDATION_ERROR` | неверное тело или параметры (БИН не из 12 цифр, неверный `status`, формат даты) |
| 404 | `NOT_FOUND` | организация, менеджер или встреча не найдены |
| 409 | `MANAGER_BUSY` | у менеджера есть пересекающаяся встреча |
| 409 | `COMPANY_BUSY` | у организации есть пересекающаяся активная встреча (даже с другим менеджером) |
| 409 | `BIN_ALREADY_EXISTS` | организация с таким БИН уже есть |
| 409 | `MEETING_NOT_ACTIVE` | встреча уже отменена |
| 422 | `OUTSIDE_WORKING_HOURS` | встреча не помещается в пн-пт 09:00-18:00 по Алматы |
| 422 | `MEETING_REQUIRED` | `meeting_set` без блока `meeting` |
| 422 | `MEETING_NOT_ALLOWED` | блок `meeting` передан для `no_answer` или `refused` |

## Что сделано

- Все эндпоинты из задания и дополнительный `GET /meetings/{id}` (показывает историю встречи).
- Схема в миграциях Flyway, защита от пересечений на уровне БД (в том числе при одновременных запросах).
- Запуск одной командой
- 6 автотестов на PostgreSQL в Testcontainers
- Примеры запросов: `requests.http`, коллекция Bruno и curl в этом файле.
- Защита от одновременного назначения одного слота: блокировка строк и ограничения БД (см. `REFLECTION.md`).

## Что не сделано и принятые допущения

- Менеджеров нельзя создать через API: три менеджера создаются миграцией `V2`.
- При отмене встречи статус организации возвращается в `new`, даже если у неё есть другая активная встреча.
- Нет защиты от повторной отправки одного и того же запроса (идемпотентности).
- Перенос встречи не берёт блокировку строк: его защищает проверка и ограничения БД.
