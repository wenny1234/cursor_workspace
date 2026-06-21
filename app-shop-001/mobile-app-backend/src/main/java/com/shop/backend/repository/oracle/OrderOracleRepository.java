package com.shop.backend.repository.oracle;

import com.shop.backend.mapper.OrderItemMapper;
import com.shop.backend.mapper.OrderMapper;
import com.shop.backend.model.Order;
import com.shop.backend.model.OrderItem;
import com.shop.backend.repository.OrderRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("oracle")
public class OrderOracleRepository implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderOracleRepository(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderMapper.findById(id);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return orderItemMapper.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public Order save(Order order, List<OrderItem> items) {
        if (order.getId() != null && orderMapper.findById(order.getId()).isPresent()) {
            orderMapper.update(order);
            orderItemMapper.deleteByOrderId(order.getId());
        } else {
            orderMapper.insert(order);
        }

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        order.setItems(items);
        return order;
    }
}
