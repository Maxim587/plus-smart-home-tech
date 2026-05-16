package ru.yandex.practicum.exceptions;

public class ProductInShoppingCartLowQuantityInWarehouseException extends CommonBadRequestException {
    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
}
