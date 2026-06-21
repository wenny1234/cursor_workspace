package com.shop.admin.mapper;

import com.shop.admin.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    Optional<Product> findById(@Param("id") Long id);

    List<Product> search(@Param("keyword") String keyword, @Param("includeInactive") boolean includeInactive);

    int insert(Product product);

    int update(Product product);

    int deactivate(@Param("id") Long id, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    long countActive();
}
