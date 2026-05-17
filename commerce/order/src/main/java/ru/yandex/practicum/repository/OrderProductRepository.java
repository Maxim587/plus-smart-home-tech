package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.ProductInOrder;

import java.util.List;
import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<ProductInOrder, UUID> {
    List<ProductInOrder> findAllByOrder(Order order);

    List<ProductInOrder> findAllByOrder_Username(String username);
}

