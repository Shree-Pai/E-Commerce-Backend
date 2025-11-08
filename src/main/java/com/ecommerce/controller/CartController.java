package com.ecommerce.controller;

import com.ecommerce.dto.CartAddRequest;
import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Cart> add(@PathVariable Long productId, @RequestBody CartAddRequest req){
        Cart c = cartService.addToCart(req.getUserId(), productId, req.getQuantity());
        return ResponseEntity.ok(c);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<Cart> update(@PathVariable Long productId, @RequestBody CartAddRequest req){
        Cart c = cartService.updateCartItem(req.getUserId(), productId, req.getQuantity());
        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> remove(@PathVariable Long productId, @RequestBody CartAddRequest req){
        Cart c = cartService.removeFromCart(req.getUserId(), productId);
        return ResponseEntity.ok(c);
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(@RequestParam Long userId){
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }
}
