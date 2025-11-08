package com.ecommerce.service.impl;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import com.ecommerce.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product create(Product p){ return productRepository.save(p); }

    @Override
    @Transactional
    public Product update(Long id, Product p){
        Product existing = findById(id);
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setPrice(p.getPrice());
        existing.setStock(p.getStock());
        existing.setCategory(p.getCategory());
        existing.setImageUrl(p.getImageUrl());
        existing.setRating(p.getRating());
        return productRepository.save(existing);
    }

    @Override
    public void delete(Long id){
        Product p = findById(id);
        productRepository.delete(p);
    }

    @Override
    public Page<Product> list(String category, Pageable pageable){
        if (category == null || category.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        } else {
            return productRepository.findByCategoryIgnoreCase(category, pageable);
        }
    }

    @Override
    public Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
}
