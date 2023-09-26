# java-filmorate
## Схема базы данных

![Схема БД для java-filmorate](https://github.com/TanyaVyatkina/java-filmorate/blob/add-database/Database.png)

### Пример команд:
* Поиск всех фильмов:
  ```
  SELECT * FROM film;
  ```
* Найти пользователя с id = 1:
  ```
  SELECT * FROM user WHERE user_id = 1;
  ```
* Найти всех друзей пользователя с id = 1:
  ```
  SELECT * FROM user WHERE user_id IN (SELECT other_user_id FROM friendship WHERE user_id = 1);
  ```
* Найти 10 самых популярных фильмов:
  ```
  SELECT * FROM film ORDER BY likesCount DESC LIMIT 10;
  ```
  

