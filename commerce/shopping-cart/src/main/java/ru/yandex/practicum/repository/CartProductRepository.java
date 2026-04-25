package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.ProductInCart;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<ProductInCart, UUID> {
    List<ProductInCart> findAllByShoppingCart(ShoppingCart shoppingCart);

    Optional<ProductInCart> findByProductIdAndShoppingCart(UUID productId, ShoppingCart shoppingCart);

    void deleteByShoppingCartAndProductIdIn(ShoppingCart shoppingCart, Collection<UUID> productIds);
}

