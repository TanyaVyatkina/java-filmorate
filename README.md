# java-filmorate
## Схема базы данных

![Схема БД для java-filmorate](https://github.com/TanyaVyatkina/java-filmorate/blob/add-database/DB.png)

### Пример команд:
* Поиск всех фильмов:
  ```
  SELECT * FROM Film;
  ```
* Найти пользователя с id = 1:
  ```
  SELECT * FROM User WHERE user_id = 1;
  ```
* Найти всех друзей пользователя с id = 1:
  ```
  SELECT * FROM USER WHERE user_id IN (SELECT other_user_id FROM Friendship WHERE user_id = 1);
  ```
* Найти 10 самых популярных фильмов:
  ```
  SELECT * FROM Film ORDER BY likesCount DESC LIMIT 10;
  ```
  

