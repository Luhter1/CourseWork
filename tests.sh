# Сервер запущен на порту 15123.
# В БД существуют тестовые данные: пользователь-художник с id=1 (username=artist1, роль ARTIST), ArtistDetails с id=1, достижения с id=1 (title="Test Achievement").
# ARTIST_TOKEN: валидный JWT для artist1 (получить: curl -X POST http://localhost:15123/api/auth/signin -H "Content-Type: application/json" -d '{"username":"artist1","password":"pass"}' → извлечь accessToken).
# USER_TOKEN: JWT для обычного пользователя (роль USER).
# INVALID_TOKEN: неверный токен, напр. eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.
# Пагинация по умолчанию: size=20, sort=id,asc.

## отсутсвующий профиль получить 
# eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9TVVBFUkFETUlOIiwiaWQiOjEsInN1YiI6InRlc3RAbWFpbC5ydSIsImlhdCI6MTc2ODE1NDI1NiwiZXhwIjoxNzk5NzExMjA4fQ.JByNhFiRqzEey75mzXTCSZNH8DpGPxbe3FqwdgVS-Yw

# eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9BUlRJU1QiLCJpZCI6Miwic3ViIjoidGVzdF91c2VyQG1haWwucnUiLCJpYXQiOjE3NjgxNTQzMTgsImV4cCI6MTc5OTcxMTI3MH0.5DfPAjfnhAGSfj9U57083Wsfmc8bdT23vzauF9Bu50U
curl -i -X POST http://localhost:15123/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
        "email": "test@mail.ru",
        "name": "myName",
        "surname": "mySurname",
        "password": "mySecurePassword",
        "role": "ROLE_ARTIST"
      }'

curl -i -X POST http://localhost:15123/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
        "email": "test_user@mail.ru",
        "password": "mySecurePassword"
      }'

curl -X POST "http://localhost:15123/api/artists/me" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "biography": "test_user@mail.ru",
        "location": "mySecurePassword"
      }'

curl -X GET "http://localhost:15123/api/artists/me" -H "Authorization: Bearer $ARTIST_TOKEN"

curl -X GET "http://localhost:15123/api/artists/1"

curl -X PUT "http://localhost:15123/api/artists/me" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "biography": null,
        "location": "111"
      }'

curl -X POST "http://localhost:15123/api/artists/me" \
  -H "Authorization: Bearer $SUPERADMIN_TOKER" \
  -H "Content-Type: application/json" \
  -d '{
        "biography": "test_user@mail.ru",
        "location": "mySecurePassword"
      }'

# Позитивный: Получение достижений существующего художника с пагинацией.
curl -X GET "http://localhost:15123/api/artists/2/achievements" -v

# Позитивный: Пагинация и сортировка.
curl -X GET "http://localhost:15123/api/artists/1/achievements?page=1&size=10&sort=id,asc" -v

# Позитивный: Получение своих достижений.
curl -X GET "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

# Негативный: Без токена.
curl -X GET "http://localhost:15123/api/artists/me/achievements" -v

# Негативный: USER роль.
curl -X GET "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $SUPERADMIN_TOKER" -v

# Позитивный: Создание достижения.
curl -X POST "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EDUCATION",
    "title": "New Achievement",
    "description": "Test desc",
    "link": "https://example.com"
  }' -v

# Негативный: Валидация (пустой title).
curl -X POST "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "title": "", "description": "desc"}' -v

# Негативный: Неверный URL.
curl -X POST "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "title": "Test", "link": "invalid-url"}' -v

# Негативный: Без ARTIST роли. (аналогично 401/403)
curl -X POST "http://localhost:15123/api/artists/me/achievements" \
  -H "Authorization: Bearer $SUPERADMIN_TOKER" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "link": "invalid-url"}' -v

# Позитивный: Обновление.
curl -X PUT "http://localhost:15123/api/artists/me/achievements/1" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "null",
    "description": "Updated desc",
    "link": "https://example.com/new"
  }' -v

# Негативный: Несуществующий achievement ID.
curl -X PUT "http://localhost:15123/api/artists/me/achievements/999" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

# Негативный: Валидация title слишком длинный.
curl -X PUT "http://localhost:15123/api/artists/me/achievements/1" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "'$(printf 'A%.0s' {1..300})'"}' -v

# Позитивный: Удаление.
curl -X DELETE "http://localhost:15123/api/artists/me/achievements/1" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

# Негативный: Несуществующий ID.
curl -X DELETE "http://localhost:15123/api/artists/me/achievements/999" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

# Позитивный: Получение своего профиля.
curl -X GET "http://localhost:15123/api/artists/me" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

# Позитивный: Обновление профиля.
curl -X PUT "http://localhost:15123/api/artists/me" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Name",
    "surname": "New Surname",
    "biography": "Bio text",
    "location": "Moscow"
  }' -v

# работы в портфолио
curl -X GET "http://localhost:15123/api/artists/2/works" -v

curl -X GET "http://localhost:15123/api/artists/me/works" \
  -H "Authorization: Bearer $ARTIST_TOKEN" -v

curl -X GET "http://localhost:15123/api/artists/me/works" \
  -H "Authorization: Bearer $SUPERADMIN_TOKER" -v

curl -X POST "http://localhost:15123/api/artists/me/works" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "EXHIBITION", "description": "123213", "artDirection": "PAINTING", "date": "2007-12-03", "link": "https://example.com/new"}' -v

curl -X PUT "http://localhost:15123/api/artists/me/works/1" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"date": "2020-12-03", "link": null}' -v

# файлы
curl -X POST "http://localhost:15123/api/artists/me/works/1/media" \
  -H "Authorization: Bearer $ARTIST_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F "files=@component_diagram.drawio.png"

# профили резиденции
curl -X POST http://localhost:15123/api/residences/me \
  -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Резиденция Альфа",
    "description": "Современная резиденция для стартапов",
    "location": "Москва, ул. Примерная, 1",
    "contacts": {
      "email": "info@alpha-residence.ru",
      "phone": "+7-999-123-45-67",
      "website": "https://alpha-residence.ru"
    }
  }'

# получение своего профиля резиденции
curl -X GET http://localhost:15123/api/residences/me \
  -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN"

# обновление своего профиля резиденции
curl -X PUT http://localhost:15123/api/residences/me \
-H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN" \
-H "Content-Type: application/json" \
-d '{
  "title": "Резиденция Альфа (обновлённая 2)",
  "description": null,
  "location": "Москва, Инновационный парк",
  "contacts": {
    "email": "contact@alpha-residence.ru",
    "telegram": "@alpha_residence"
  }
}'


# получение статуса валидации своего профиля резиденции
curl -X GET http://localhost:15123/api/residences/me/validation-status   -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN"

# профиль резиденции по ID
curl -X GET http://localhost:15123/api/residences/1

# список опубликованных профилей резиденции
curl -X GET http://localhost:15123/api/residences

# статистика просмотров резиденции
curl -X GET "http://localhost:15123/api/residences/me/stats" \
  -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN"


# список всех валидаций профилей резиденции (только для SUPERADMIN)
curl -X GET "http://localhost:15123/api/admin/validation-requests" \
  -H "Authorization: Bearer $ROLE_SUPERADMIN"

# получение конкретной валидации профиля резиденции по ID (только для SUPERADMIN)
curl -X GET "http://localhost:15123/api/admin/validation-requests/1" \
  -H "Authorization: Bearer $ROLE_SUPERADMIN"

curl -X POST "http://localhost:15123/api/admin/validation-requests/1/approve" \
  -H "Authorization: Bearer $ROLE_SUPERADMIN"

curl -X POST "http://localhost:15123/api/admin/validation-requests/1/reject" \
  -H "Authorization: Bearer $ROLE_SUPERADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "comment": "Недостаточно информации для подтверждения"
  }'

# программы резиденции
curl -X GET "http://localhost:15123/api/residences/me/programs?page=0&size=10&sort=createdAt,asc" \
  -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN"

curl -X POST "http://localhost:15123/api/residences/me/programs" \
  -H "Authorization: Bearer $ROLE_RESIDENCE_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Новая программа",
    "description": "Описание новой программы",
    "goals": {
      "innovation": true,
      "export": false
    },
    "conditions": {
      "stage": "seed",
      "country": "RU"
    },
    "deadlineApply": "2025-03-01",
    "deadlineReview": "2025-03-10",
    "deadlineNotify": "2025-03-15",
    "durationDays": 60,
    "budgetQuota": 500000,
    "peopleQuota": 15
  }'


# cообщения
curl -X GET "http://localhost:15123/api/notifications" \
  -H "Authorization: Bearer $TOKEN"

curl -X GET "http://localhost:15123/api/notifications/unread-count" \
  -H "Authorization: Bearer $TOKEN"

curl -X GET "http://localhost:15123/api/notifications/1/read" \
  -H "Authorization: Bearer $TOKEN"

curl -X GET "http://localhost:15123/api/notifications/read-all" \
  -H "Authorization: Bearer $TOKEN"