package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.model.ProductInCart;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CartMapper {

    @Mapping(target = "shoppingCartId", source = "shoppingCart.id")
    ShoppingCartDto mapShoppingCartToShoppingCartDto(ShoppingCart shoppingCart, List<ProductInCart> products);

    default Map<UUID, Integer> mapProductsInCartToProductsMap(List<ProductInCart> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyMap();
        }
        return products.stream().collect(Collectors.toMap(ProductInCart::getProductId, ProductInCart::getQuantity));
    }

    default List<ProductInCart> mapProductsMapToProductsInCartList(Map<UUID, Integer> products, ShoppingCart cart) {
        return products.entrySet().stream()
                .map(entry ->
                        ProductInCart.builder()
                                .productId(entry.getKey())
                                .quantity(entry.getValue())
                                .shoppingCart(cart)
                                .build())
                .toList();
    }
}