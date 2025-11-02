# Используем базовый образ Python
FROM python:3.8-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы приложения в контейнер
COPY . /app

# Устанавливаем зависимости
RUN pip install --no-cache-dir -r requirements.txt

# Определяем команду для запуска приложения
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "5001"]
