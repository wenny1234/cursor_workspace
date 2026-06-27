package com.shop.wms.mapper;

import com.shop.wms.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}
