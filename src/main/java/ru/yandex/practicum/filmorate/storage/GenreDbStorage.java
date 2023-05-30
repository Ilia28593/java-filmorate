package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public LinkedHashSet<Genre> getAllGenres() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("""
                select *
                 from genres """);
        while (rs.next()) {
            Genre genre = new Genre(
                    rs.getInt("id"),
                    rs.getString("genres_name")
            );
            genres.add(genre);
        }
        return genres;
    }

    public Genre getGenresById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("""
                select *
                 from genres
                  where id = ?""", id);
        if (rs.next()) {
            Genre genre = new Genre(
                    rs.getInt("id"),
                    rs.getString("genres_name")
            );
            return genre;
        } else {
            throw new NotFoundException("Id genres no found");
        }
    }
}
