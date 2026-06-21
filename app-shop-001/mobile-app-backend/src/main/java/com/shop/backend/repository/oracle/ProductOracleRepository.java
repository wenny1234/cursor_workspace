package com.shop.backend.repository.oracle;

import com.shop.backend.mapper.ProductMapper;
import com.shop.backend.model.Product;
import com.shop.backend.repository.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("oracle")
public class ProductOracleRepository implements ProductRepository {

    private final ProductMapper productMapper;

    public ProductOracleRepository(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productMapper.findByCategory(category);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productMapper.findByNameContaining(name);
    }

    @Override
    public Product save(Product product) {
        if (product.getId() != null && productMapper.findById(product.getId()).isPresent()) {
            productMapper.update(product);
        } else {
            productMapper.insert(product);
        }
        return product;
    }

    @Override
    public void deleteById(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productMapper.existsById(id) > 0;
    }

    @Override
    public long count() {
        return productMapper.count();
    }

    @Override
    public List<Product> findInStock() {
        return productMapper.findInStock();
    }

    @Override
    public List<Product> findOutOfStock() {
        return productMapper.findOutOfStock();
    }
}
