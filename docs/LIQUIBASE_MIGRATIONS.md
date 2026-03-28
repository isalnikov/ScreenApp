# Liquibase Configuration for Digital Signage Project

## Структура проекта

```
src/main/resources/db/
└── changelog/
    ├── db.changelog-master.xml    # Главный файл миграций
    ├── 001-initial-schema.xml     # Создание всех таблиц
    ├── 002-add-indexes.xml        # Добавление индексов
    ├── 003-add-triggers.xml       # Создание триггеров
    └── 004-seed-data.xml          # Начальные данные
```

## Конфигурация подключения

### application.properties (Spring Boot)

```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/digital_signage
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Liquibase configuration
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.contexts=dev
spring.liquibase.default-schema=public
```

### application.yml (альтернативный вариант)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/digital_signage
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
    contexts: dev
    default-schema: public
```

## Зависимости Maven

```xml
<dependencies>
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Liquibase -->
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>
</dependencies>
```

## Зависимости Gradle

```gradle
dependencies {
    // PostgreSQL Driver
    runtimeOnly 'org.postgresql:postgresql'
    
    // Liquibase
    implementation 'org.liquibase:liquibase-core'
}
```

## Команды Liquibase CLI

### Установка Liquibase

Скачайте с официального сайта: https://www.liquibase.org/download

### Настройка liquibase.properties

```properties
changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml
url=jdbc:postgresql://localhost:5432/digital_signage
username=postgres
password=postgres
driver=org.postgresql.Driver
defaultSchemaName=public
logLevel=info
```

### Основные команды

```bash
# Проверить статус миграций
liquibase status

# Применить все ожидающие миграции
liquibase update

# Откатить последнюю миграцию
liquibase rollback --count=1

# Откатить до конкретного тега
liquibase rollback --tag=v1.0

# Создать тег для текущего состояния
liquibase tag v1.0

# Генерация changelog из существующей БД
liquibase generateChangelog

# Проверка SQL без выполнения (dry-run)
liquibase update --output-file=migration.sql

# Очистка базы данных (осторожно!)
liquibase dropAll
```

## Стратегия версионирования

### Нумерация файлов миграций

- `001-` - Инициализация схемы (создание таблиц)
- `002-` - Индексы
- `003-` - Триггеры и функции
- `004-` - Начальные данные (seed data)
- `005+` - Последующие изменения

### Правила создания миграций

1. **Каждое изменение в отдельном changeset**
   - Уникальный `id` и `author`
   - Осмысленный `comment`

2. **Идемпотентность**
   - Миграции должны выполняться многократно без побочных эффектов
   - Использовать `CREATE TABLE IF NOT EXISTS` когда применимо

3. **Откат изменений (rollback)**
   - Всегда указывать `<rollback>` для критических изменений
   - Тестировать откат перед deployment

4. **Контексты**
   - Использовать контексты для разделения dev/test/prod данных
   - Пример: `context="dev"` для тестовых данных

## Пример migration с rollback

```xml
<changeSet id="005-001" author="developer">
    <comment>Добавление нового поля в таблицу screens</comment>
    <addColumn tableName="screens">
        <column name="brightness_level" type="INTEGER" defaultValueNumeric="100"/>
    </addColumn>
    
    <rollback>
        <dropColumn tableName="screens" columnName="brightness_level"/>
    </rollback>
</changeSet>
```

## CI/CD Интеграция

### GitHub Actions пример

```yaml
name: Database Migrations

on:
  push:
    branches: [ main ]

jobs:
  migrate:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: digital_signage
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Run Liquibase migrations
        uses: liquibase/liquibase-github-action@v1
        with:
          command: update
          url: jdbc:postgresql://localhost:5432/digital_signage
          username: postgres
          password: postgres
          changelog-file: src/main/resources/db/changelog/db.changelog-master.xml
```

## Мониторинг и аудит

Liquibase автоматически создаёт таблицы:

- `DATABASECHANGELOG` - история выполненных миграций
- `DATABASECHANGELOGLOCK` - блокировка для предотвращения параллельного выполнения

### Проверка статуса

```sql
-- Просмотр истории миграций
SELECT * FROM databasechangelog ORDER BY dateexecuted DESC;

-- Проверка блокировки
SELECT * FROM databasechangeloglock;
```

## Рекомендации по безопасности

1. **Не хранить пароли в коде**
   - Использовать environment variables
   - Использовать secrets manager (Vault, AWS Secrets Manager)

2. **Разделение доступов**
   - Отдельный пользователь для миграций с правами DDL
   - Отдельный пользователь для приложения с правами DML

3. **Backup перед миграцией**
   ```bash
   pg_dump -U postgres digital_signage > backup_$(date +%Y%m%d_%H%M%S).sql
   ```

## Troubleshooting

### Частые проблемы

1. **Lock застрял**
   ```sql
   UPDATE databasechangeloglock SET LOCKED = FALSE, LOCKGRANTEDBY = NULL, LOCKEDBY = NULL WHERE ID = 1;
   ```

2. **Пропущенная миграция**
   ```bash
   liquibase changelog-sync
   ```

3. **Валидация checksum**
   ```bash
   liquibase clear-checksums
   ```
