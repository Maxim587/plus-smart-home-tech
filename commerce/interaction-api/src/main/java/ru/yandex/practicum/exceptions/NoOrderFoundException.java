package ru.yandex.practicum.exceptions;

public class NoOrderFoundException extends CommonBadRequestException {
    public NoOrderFoundException(String message) {
        super(message);
    }
}
