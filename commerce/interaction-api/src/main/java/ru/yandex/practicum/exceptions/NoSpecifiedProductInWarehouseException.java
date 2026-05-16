package ru.yandex.practicum.exceptions;

public class NoSpecifiedProductInWarehouseException extends CommonBadRequestException {
    public NoSpecifiedProductInWarehouseException(String message) {
        super(message);
    }
}
