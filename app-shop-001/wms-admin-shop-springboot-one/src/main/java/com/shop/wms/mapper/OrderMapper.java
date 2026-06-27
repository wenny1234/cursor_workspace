package com.shop.wms.mapper;

import com.shop.wms.model.Order;
import com.shop.wms.model.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    Optional<Order> findById(@Param("id") Long id);

    List<Order> findByStatus(@Param("status") OrderStatus status);

    List<Order> findRecent(@Param("limit") int limit);

    int update(Order order);

    long countAll();

    long countByStatus(@Param("status") String status);

    BigDecimal sumTotalAmount();
}
