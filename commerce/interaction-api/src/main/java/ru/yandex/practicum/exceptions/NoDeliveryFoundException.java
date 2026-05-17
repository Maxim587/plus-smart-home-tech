package ru.yandex.practicum.exceptions;

public class NoDeliveryFoundException extends NotFoundException {
    public NoDeliveryFoundException(String message) {
        super(message);
    }
}
