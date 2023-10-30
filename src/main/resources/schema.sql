DROP TABLE IF EXISTS friendship, likes, film_genre, films, directors cascade constraints;
DROP TABLE IF EXISTS users, genres, ratings, directors, film_director, films, film_genre, likes, friendship;

CREATE TABLE public.users(
        user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        email varchar(255) NOT NULL,
        login  varchar(255) NOT NULL,
        name varchar(255),
        birthday date
);

CREATE TABLE public.genres(
        genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(255) NOT NULL
);

CREATE TABLE public.ratings(
        rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        rating_name varchar(255) NOT NULL
);

CREATE TABLE public.directors(
        director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        director_name varchar(255) NOT NULL
);

CREATE TABLE public.films(
        film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(255) NOT NULL,
        description  varchar(1024),
        release_date date NOT NULL,
        duration integer NOT NULL,
        likes_count integer,
        rating_id integer NOT NULL REFERENCES ratings(rating_id) 
);


CREATE TABLE public.film_genre(
		film_id INTEGER NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
        genre_id INTEGER NOT NULL REFERENCES genres(genre_id)
);

CREATE TABLE public.film_director(
		film_id INTEGER NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
        director_id INTEGER NOT NULL REFERENCES directors(director_id) ON DELETE CASCADE
);

CREATE TABLE public.likes(
		user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        film_id INTEGER NOT NULL REFERENCES films(film_id) ON DELETE CASCADE
);

CREATE TABLE public.friendship(
		user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        friend_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);