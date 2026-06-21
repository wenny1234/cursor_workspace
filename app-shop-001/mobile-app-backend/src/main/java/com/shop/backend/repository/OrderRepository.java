package com.shop.backend.repository;

import com.shop.backend.model.Order;
import com.shop.backend.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    List<OrderItem> findItemsByOrderId(Long orderId);

    Order save(Order order, List<OrderItem> items);
}
