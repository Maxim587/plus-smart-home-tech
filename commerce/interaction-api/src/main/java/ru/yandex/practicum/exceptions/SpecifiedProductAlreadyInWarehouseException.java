package ru.yandex.practicum.exceptions;

public class SpecifiedProductAlreadyInWarehouseException extends CommonBadRequestException {
    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}
