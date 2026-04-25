package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exceptions.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];


    @Override
    @Transactional
    public void addNewProductInWarehouse(NewProductInWarehouseRequest dto) {
        if (warehouseRepository.existsById(dto.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар id:" + dto.getProductId() + " уже зарегистрирован на складе");
        }

        WarehouseProduct product = warehouseMapper.mapNewProductInWarehouseRequestToWarehouseProduct(dto, 0);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional
    public void addProductQuantityToWarehouse(AddProductToWarehouseRequest dto) {
        WarehouseProduct product = warehouseRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар id:" + dto.getProductId() + " не найден на складе"));

        product.setQuantity(product.getQuantity() + dto.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return new AddressDto(CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS, CURRENT_ADDRESS);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto dto) {
        Map<UUID, Integer> cartProducts = dto.getProducts();
        List<WarehouseProduct> products = warehouseRepository.findAllByProductIdIn(dto.getProducts().keySet());
        List<UUID> lackOfAmountProductIds = new ArrayList<>();

        double deliveryWeight = 0;
        double deliveryVolume = 0;
        boolean fragile = false;

        for (WarehouseProduct warehouseProduct : products) {
            int requestedQty = cartProducts.get(warehouseProduct.getProductId());
            if (requestedQty > warehouseProduct.getQuantity()) {
                lackOfAmountProductIds.add(warehouseProduct.getProductId());
                continue;
            }
            deliveryWeight += warehouseProduct.getWeight();
            deliveryVolume += warehouseProduct.getHeight() * warehouseProduct.getWidth() * warehouseProduct.getDepth();
            fragile = warehouseProduct.isFragile();
        }

        if (!lackOfAmountProductIds.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Товары отсутствуют на складе: " + lackOfAmountProductIds);
        }

        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }
}
