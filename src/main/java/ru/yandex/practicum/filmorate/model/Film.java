package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Film {

    private long id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 0, max = 200, message = "length no more 200")
    private String description;
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private List<Genre> genres;

    private Mpa mpa = Mpa.R;

    private Integer rate = null;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public List<Genre> getGenres() {
        return genres;
    }
}
