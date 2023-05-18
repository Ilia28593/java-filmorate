package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friends {
    private Long friendOne;
    private Long friendTwo;
    private Status status;
}
