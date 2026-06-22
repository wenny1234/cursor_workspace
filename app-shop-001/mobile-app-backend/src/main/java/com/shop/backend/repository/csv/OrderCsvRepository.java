package com.shop.backend.repository.csv;

import com.shop.backend.model.Order;
import com.shop.backend.model.OrderItem;
import com.shop.backend.model.OrderStatus;
import com.shop.backend.repository.OrderRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("csv")
public class OrderCsvRepository implements OrderRepository {

    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final Map<Long, List<OrderItem>> itemsByOrderId = new HashMap<>();
    private final AtomicLong orderIdSeq = new AtomicLong(1);
    private final AtomicLong itemIdSeq = new AtomicLong(1);

    @Override
    public Optional<Order> findById(Long id) {
        Order order = orders.get(id);
        if (order == null) {
            return Optional.empty();
        }
        order.setItems(new ArrayList<>(itemsByOrderId.getOrDefault(id, List.of())));
        return Optional.of(order);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(o -> Objects.equals(o.getUserId(), userId))
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .peek(o -> o.setItems(new ArrayList<>(itemsByOrderId.getOrDefault(o.getId(), List.of()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .peek(o -> o.setItems(new ArrayList<>(itemsByOrderId.getOrDefault(o.getId(), List.of()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return new ArrayList<>(itemsByOrderId.getOrDefault(orderId, List.of()));
    }

    @Override
    public Order save(Order order, List<OrderItem> items) {
        if (order.getId() == null) {
            order.setId(orderIdSeq.getAndIncrement());
        }
        orders.put(order.getId(), order);

        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : items) {
            item.setId(itemIdSeq.getAndIncrement());
            item.setOrderId(order.getId());
            savedItems.add(item);
        }
        itemsByOrderId.put(order.getId(), savedItems);
        order.setItems(savedItems);
        return order;
    }
}
