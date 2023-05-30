package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import javax.validation.Valid;
import java.util.LinkedHashSet;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping
    public LinkedHashSet<Genre> findAll() {
        log.info("GET genres");
        return genreDbStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getUserById(@Valid @PathVariable("id") Integer id) {
        log.info("GET genre by id");
        return genreDbStorage.getGenresById(id);
    }
}
