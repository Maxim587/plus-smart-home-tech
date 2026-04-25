package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> uuids);

    void deactivateCurrentShoppingCart(String username);
}
