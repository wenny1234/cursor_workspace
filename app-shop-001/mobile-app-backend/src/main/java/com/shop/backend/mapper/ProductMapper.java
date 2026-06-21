package com.shop.backend.mapper;

import com.shop.backend.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    Optional<Product> findById(@Param("id") Long id);

    List<Product> findAll();

    List<Product> findByCategory(@Param("category") String category);

    List<Product> findByNameContaining(@Param("name") String name);

    List<Product> findInStock();

    List<Product> findOutOfStock();

    int insert(Product product);

    int update(Product product);

    int deleteById(@Param("id") Long id);

    int deleteAll();

    int insertWithId(Product product);

    Long selectMaxId();

    long count();

    int existsById(@Param("id") Long id);
}
