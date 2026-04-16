-- Функция для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Триггер для автоматического обновления updated_at при изменении записи
CREATE TRIGGER update_homework_updated_at
    BEFORE UPDATE ON homework
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON FUNCTION update_updated_at_column() IS
'Автоматически обновляет поле updated_at при изменении записи';

COMMENT ON TRIGGER update_homework_updated_at ON homework IS
'Триггер для автоматического обновления времени изменения';
