package com.ecommerce.service.impl;

import com.ecommerce.entity.*;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.*;
import com.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           CartItemRepository cartItemRepository){
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public Cart getCartForUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Override
    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer qty){
        Cart cart = getCartForUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId)).findFirst();
        if (existing.isPresent()){
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(qty);
            item.setCart(cart);
            cart.getItems().add(item);
        }
        cart.recalc();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItem(Long userId, Long productId, Integer qty){
        Cart cart = getCartForUser(userId);
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId)).findFirst();
        if (itemOpt.isEmpty()) throw new ResourceNotFoundException("Cart item not found");
        CartItem item = itemOpt.get();
        if (qty <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(qty);
            cartItemRepository.save(item);
        }
        cart.recalc();
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeFromCart(Long userId, Long productId){
        return updateCartItem(userId, productId, 0);
    }

    @Override
    @Transactional
    public void clearCart(Cart cart){
        cart.getItems().clear();
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
