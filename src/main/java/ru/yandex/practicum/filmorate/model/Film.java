package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private long id;
    @NotNull
    @NotBlank
    private String name;

    private String description;
    private LocalDate releaseDate;
    @Positive
    private long duration;

    private List<Genre> genres;

    private Mpa mpa;

    private Integer rate;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && duration == film.duration && Objects.equals(name, film.name) && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate) && Objects.equals(genres, film.genres) && Objects.equals(mpa, film.mpa) && Objects.equals(rate, film.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, genres, mpa, rate);
    }
}
