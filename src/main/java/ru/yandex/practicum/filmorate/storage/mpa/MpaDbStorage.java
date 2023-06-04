package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public Mpa getFilmFilmId(long filmId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from mpa where id in (select MPA_ID from films_mpa where film_id = ?)", filmId);
        if (rs.next()) {
            return new Mpa(
                    rs.getLong("id"),
                    rs.getString("name"));
        } else {
            throw new NotFoundException("Id genres no found");
        }
    }

    @Override
    public Mpa get(long mpaId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", mpaId);
        if (rs.next()) {
            return new Mpa(
                    rs.getLong("id"),
                    rs.getString("name"));
        } else {
            throw new NotFoundException("Id genres no found");
        }
    }

    @Override
    public List<Mpa> getAll() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from mpa ");
        while (rs.next()) {
            Mpa mpa = new Mpa(
                    rs.getLong("id"),
                    rs.getString("name")
            );
            mpas.add(mpa);
        }
        return mpas;
    }
}
