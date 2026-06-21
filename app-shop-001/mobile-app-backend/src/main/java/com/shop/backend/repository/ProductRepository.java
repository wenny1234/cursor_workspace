package com.shop.backend.repository;

import com.shop.backend.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findByNameContaining(String name);
    Product save(Product product);
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();
    List<Product> findInStock();
    List<Product> findOutOfStock();
}