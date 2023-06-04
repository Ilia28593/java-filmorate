package ru.yandex.practicum.filmorate.storage.film;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final AtomicInteger id = new AtomicInteger();

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from films");
        while (rs.next()) {
            Film newFilm = new Film(
                    rs.getString("name").trim(),
                    rs.getString("description").trim(),
                    rs.getDate("releaseDate").toLocalDate(),
                    rs.getInt("duration")
            );
            newFilm.setId(rs.getInt("id"));
            newFilm.setMpa(mpaDbStorage.getFilmFilmId(rs.getInt("id")));
            List<Genre> listGenres = getGenres(Long.valueOf(rs.getInt("id")));
            newFilm.setGenres(listGenres);
            films.add(newFilm);
        }
        return films;
    }

    @Transactional
    @Override
    public Film filmCreate(@NotNull Film film) {
        id.getAndIncrement();
        film.setId(Long.parseLong(String.valueOf(id)));
        String sqlQuery = "insert into films(id, name, description, releaseDate, duration, rate) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate());
        if (film.getMpa() != null) {
            String sqlQuery2 = "insert into films_mpa (film_id, MPA_ID) values (?,?)";
            jdbcTemplate.update(sqlQuery2, film.getId(), film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(f -> {
                String sqlQuery2 = "insert into films_genres (FILM_ID, GENRE_ID) values (?,?)";
                jdbcTemplate.update(sqlQuery2, film.getId(), f.getId());
            });
        }
        return filmById(film.getId());
    }

    @Override
    public Film filmUpdate(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, releaseDate = ?, duration = ?, rate = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getId());
        String sqlUpdate = "update films_mpa set MPA_ID = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlUpdate, film.getMpa().getId(),film.getId());
        String sqlQueryDel = "delete from films_genres where FILM_ID = ?";
        jdbcTemplate.update(sqlQueryDel, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream().distinct().forEach(f -> {
                String sqlQuery2 = "insert into films_genres (FILM_ID, GENRE_ID) values (?,?)";
                jdbcTemplate.update(sqlQuery2, film.getId(), f.getId());
            });
        }
        return filmById(film.getId());
    }

    @Override
    public Film filmById(Long id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        if (rs.next()) {
            Film newFilm = new Film(rs.getString("name").trim(), rs.getString("description").trim(),
                    rs.getDate("releaseDate").toLocalDate(), rs.getInt("duration"));
            newFilm.setId(rs.getInt("id"));
            newFilm.setMpa(mpaDbStorage.getFilmFilmId(rs.getInt("id")));
            newFilm.setGenres(getGenres(id));
            return newFilm;
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    public List<Genre> getGenres(Long id) {
        List<Genre> listGenres = new ArrayList<>();
        String sqlQuery2 = "select film_id, genre_id, genres_name from films_genres left join genres on films_genres.genre_id = genres.id where film_id = ?";
        SqlRowSet rs2 = jdbcTemplate.queryForRowSet(sqlQuery2, id);
        while (rs2.next()) {
            Genre newGenre = new Genre(rs2.getInt("genre_id"), rs2.getString("GENRES_NAME"));
            listGenres.add(newGenre);
        }
        return listGenres;
    }
}


