package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public LinkedHashSet<Mpa> getAllMpa() {

        LinkedHashSet<Mpa> mpas = new LinkedHashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("""
        select * 
        from mpa """);
        while (rs.next()) {
            Mpa mpa = Mpa.forValues(rs.getInt("mpa_id"));
            mpas.add(mpa);
        }
        return mpas;
    }
}
