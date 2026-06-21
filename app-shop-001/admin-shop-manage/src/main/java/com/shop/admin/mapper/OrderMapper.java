package com.shop.admin.mapper;

import com.shop.admin.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    Optional<Order> findById(@Param("id") Long id);

    List<Order> findRecent(@Param("limit") int limit);

    long countAll();

    long countByStatus(@Param("status") String status);

    BigDecimal sumTotalAmount();
}
