-- Создание таблицы для расписания уроков
CREATE TABLE IF NOT EXISTS schedule (
    id TEXT PRIMARY KEY,
    day_of_week TEXT NOT NULL,
    lesson_number INTEGER NOT NULL,
    subject TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_schedule_day ON schedule(day_of_week);
CREATE INDEX IF NOT EXISTS idx_schedule_lesson ON schedule(lesson_number);

-- Включение Row Level Security
ALTER TABLE schedule ENABLE ROW LEVEL SECURITY;

-- Политики доступа
CREATE POLICY "Allow public read access to schedule"
ON schedule
FOR SELECT
USING (true);

CREATE POLICY "Allow public insert access to schedule"
ON schedule
FOR INSERT
WITH CHECK (true);

CREATE POLICY "Allow public update access to schedule"
ON schedule
FOR UPDATE
USING (true)
WITH CHECK (true);

CREATE POLICY "Allow public delete access to schedule"
ON schedule
FOR DELETE
USING (true);

-- Создание таблицы для времени звонков
CREATE TABLE IF NOT EXISTS bell_schedule (
    id TEXT PRIMARY KEY,
    day_type TEXT NOT NULL, -- 'normal' или 'wednesday'
    lesson_number INTEGER NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_bell_day_type ON bell_schedule(day_type);
CREATE INDEX IF NOT EXISTS idx_bell_lesson ON bell_schedule(lesson_number);

-- Включение Row Level Security
ALTER TABLE bell_schedule ENABLE ROW LEVEL SECURITY;

-- Политики доступа
CREATE POLICY "Allow public read access to bell_schedule"
ON bell_schedule
FOR SELECT
USING (true);

CREATE POLICY "Allow public insert access to bell_schedule"
ON bell_schedule
FOR INSERT
WITH CHECK (true);

CREATE POLICY "Allow public update access to bell_schedule"
ON bell_schedule
FOR UPDATE
USING (true)
WITH CHECK (true);

CREATE POLICY "Allow public delete access to bell_schedule"
ON bell_schedule
FOR DELETE
USING (true);

-- Триггеры для автоматического обновления updated_at
CREATE TRIGGER update_schedule_updated_at
    BEFORE UPDATE ON schedule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bell_schedule_updated_at
    BEFORE UPDATE ON bell_schedule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
