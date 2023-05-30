package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum Mpa {

    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC17(5, "NC-17");

    private final int id;

    private final String name;

    @JsonCreator
    public static ru.yandex.practicum.filmorate.model.Mpa forValues(@JsonProperty("id") int id) {
        for (ru.yandex.practicum.filmorate.model.Mpa mpa : ru.yandex.practicum.filmorate.model.Mpa.values()) {
            if (mpa.id == id) {
                return mpa;
            }
        }
        throw new FilmNotFoundException(id);
    }
}
