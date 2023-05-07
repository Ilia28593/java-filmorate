package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ThrowException extends Throwable {
    @ExceptionHandler
    public ErrorResponse handleUnauthorizedUser(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}
