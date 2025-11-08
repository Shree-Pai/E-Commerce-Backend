package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;

public interface CartService {
    Cart getCartForUser(Long userId);
    Cart addToCart(Long userId, Long productId, Integer qty);
    Cart updateCartItem(Long userId, Long productId, Integer qty);
    Cart removeFromCart(Long userId, Long productId);
    void clearCart(Cart cart);
}
