package com.ecommerce.service;

import com.ecommerce.entity.OrderEntity;
import com.ecommerce.dto.CheckoutRequest;
import java.util.List;

public interface OrderService {
    OrderEntity checkout(CheckoutRequest req);
    List<OrderEntity> findByUser(Long userId);
    OrderEntity findById(Long id);
    OrderEntity updateStatus(Long orderId, String status); // admin
}
