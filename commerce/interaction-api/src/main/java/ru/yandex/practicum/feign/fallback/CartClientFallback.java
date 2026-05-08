package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.feign.CartClient;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class CartClientFallback implements CartClient {
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.error("Fallback response: shopping cart service is unavailable");
        return null;
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        log.error("Fallback response: shopping cart service is unavailable");
        return null;
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        log.error("Fallback response: shopping cart service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.error("Fallback response: shopping cart service is unavailable");
        return null;
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> uuids) {
        log.error("Fallback response: shopping cart service is unavailable");
        return null;
    }
}
