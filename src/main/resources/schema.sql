create table if not exists films
(
    id          INTEGER generated by default as identity primary key,
    genres      INTEGER,
    name        character,
    description character,
    releaseDate timestamp,
    duration    INTEGER,
    rate        INTEGER
);

create table if not exists genres
(
    id          INTEGER NOT NULL AUTO_INCREMENT,
    genres_name varchar NOT NULL,
    CONSTRAINT "GENRE_PK"
        PRIMARY KEY (ID)
);


create table if not exists mpa
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    constraint MPA_PK primary key (id)
);

create table if not exists users
(
    id       INTEGER generated by default as identity primary key,
    email    character(100) NOT NULL,
    login    character(100) NOT NULL,
    name     character(100),
    birthday timestamp
);

create table if not exists users_friend
(
    user_id   INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id),
    constraint PK_USERS_FRIEND
        primary key (user_id, friend_id)
);

create table if not exists film_likes
(
    user_id INTEGER NOT NULL REFERENCES users (id),
    film_id INTEGER NOT NULL REFERENCES films (id)
);

create table if not exists films_genres
(
    FILM_ID  INTEGER not null REFERENCES films (id),
    GENRE_ID INTEGER not null references genres (id)
);

create table if not exists films_mpa
(
    film_id INTEGER not null REFERENCES films (id),
    mpa_id  INTEGER not null references mpa (id)
);