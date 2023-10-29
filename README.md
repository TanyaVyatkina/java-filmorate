# java-filmorate
## Схема базы данных

![Схема БД для java-filmorate](https://github.com/TanyaVyatkina/java-filmorate/blob/develop/Database.png)

### Пример команд:
* Поиск всех фильмов:
  ```
  SELECT * FROM films;
  ```
* Найти пользователя с id = 1:
  ```
  SELECT * FROM users WHERE user_id = 1;
  ```
* Найти всех друзей пользователя с id = 1:
  ```
  SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = 1);
  ```
* Найти 10 самых популярных фильмов:
  ```
  SELECT * FROM films ORDER BY likes_count DESC LIMIT 10;
  ```
  

