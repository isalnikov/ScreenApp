# ScreenApp - Система управления цифровыми вывесками (Digital Signage)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Описание проекта

Система предназначена для показа текстового, графического и видео контента на витринах-экранах по всей стране. Масштаб системы: порядка 5000 экранов (телевизоров).

## Технологический стек

### Backend
- **Язык**: Java 21
- **Фреймворк**: Spring Boot 3.0
- **База данных**: PostgreSQL 15+
- **Кэш**: Redis
- **Build tool**: Maven/Gradle

### Frontend (ARM Контента)
- **Технологии**: HTML5, CSS3, JavaScript (ES6+)
- **API взаимодействие**: REST API

## Архитектура

Проект включает в себя:
- **Backend сервер** с REST API для управления контентом и экранами
- **Веб-интерфейс** (ARM Контента) для администрирования системы
- **Клиентское приложение** для экранов (получение и воспроизведение контента)
- **База данных** для хранения метаданных и конфигураций

Подробная документация:
- [Архитектура системы](docs/ARCHITECTURE.md)
- [Схема базы данных](docs/DATABASE_SCHEMA.md)
- [Объектная модель](docs/OBJECT_MODEL.md)

## Структура базы данных

Основные сущности:
- **Screen** - информация об экранах (~5000 единиц)
- **Content** - медиа-контент (изображения, видео, текст, HTML)
- **Playlist** - списки воспроизведения
- **Schedule** - расписания показа контента
- **Region** - региональная группировка экранов
- **User** - пользователи системы с ролями
- **ScreenGroup** - группы экранов для массового управления
- **ContentLog** - логи воспроизведения контента
- **ScreenStat** - телеметрия и статистика экранов

## Функциональность

### Управление контентом
- Загрузка и хранение медиафайлов (изображения, видео)
- Создание текстового и HTML контента
- Организация контента по типам и категориям

### Управление плейлистами
- Создание списков воспроизведения
- Настройка переходов между элементами
- Автоматический расчёт длительности

### Планирование
- Гибкое расписание показа (по дням недели, времени, датам)
- Приоритеты расписаний
- Массовое назначение на экраны и группы

### Мониторинг
- Отслеживание статуса экранов (online/offline/error)
- Статистика воспроизведения
- Телеметрия (CPU, память, сеть, температура)

### Безопасность
- Ролевая модель (ADMIN, CONTENT_MANAGER, VIEWER)
- JWT аутентификация
- API токены для экранов
- Аудит действий пользователей

## API

Публичное REST API предоставляет endpoints для:
- Управления пользователями и аутентификации
- CRUD операции с контентом
- Управления плейлистами и расписаниями
- Мониторинга экранов
- Получения статистики и логов

Документация API доступна после запуска приложения по адресу: `/swagger-ui.html`

## Требования

- Java 21 или выше
- PostgreSQL 15 или выше
- Redis 7 или выше
- Maven 3.8+ или Gradle 8+

## Установка и запуск

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd screenapp
```

### 2. Настройка базы данных
```sql
CREATE DATABASE screenapp;
CREATE USER screenapp_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE screenapp TO screenapp_user;
```

### 3. Конфигурация приложения
Создайте файл `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/screenapp
    username: screenapp_user
    password: your_password
  redis:
    host: localhost
    port: 6379

server:
  port: 8080
```

### 4. Сборка и запуск
```bash
# Сборка проекта
mvn clean package

# Запуск приложения
java -jar target/screenapp-0.0.1-SNAPSHOT.jar
```

### 5. Запуск frontend (ARM Контента)
Frontend размещается в `src/main/resources/static` или на отдельном веб-сервере.

## Покрытие тестами

Цель проекта - не менее 90% покрытия кода тестами.

Запуск тестов:
```bash
mvn test
```

Отчёт о покрытии:
```bash
mvn jacoco:report
```

Отчёт доступен в `target/site/jacoco/index.html`

## Разработка

### Структура проекта
```
screenapp/
├── src/
│   ├── main/
│   │   ├── java/com/screenapp/
│   │   │   ├── config/          # Конфигурация
│   │   │   ├── controller/      # REST контроллеры
│   │   │   ├── service/         # Бизнес-логика
│   │   │   ├── repository/      # Репозитории
│   │   │   ├── model/           # Entity классы
│   │   │   ├── dto/             # DTO классы
│   │   │   └── exception/       # Исключения
│   │   └── resources/
│   │       ├── static/          # Frontend файлы
│   │       └── application.yml  # Конфигурация
│   └── test/
│       └── java/com/screenapp/  # Тесты
├── docs/                        # Документация
└── README.md
```

## Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменений (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## Лицензия

MIT License - см. файл [LICENSE](LICENSE)

## Контакты

По вопросам обращайтесь: admin@screenapp.com
