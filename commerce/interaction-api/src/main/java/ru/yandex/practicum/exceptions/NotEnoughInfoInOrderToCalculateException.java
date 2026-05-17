package ru.yandex.practicum.exceptions;

public class NotEnoughInfoInOrderToCalculateException extends CommonBadRequestException {
    public NotEnoughInfoInOrderToCalculateException(String message) {
        super(message);
    }
}
