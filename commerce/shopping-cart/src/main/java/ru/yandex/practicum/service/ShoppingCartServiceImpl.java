package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exceptions.NoProductsInShoppingCartException;
import ru.yandex.practicum.exceptions.NotAuthorizedUserException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ProductInCart;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartProductRepository;
import ru.yandex.practicum.repository.CartRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;
    private final CartProductRepository cartProductRepository;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUser(username);
        ShoppingCart cart = cartRepository.findByUsernameAndIsActive(username, true);
        List<ProductInCart> products = cartProductRepository.findAllByShoppingCart(cart);
        return cartMapper.mapShoppingCartToShoppingCartDto(cart, products);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Список товаров для добавления в корзину не может быть пустым");
        }
        checkUser(username);
        ShoppingCart cart = getNewOrExistingUsersShoppingCart(username);
        ShoppingCartDto dto = cartMapper.mapShoppingCartToShoppingCartDto(cart, null);
        dto.setProducts(products);
        warehouseClient.checkProductQuantityEnoughForShoppingCart(dto);
        List<ProductInCart> productsInCart = cartMapper.mapProductsMapToProductsInCartList(products, cart);
        cartProductRepository.saveAll(productsInCart);
        return dto;
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUser(username);
        ShoppingCart cart = getUsersActiveShoppingCart(username);
        ProductInCart product = getProductByIdAndCart(request, cart);
        product.setQuantity(request.getNewQuantity());
        List<ProductInCart> products = cartProductRepository.findAllByShoppingCart(cart);
        return cartMapper.mapShoppingCartToShoppingCartDto(cart, products);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, Set<UUID> uuids) {
        checkUser(username);
        ShoppingCart cart = getUsersActiveShoppingCart(username);
        cartProductRepository.deleteByShoppingCartAndProductIdIn(cart, uuids);
        List<ProductInCart> products = cartProductRepository.findAllByShoppingCart(cart);
        return cartMapper.mapShoppingCartToShoppingCartDto(cart, products);
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        checkUser(username);
        ShoppingCart cart = getUsersActiveShoppingCart(username);
        if (cart.isActive()) {
            cart.setActive(false);
        }
    }

    private void checkUser(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не заполнено");
        }
    }

    private ShoppingCart getUsersActiveShoppingCart(String username) {
        ShoppingCart cart = cartRepository.findByUsernameAndIsActive(username, true);
        if (cart == null) {
            throw new NotFoundException("Корзина пользователя " + username + " не найдена");
        }
        return cart;
    }

    @Transactional
    protected ShoppingCart getNewOrExistingUsersShoppingCart(String username) {
        ShoppingCart cart = cartRepository.findByUsernameAndIsActive(username, true);
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setUsername(username);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    private ProductInCart getProductByIdAndCart(ChangeProductQuantityRequest newQtyRequest, ShoppingCart cart) {
        return cartProductRepository.findByProductIdAndShoppingCart(newQtyRequest.getProductId(), cart)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Товар с id " + newQtyRequest.getProductId() + " не содержится в корзине"));
    }
}
