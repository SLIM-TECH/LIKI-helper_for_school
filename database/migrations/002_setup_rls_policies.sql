-- Включение Row Level Security для таблицы homework
ALTER TABLE homework ENABLE ROW LEVEL SECURITY;

-- Политика: разрешить всем читать домашние задания
CREATE POLICY "Allow public read access to homework"
ON homework
FOR SELECT
USING (true);

-- Политика: разрешить всем вставлять новые задания (для администратора)
CREATE POLICY "Allow public insert access to homework"
ON homework
FOR INSERT
WITH CHECK (true);

-- Политика: разрешить всем обновлять задания (для отметки выполненных)
CREATE POLICY "Allow public update access to homework"
ON homework
FOR UPDATE
USING (true)
WITH CHECK (true);

-- Политика: разрешить всем удалять задания (для администратора)
CREATE POLICY "Allow public delete access to homework"
ON homework
FOR DELETE
USING (true);

-- Комментарии к политикам
COMMENT ON POLICY "Allow public read access to homework" ON homework IS
'Разрешает всем пользователям читать домашние задания';

COMMENT ON POLICY "Allow public insert access to homework" ON homework IS
'Разрешает администратору добавлять новые домашние задания';

COMMENT ON POLICY "Allow public update access to homework" ON homework IS
'Разрешает пользователям отмечать задания как выполненные';

COMMENT ON POLICY "Allow public delete access to homework" ON homework IS
'Разрешает администратору удалять домашние задания';
