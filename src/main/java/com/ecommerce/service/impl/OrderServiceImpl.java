package com.ecommerce.service.impl;

import com.ecommerce.dto.CheckoutRequest;
import com.ecommerce.entity.*;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import com.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public OrderServiceImpl(CartRepository cartRepository, UserRepository userRepository,
                            ProductRepository productRepository, OrderRepository orderRepository,
                            CartItemRepository cartItemRepository){
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public OrderEntity checkout(CheckoutRequest req){
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.getUserId()));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) throw new IllegalArgumentException("Cart is empty");

        // Validate stock
        for (CartItem ci : cart.getItems()){
            Product p = productRepository.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + ci.getProduct().getId()));
            if (p.getStock() < ci.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + p.getName());
            }
        }

        // Create order
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotalAmount(cart.getTotalPrice());
        for (CartItem ci : cart.getItems()){
            OrderItem oi = new OrderItem();
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice()); // price snapshot
            order.addItem(oi);
        }
        order = orderRepository.save(order);

        // Simulate payment:
        if (req.isPaymentSuccess()){
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.PLACED);

            // decrement stock
            for (CartItem ci : cart.getItems()){
                Product p = productRepository.findById(ci.getProduct().getId()).get();
                p.setStock(p.getStock() - ci.getQuantity());
                productRepository.save(p);
            }

            // clear cart
            cart.getItems().clear();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> findByUser(Long userId){
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return orderRepository.findByUser(u);
    }

    @Override
    public OrderEntity findById(Long id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @Override
    @Transactional
    public OrderEntity updateStatus(Long orderId, String status){
        OrderEntity o = findById(orderId);
        o.setOrderStatus(OrderStatus.valueOf(status));
        return orderRepository.save(o);
    }
}
