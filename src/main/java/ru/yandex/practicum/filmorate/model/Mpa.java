package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mpa implements Serializable {
    private Long id;
    private String name;
}
