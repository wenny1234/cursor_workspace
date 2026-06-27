package com.shop.wms.mapper;

import com.shop.wms.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {

    Optional<Product> findById(@Param("id") Long id);

    List<Product> search(@Param("keyword") String keyword, @Param("includeInactive") boolean includeInactive);

    int insert(Product product);

    int update(Product product);

    int deactivate(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);

    long countActive();
}
