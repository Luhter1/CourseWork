-- enums
CREATE TYPE art2art_user_role_enum AS ENUM (
    'ROLE_ARTIST', 
    'ROLE_EXPERT', 
    'ROLE_RESIDENCE_ADMIN', 
    'ROLE_SUPERADMIN'
);

CREATE TYPE art2art_art_direction_enum AS ENUM (
    'PAINTING',
    'SCULPTURE',
    'PERFORMANCE',
    'MULTIMEDIA',
    'DIGITAL_ART',
    'PHOTO',
    'OTHER'
);

CREATE TYPE art2art_achievements_type_enum AS ENUM (
    'EDUCATION',
    'EXHIBITION',
    'PUBLICATION',
    'AWARD',
    'AUTO'
);

CREATE TYPE art2art_media_type_enum AS ENUM (
    'IMAGE',
    'VIDEO'
);

CREATE TYPE art2art_application_request_status AS ENUM (
    'SENT', 
    'REVIEWED',
    'APPROVED', 
    'RESERVE',
    'REJECTED',
    'CONFIRMED', 
    'DECLINED_BY_ARTIST'
);

-- таблица пользователей
CREATE TABLE art2art_users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) UNIQUE NOT NULL CHECK (email <> ''),
    name            TEXT NOT NULL CHECK (name <> ''),
    surname         TEXT NOT NULL CHECK (surname <> ''),
    password_hash   VARCHAR(128) NOT NULL,
    role            art2art_user_role_enum NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

-- таблица профиля художника
CREATE TABLE art2art_artist_details (
    id          BIGSERIAL PRIMARY KEY,
    -- профиль художника - расширение записи пользователя. Если пользователь удаляется, то и данные профиля
    user_id     BIGINT UNIQUE NOT NULL REFERENCES art2art_users(id) ON DELETE CASCADE,
    biography   TEXT,
    location    VARCHAR(255),
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP DEFAULT now()
);

-- таблица резиденций
CREATE TABLE art2art_residence_details (
    id              BIGSERIAL PRIMARY KEY,
    -- удаление администратора резиденции не должно автоматически удалять саму резиденцию
    user_id         BIGINT UNIQUE NOT NULL REFERENCES art2art_users(id) ON DELETE RESTRICT,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    contacts        JSONB,
    location        VARCHAR(255),
    is_published    BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

-- таблица заявок на валидацию резиденции
CREATE TABLE art2art_validation_requests (
    id               BIGSERIAL PRIMARY KEY,
    -- если удаляют резиденцию, то все её заявки на валидацию теряют смысл
    residence_id     BIGINT NOT NULL REFERENCES art2art_residence_details(id) ON DELETE CASCADE,
    status           VARCHAR(50) NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
    comment          TEXT,
    submitted_at     TIMESTAMP,
    processed_at     TIMESTAMP,
    updated_at       TIMESTAMP,
    created_at       TIMESTAMP DEFAULT now()
);

-- таблица программ
CREATE TABLE art2art_programs (
    id                          BIGSERIAL PRIMARY KEY,
    -- программа не может существовать без резиденции
    residence_id                BIGINT NOT NULL REFERENCES art2art_residence_details(id) ON DELETE CASCADE,
    title                       VARCHAR(255) NOT NULL,
    description                 TEXT,
    goals                       JSONB,
    conditions                  JSONB,
    deadline_apply              DATE NOT NULL,
    deadline_review             DATE NOT NULL,
    deadline_notify             DATE NOT NULL,
    duration_days               INT CHECK (duration_days >= 0),
    budget_quota                INT CHECK (budget_quota >= 0),
    people_quota                INT CHECK (people_quota >= 0),
    futher_actions_sent_at      TIMESTAMP,
    is_published                BOOLEAN NOT NULL,
    created_at                  TIMESTAMP DEFAULT now(),
    updated_at                  TIMESTAMP DEFAULT now()
);

-- таблица назначения экспертов
CREATE TABLE art2art_program_experts (
    id                              BIGSERIAL PRIMARY KEY,
    -- если программа удалена, то все назначения экспертов должны быть удалены.
    program_id                      BIGINT NOT NULL REFERENCES art2art_programs(id) ON DELETE CASCADE,
    -- TODO: возможно надо добавить ограничение на добавление только с ролью ROLE_EXPERT
    -- нельзя удалять эксперта, если он назначен на действующие программы
    user_id                         BIGINT NOT NULL REFERENCES art2art_users(id) ON DELETE RESTRICT,
    assigned_at                     TIMESTAMP DEFAULT now(),
    created_at                      TIMESTAMP DEFAULT now(),
    -- требование, чтобы эксперт мог быть назначен на программу только 1 раз
    UNIQUE(program_id, user_id)
);

-- таблица заявок художников
CREATE TABLE art2art_application_requests (
    id               BIGSERIAL PRIMARY KEY,
    -- без программы заявки не имеют смысла
    program_id       BIGINT NOT NULL REFERENCES art2art_programs(id) ON DELETE CASCADE,
    -- TODO: возможно надо добавить ограничение на добавление только с ролью ROLE_ARTIST
    -- если художник участвовал в программе, его нельзя удалять
    artist_id        BIGINT NOT NULL REFERENCES art2art_users(id) ON DELETE RESTRICT,
    status           art2art_application_request_status NOT NULL,
    submitted_at     TIMESTAMP DEFAULT now(),
    created_at       TIMESTAMP DEFAULT now(),
    -- требование, чтобы художник мог подать заявку только 1 раз
    UNIQUE(artist_id, program_id)
);

-- таблица оценок заявок ходожников экспертами
CREATE TABLE art2art_application_evaluations (
    id              BIGSERIAL PRIMARY KEY,
    -- оценка без заявки бессмыслена
    application_id  BIGINT NOT NULL REFERENCES art2art_application_requests(id) ON DELETE CASCADE,
    -- эксперт удалит аккаунт, оценки не должны исчезнуть
    expert_id       BIGINT NOT NULL REFERENCES art2art_program_experts(id) ON DELETE RESTRICT,
    score           INT CHECK (score >= 0 AND score <= 100),
    comment         TEXT,
    created_at      TIMESTAMP DEFAULT now(),
    -- требование, чтобы эксперт мог оценить заявку только 1 раз
    UNIQUE(application_id, expert_id)
);

-- таблица портфолио художников
CREATE TABLE art2art_portfolio_works (
    id               BIGSERIAL PRIMARY KEY,
    -- работы из портфолио бессмыслены без художника
    artist_id        BIGINT NOT NULL REFERENCES art2art_artist_details(id) ON DELETE CASCADE,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    link             TEXT,
    art_direction    art2art_art_direction_enum NOT NULL,
    date             DATE CHECK (date > '1950-01-01' AND date <= now()),
    created_at       TIMESTAMP DEFAULT now(),
    updated_at       TIMESTAMP DEFAULT now()
);

-- таблица с данными работ из портфолио, которые хранятся в MinIO
CREATE TABLE art2art_media (
    id              BIGSERIAL PRIMARY KEY,
    -- если удалены работы, то и медиа тоже надо
    work_id         BIGINT NOT NULL REFERENCES art2art_portfolio_works(id) ON DELETE CASCADE,
    uri             TEXT NOT NULL,
    media_type      art2art_media_type_enum NOT NULL,
    file_size       BIGINT,
    created_at      TIMESTAMP DEFAULT now()
);

-- таблица с достижениями художников
CREATE TABLE art2art_achievements (
    id              BIGSERIAL PRIMARY KEY,
    -- достижения без художника бессмыслены
    artist_id       BIGINT NOT NULL REFERENCES art2art_artist_details(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    link            TEXT,
    type            art2art_achievements_type_enum NOT NULL,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

-- таблица с отзывами художников о резиденциях
CREATE TABLE art2art_reviews (
    id           BIGSERIAL PRIMARY KEY,
    -- отзывы без программы ненужны
    program_id   BIGINT NOT NULL REFERENCES art2art_programs(id) ON DELETE CASCADE,
    -- сохраняем исторические данные
    artist_id    BIGINT NOT NULL REFERENCES art2art_users(id) ON DELETE RESTRICT,
    score        INT CHECK (score >= 1 AND score <= 10),
    comment      TEXT,
    created_at   TIMESTAMP DEFAULT now(),

    UNIQUE(program_id, artist_id)
);

-- таблица с уведомлениями пользователям
CREATE TABLE art2art_notifications (
    id            BIGSERIAL PRIMARY KEY,
    -- уведомления для пользователя без пользователя ненужны
    user_id       BIGINT NOT NULL REFERENCES art2art_users(id) ON DELETE CASCADE,
    message       TEXT NOT NULL,
    link          TEXT,
    category      VARCHAR(50) CHECK (category IN ('system', 'invite', 'review', 'status')),
    read_at       TIMESTAMP,
    created_at    TIMESTAMP DEFAULT now()
);

-- таблица со статистикой программ
CREATE TABLE art2art_program_stats (
    id                  BIGSERIAL PRIMARY KEY,
    -- статистика существует только вместе с программой
    program_id          BIGINT NOT NULL UNIQUE REFERENCES art2art_programs(id) ON DELETE CASCADE,
    views_count         INT DEFAULT 0,
    applications_count  INT DEFAULT 0,
    created_at          TIMESTAMP DEFAULT now(),
    updated_at          TIMESTAMP DEFAULT now()
);

-- таблица со статистикой резиденций
CREATE TABLE art2art_residence_stats (
    id              BIGSERIAL PRIMARY KEY,
    -- статистика существует только вместе с резиденцией
    residence_id    BIGINT NOT NULL UNIQUE REFERENCES art2art_residence_details(id) ON DELETE CASCADE,
    views_count     INT DEFAULT 0,
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

-- таблица для просмотров программ
CREATE TABLE art2art_program_views_log (
    id BIGSERIAL PRIMARY KEY,
    -- просмотры без программы бессмыслены
    program_id BIGINT REFERENCES art2art_programs(id) ON DELETE CASCADE,
    viewed_at TIMESTAMP DEFAULT now()
);

-- таблица для просмотров резиденций
CREATE TABLE art2art_residence_views_log (
    id BIGSERIAL PRIMARY KEY,
    -- просмотры без резиденции бессмыслены
    residence_id BIGINT REFERENCES art2art_residence_details(id) ON DELETE CASCADE,
    viewed_at TIMESTAMP DEFAULT now()
);
