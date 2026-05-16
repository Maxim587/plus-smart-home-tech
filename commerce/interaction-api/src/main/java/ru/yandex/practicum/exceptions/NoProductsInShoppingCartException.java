package ru.yandex.practicum.exceptions;

public class NoProductsInShoppingCartException extends CommonBadRequestException {
    public NoProductsInShoppingCartException(String message) {
        super(message);
    }
}
