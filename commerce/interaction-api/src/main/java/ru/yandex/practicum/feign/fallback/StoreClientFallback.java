package ru.yandex.practicum.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.enums.ProductCategory;
import ru.yandex.practicum.dto.enums.QuantityState;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.exceptions.InternalServerErrorException;
import ru.yandex.practicum.feign.StoreClient;

import java.util.UUID;

@Slf4j
@Component
public class StoreClientFallback implements StoreClient {
    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.error("Fallback response: shopping store service is unavailable");
        return null;
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.error("Fallback response: shopping store service is unavailable");
        return null;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.error("Fallback response: shopping store service is unavailable");
        return null;
    }

    @Override
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        log.error("Fallback response: shopping store service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.error("Fallback response: shopping store service is unavailable");
        throw new InternalServerErrorException("Сервис временно недоступен");
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.error("Fallback response: shopping store service is unavailable");
        return null;
    }
}
