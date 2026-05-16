package ru.yandex.practicum.exceptions;

public class CommonBadRequestException extends RuntimeException {
    public CommonBadRequestException(String message) {
        super(message);
    }
}
