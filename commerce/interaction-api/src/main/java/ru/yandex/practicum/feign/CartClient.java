package ru.yandex.practicum.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.fallback.CartClientFallback;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "shopping-cart", fallback = CartClientFallback.class)
public interface CartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto addProductToShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    void deactivateCurrentShoppingCart(@RequestParam String username);

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto changeProductQuantity(@RequestParam String username, @Valid @RequestBody ChangeProductQuantityRequest request);

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    ShoppingCartDto removeFromShoppingCart(@RequestParam String username, @RequestBody Set<UUID> uuids);

}

