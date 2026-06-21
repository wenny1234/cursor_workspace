package com.shop.backend.mapper;

import com.shop.backend.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    int insert(OrderItem item);

    int deleteByOrderId(@Param("orderId") Long orderId);
}
