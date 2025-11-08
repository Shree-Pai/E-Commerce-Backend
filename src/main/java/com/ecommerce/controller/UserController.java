package com.ecommerce.controller;

import com.ecommerce.dto.UserRegisterRequest;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserController - supports:
 *  POST /api/users/register
 *  POST /api/users/login
 *  GET  /api/users/{id}
 *  PUT  /api/users/{id}
 *  DELETE /api/users/{id}
 *
 * Note: Role-based protection (Admin only for delete) should be enforced via Spring Security in production.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){ this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest req){
        User u = userService.register(req.getName(), req.getEmail(), req.getPassword());
        u.setPassword(null); // do not return password
        return ResponseEntity.status(201).body(u);
    }

    /**
     * Login endpoint - expects { "email": "...", "password": "..." }
     * Returns the user (without password) if credentials match; 401 otherwise.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRegisterRequest req){
        User u = userService.login(req.getEmail(), req.getPassword());
        if (u == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        User u = userService.findById(id);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    /**
     * Update user details (name, email, password)
     * Body uses UserRegisterRequest for simplicity (name,email,password)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRegisterRequest req){
        User updated = userService.updateUser(id, req.getName(), req.getEmail(), req.getPassword());
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete user (Admin only in production).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
