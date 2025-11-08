package com.ecommerce.service;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product create(Product p);
    Product update(Long id, Product p);
    void delete(Long id);
    Page<Product> list(String category, Pageable pageable);
    Product findById(Long id);
}
