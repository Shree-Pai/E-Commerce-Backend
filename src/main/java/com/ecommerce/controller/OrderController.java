package com.ecommerce.controller;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.OrderEntity;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService){ this.orderService = orderService; }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest req){
        OrderEntity o = orderService.checkout(req);
        return ResponseEntity.status(201).body(o);
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> listByUser(@RequestParam Long userId){
        return ResponseEntity.ok(orderService.findByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status){
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
