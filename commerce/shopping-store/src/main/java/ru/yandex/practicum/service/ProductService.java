package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.QuantityState;
import ru.yandex.practicum.dto.store.ProductDto;

import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProducts(ProductCategory category, Pageable page);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean setProductQuantityState(UUID productId, QuantityState quantityState);

    ProductDto getProductById(UUID productId);

    boolean removeProductFromStore(UUID productId);
}
