package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.common.AddressDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.exceptions.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exceptions.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseOperation;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final OrderBookingRepository bookingRepository;
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
        return getBookedProductDto(cartProducts, products);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Integer> products) {
        List<WarehouseProduct> foundProducts = warehouseRepository.findAllByProductIdIn(products.keySet());
        List<WarehouseProduct> updatedProducts = updateProductsQuantity(products, foundProducts, WarehouseOperation.RETURN);
        warehouseRepository.saveAll(updatedProducts);
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        List<WarehouseProduct> foundProducts = warehouseRepository.findAllByProductIdIn(products.keySet());
        List<WarehouseProduct> updatedProducts = updateProductsQuantity(products, foundProducts, WarehouseOperation.ASSEMBLY);
        warehouseRepository.saveAll(updatedProducts);
        OrderBooking booking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        bookingRepository.save(booking);
        return getBookedProductDto(products, foundProducts);
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ c id " + request.getOrderId() + " не найден на складе"));
        booking.setDeliveryId(request.getDeliveryId());
    }

    private List<WarehouseProduct> updateProductsQuantity(Map<UUID, Integer> products,
                                                          List<WarehouseProduct> foundProducts,
                                                          WarehouseOperation operation) {
        List<WarehouseProduct> updatedProducts = new ArrayList<>();
        List<UUID> productsNotFound = new ArrayList<>();

        for (WarehouseProduct warehouseProduct : foundProducts) {
            Optional<Integer> extraQty = Optional.ofNullable(products.get(warehouseProduct.getProductId()));
            if (extraQty.isPresent()) {
                int newQty = getNewQty(operation, warehouseProduct, extraQty.get());
                warehouseProduct.setQuantity(newQty);
                updatedProducts.add(warehouseProduct);
            } else {
                productsNotFound.add(warehouseProduct.getProductId());
            }
        }

        if (!productsNotFound.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Товары не найдены в ассортименте магазина: " + productsNotFound);
        }
        return updatedProducts;
    }

    private int getNewQty(WarehouseOperation operation, WarehouseProduct warehouseProduct, Integer extraQty) {
        int newQty = switch (operation) {
            case RETURN -> warehouseProduct.getQuantity() + extraQty;
            case ASSEMBLY -> warehouseProduct.getQuantity() - extraQty;
        };
        if (newQty < 0) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException("Отсутствует необходимое количество товара с id: "
                                                                           + warehouseProduct.getProductId());
        }
        return newQty;
    }

    private BookedProductsDto getBookedProductDto(Map<UUID, Integer> requestedProducts,
                                                  List<WarehouseProduct> warehouseProducts) {
        Map<UUID, Integer> lackOfAmountProductIds = new HashMap<>();
        double deliveryWeight = 0;
        double deliveryVolume = 0;
        boolean fragile = false;

        for (WarehouseProduct warehouseProduct : warehouseProducts) {
            int requestedQty = requestedProducts.get(warehouseProduct.getProductId());
            if (requestedQty > warehouseProduct.getQuantity()) {
                lackOfAmountProductIds.put(warehouseProduct.getProductId(), requestedQty - warehouseProduct.getQuantity());
                continue;
            }
            deliveryWeight += warehouseProduct.getWeight() * requestedQty;
            deliveryVolume += warehouseProduct.getHeight() * warehouseProduct.getWidth() * warehouseProduct.getDepth() * requestedQty;
            if (warehouseProduct.isFragile()) {
                fragile = true;
            }
        }

        if (!lackOfAmountProductIds.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException("Товары отсутствуют на складе в нужном количестве: " + lackOfAmountProductIds);
        }

        return new BookedProductsDto(deliveryWeight, deliveryVolume, fragile);
    }
}
