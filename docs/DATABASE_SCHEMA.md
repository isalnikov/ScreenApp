# Схема базы данных Digital Signage

## Таблицы

### 1. users (Пользователи системы)
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER', -- ADMIN, CONTENT_MANAGER, VIEWER
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 2. regions (Регионы)
```sql
CREATE TABLE regions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10) UNIQUE NOT NULL,
    parent_region_id BIGINT REFERENCES regions(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. screens (Экраны/Телевизоры)
```sql
CREATE TABLE screens (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    serial_number VARCHAR(50) UNIQUE NOT NULL,
    region_id BIGINT REFERENCES regions(id),
    location_address TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    screen_type VARCHAR(20) DEFAULT 'TV', -- TV, PROJECTOR, LED_WALL
    resolution_width INTEGER DEFAULT 1920,
    resolution_height INTEGER DEFAULT 1080,
    orientation VARCHAR(10) DEFAULT 'LANDSCAPE', -- LANDSCAPE, PORTRAIT
    status VARCHAR(20) DEFAULT 'OFFLINE', -- ONLINE, OFFLINE, ERROR, MAINTENANCE
    last_seen_at TIMESTAMP,
    firmware_version VARCHAR(20),
    ip_address INET,
    mac_address VARCHAR(17),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 4. content_types (Типы контента)
```sql
CREATE TABLE content_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- IMAGE, VIDEO, TEXT, HTML, STREAM
    mime_type_pattern VARCHAR(255)
);
```

### 5. content (Контент)
```sql
CREATE TABLE content (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    content_type_id INTEGER REFERENCES content_types(id),
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    duration_seconds INTEGER, -- Для видео и аудио
    text_content TEXT, -- Для текстового контента
    html_content TEXT, -- Для HTML контента
    thumbnail_path VARCHAR(500),
    url VARCHAR(500), -- Для потокового контента
    checksum VARCHAR(64), -- MD5/SHA256 для проверки целостности
    uploaded_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    metadata JSONB -- Дополнительные метаданные
);
```

### 6. playlists (Плейлисты)
```sql
CREATE TABLE playlists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    total_duration_seconds INTEGER DEFAULT 0,
    version INTEGER DEFAULT 1
);
```

### 7. playlist_items (Элементы плейлиста)
```sql
CREATE TABLE playlist_items (
    id BIGSERIAL PRIMARY KEY,
    playlist_id BIGINT REFERENCES playlists(id) ON DELETE CASCADE,
    content_id BIGINT REFERENCES content(id),
    position INTEGER NOT NULL,
    duration_seconds INTEGER, -- Переопределение длительности
    transition_type VARCHAR(20) DEFAULT 'NONE', -- FADE, SLIDE, NONE
    transition_duration INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(playlist_id, position)
);
```

### 8. schedules (Расписания)
```sql
CREATE TABLE schedules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    playlist_id BIGINT REFERENCES playlists(id),
    start_date DATE NOT NULL,
    end_date DATE,
    start_time TIME NOT NULL DEFAULT '00:00:00',
    end_time TIME NOT NULL DEFAULT '23:59:59',
    days_of_week VARCHAR(20), -- bitmask: 1234567 (Пн-Вс) или '*' для всех
    priority INTEGER DEFAULT 0,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 9. screen_schedules (Привязка расписаний к экранам)
```sql
CREATE TABLE screen_schedules (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT REFERENCES screens(id) ON DELETE CASCADE,
    schedule_id BIGINT REFERENCES schedules(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT REFERENCES users(id),
    UNIQUE(screen_id, schedule_id)
);
```

### 10. screen_groups (Группы экранов)
```sql
CREATE TABLE screen_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 11. screen_group_members (Члены групп экранов)
```sql
CREATE TABLE screen_group_members (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT REFERENCES screen_groups(id) ON DELETE CASCADE,
    screen_id BIGINT REFERENCES screens(id) ON DELETE CASCADE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, screen_id)
);
```

### 12. content_logs (Логи показа контента)
```sql
CREATE TABLE content_logs (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT REFERENCES screens(id),
    content_id BIGINT REFERENCES content(id),
    playlist_id BIGINT REFERENCES playlists(id),
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    status VARCHAR(20) DEFAULT 'PLAYED', -- PLAYED, ERROR, SKIPPED, INTERRUPTED
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 13. screen_stats (Статистика экранов)
```sql
CREATE TABLE screen_stats (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT REFERENCES screens(id),
    reported_at TIMESTAMP NOT NULL,
    cpu_usage DECIMAL(5, 2),
    memory_usage DECIMAL(5, 2),
    storage_free_gb INTEGER,
    network_upload_mbps DECIMAL(8, 2),
    network_download_mbps DECIMAL(8, 2),
    temperature DECIMAL(5, 2),
    uptime_seconds BIGINT,
    current_playlist_id BIGINT REFERENCES playlists(id),
    current_content_id BIGINT REFERENCES content(id),
    playback_status VARCHAR(20), -- PLAYING, PAUSED, STOPPED, BUFFERING
    additional_data JSONB
);
```

### 14. audit_log (Аудит действий)
```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    entity_type VARCHAR(50) NOT NULL, -- SCREEN, CONTENT, PLAYLIST, SCHEDULE
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 15. api_tokens (API токены для экранов)
```sql
CREATE TABLE api_tokens (
    id BIGSERIAL PRIMARY KEY,
    screen_id BIGINT REFERENCES screens(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) UNIQUE NOT NULL,
    description VARCHAR(200),
    expires_at TIMESTAMP,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

## Индексы

```sql
-- Индексы для производительности
CREATE INDEX idx_screens_region ON screens(region_id);
CREATE INDEX idx_screens_status ON screens(status);
CREATE INDEX idx_screens_last_seen ON screens(last_seen_at);
CREATE INDEX idx_content_type ON content(content_type_id);
CREATE INDEX idx_content_uploaded_by ON content(uploaded_by);
CREATE INDEX idx_playlist_items_playlist ON playlist_items(playlist_id);
CREATE INDEX idx_playlist_items_content ON playlist_items(content_id);
CREATE INDEX idx_schedules_playlist ON schedules(playlist_id);
CREATE INDEX idx_schedules_dates ON schedules(start_date, end_date);
CREATE INDEX idx_screen_schedules_screen ON screen_schedules(screen_id);
CREATE INDEX idx_screen_schedules_schedule ON screen_schedules(schedule_id);
CREATE INDEX idx_content_logs_screen ON content_logs(screen_id);
CREATE INDEX idx_content_logs_started_at ON content_logs(started_at);
CREATE INDEX idx_screen_stats_screen ON screen_stats(screen_id);
CREATE INDEX idx_screen_stats_reported_at ON screen_stats(reported_at);
CREATE INDEX idx_audit_log_user ON audit_log(user_id);
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
```

## Триггеры

```sql
-- Автоматическое обновление updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_screens_updated_at BEFORE UPDATE ON screens
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_content_updated_at BEFORE UPDATE ON content
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_playlists_updated_at BEFORE UPDATE ON playlists
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## Начальные данные

```sql
-- Типы контента
INSERT INTO content_types (name, mime_type_pattern) VALUES
('IMAGE', 'image/*'),
('VIDEO', 'video/*'),
('TEXT', 'text/plain'),
('HTML', 'text/html'),
('STREAM', 'application/x-mpegURL');

-- Администратор по умолчанию (пароль нужно захешировать)
INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@screenapp.com', '$2a$10$...', 'ADMIN');
```

## ER-диаграмма (описание связей)

```
users (1) ──────< content (M)
users (1) ──────< playlists (M)
users (1) ──────< schedules (M)
users (1) ──────< screen_groups (M)
users (1) ──────< audit_log (M)

regions (1) ────< screens (M)
regions (1) ────< regions (M) [иерархия]

content_types (1) ──< content (M)

playlists (1) ────< playlist_items (M)
content (1) ──────< playlist_items (M)

playlists (1) ────< schedules (M)

screens (1) ──────< screen_schedules (M)
schedules (1) ────< screen_schedules (M)

screen_groups (1) ──< screen_group_members (M)
screens (1) ────────< screen_group_members (M)

screens (1) ──────< content_logs (M)
content (1) ──────< content_logs (M)
playlists (1) ────< content_logs (M)

screens (1) ──────< screen_stats (M)
screens (1) ──────< api_tokens (M)
```
