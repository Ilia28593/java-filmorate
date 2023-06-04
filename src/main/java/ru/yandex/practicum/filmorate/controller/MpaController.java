package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class MpaController {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaController(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @GetMapping(value = "/mpa")
    public List<Mpa> findAll() {
        log.info("Получен запрос GET genres");
        return mpaDbStorage.getAll();
    }

    @GetMapping(value = "/mpa/{id}")
    public Mpa getMap(@Valid @PathVariable("id") long id){
        log.info("Получен запрос GET genres");
        return mpaDbStorage.get(id);
    }
}
