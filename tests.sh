# Сервер запущен на порту 8080.
# В БД существуют тестовые данные: пользователь-художник с id=1 (username=artist1, роль ARTIST), ArtistDetails с id=1, достижения с id=1 (title="Test Achievement").
# ARTIST_TOKEN: валидный JWT для artist1 (получить: curl -X POST http://localhost:8080/api/auth/signin -H "Content-Type: application/json" -d '{"username":"artist1","password":"pass"}' → извлечь accessToken).
# USER_TOKEN: JWT для обычного пользователя (роль USER).
# INVALID_TOKEN: неверный токен, напр. eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.
# Пагинация по умолчанию: size=20, sort=id,asc.

# Позитивный: Получение достижений существующего художника с пагинацией.
curl -X GET "http://localhost:8080/api/artists/1/achievements?page=0&size=5&sort=title,desc" -v

# Позитивный: Пагинация и сортировка.
curl -X GET "http://localhost:8080/api/artists/1/achievements?page=1&size=10&sort=id,asc" -v

# Негативный: Несуществующий ID художника.
curl -X GET "http://localhost:8080/api/artists/999/achievements" -v

# Негативный: Отсутствующий ArtistDetails.
curl -X GET "http://localhost:8080/api/artists/2/achievements" -v  # id=2 без ArtistDetails

# Позитивный: Получение своих достижений.
curl -X GET "http://localhost:8080/api/artists/me/achievements?page=0&size=10" \
  -H "Authorization: Bearer ARTIST_TOKEN" -v

# Негативный: Без токена.
curl -X GET "http://localhost:8080/api/artists/me/achievements" -v

# Негативный: USER роль.
curl -X GET "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer USER_TOKEN" -v

# Негативный: INVALID_TOKEN.
curl -X GET "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer INVALID_TOKEN" -v

# Позитивный: Создание достижения.
curl -X POST "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EXHIBITION",
    "title": "New Achievement",
    "description": "Test desc",
    "link": "https://example.com"
  }' -v

# Негативный: Валидация (пустой title).
curl -X POST "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "title": "", "description": "desc"}' -v

# Негативный: Неверный URL.
curl -X POST "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "title": "Test", "link": "invalid-url"}' -v

# Негативный: Без ARTIST роли. (аналогично 401/403)
curl -X POST "http://localhost:8080/api/artists/me/achievements" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type": "EXHIBITION", "link": "invalid-url"}' -v

# Позитивный: Обновление.
curl -X PUT "http://localhost:8080/api/artists/me/achievements/1" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "description": "Updated desc",
    "link": "https://example.com/new"
  }' -v

# Негативный: Несуществующий achievement ID.
curl -X PUT "http://localhost:8080/api/artists/me/achievements/999" \
  -H "Authorization: Bearer ARTIST_TOKEN" -v

# Негативный: Валидация title слишком длинный.
curl -X PUT "http://localhost:8080/api/artists/me/achievements/1" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "'$(printf 'A%.0s' {1..300})'"}' -v

# Позитивный: Удаление.
curl -X DELETE "http://localhost:8080/api/artists/me/achievements/1" \
  -H "Authorization: Bearer ARTIST_TOKEN" -v

# Негативный: Несуществующий ID.
curl -X DELETE "http://localhost:8080/api/artists/me/achievements/999" \
  -H "Authorization: Bearer ARTIST_TOKEN" -v

# Позитивный: Получение своего профиля.
curl -X GET "http://localhost:8080/api/artists/me" \
  -H "Authorization: Bearer ARTIST_TOKEN" -v

# Позитивный: Обновление профиля.
curl -X PUT "http://localhost:8080/api/artists/me" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Name",
    "surname": "New Surname",
    "biography": "Bio text",
    "location": "Moscow"
  }' -v

# Негативный: Пустое имя.
curl -X PUT "http://localhost:8080/api/artists/me" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "", "surname": "Surname"}' -v

# Негативный: Биография слишком длинная.
curl -X PUT "http://localhost:8080/api/artists/me" \
  -H "Authorization: Bearer ARTIST_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "Name", "surname": "Surname", "biography": "'$(printf 'A%.0s' {1..3000})'"}' -v
