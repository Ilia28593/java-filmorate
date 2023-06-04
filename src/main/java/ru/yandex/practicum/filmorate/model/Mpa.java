package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mpa implements Serializable {
    private Long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpa mpa = (Mpa) o;
        return Objects.equals(id, mpa.id) && Objects.equals(name, mpa.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
