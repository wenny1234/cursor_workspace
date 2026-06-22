package com.shop.backend.mapper;

import com.shop.backend.model.Order;
import com.shop.backend.model.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    Optional<Order> findById(@Param("id") Long id);

    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

    List<Order> findByUserId(@Param("userId") Long userId);

    List<Order> findByStatus(@Param("status") OrderStatus status);

    int insert(Order order);

    int update(Order order);
}
