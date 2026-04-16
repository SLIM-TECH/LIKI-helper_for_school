-- Создание таблицы для домашних заданий
CREATE TABLE IF NOT EXISTS homework (
    id TEXT PRIMARY KEY,
    subject TEXT NOT NULL,
    description TEXT NOT NULL,
    due_date TEXT,
    day_of_week TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TEXT NOT NULL,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Создание индексов для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_homework_day ON homework(day_of_week);
CREATE INDEX IF NOT EXISTS idx_homework_subject ON homework(subject);
CREATE INDEX IF NOT EXISTS idx_homework_completed ON homework(is_completed);

-- Комментарии к таблице
COMMENT ON TABLE homework IS 'Таблица для хранения домашних заданий';
COMMENT ON COLUMN homework.id IS 'Уникальный идентификатор задания (UUID)';
COMMENT ON COLUMN homework.subject IS 'Название предмета';
COMMENT ON COLUMN homework.description IS 'Описание домашнего задания';
COMMENT ON COLUMN homework.due_date IS 'Срок сдачи в формате дд.мм.гггг';
COMMENT ON COLUMN homework.day_of_week IS 'День недели (Понедельник, Вторник и т.д.)';
COMMENT ON COLUMN homework.is_completed IS 'Флаг выполнения задания';
COMMENT ON COLUMN homework.created_at IS 'Время создания записи';
COMMENT ON COLUMN homework.updated_at IS 'Время последнего обновления';
