package com.ecommerce.service;

import com.ecommerce.entity.User;

public interface UserService {
    User register(String name, String email, String rawPassword);
    User findById(Long id);
    User findByEmail(String email);

    // new:
    /**
     * Attempts login. Returns User when credentials match, otherwise null (or throw custom exception).
     */
    User login(String email, String rawPassword);

    /**
     * Update user fields. If password is null or empty, password remains unchanged.
     */
    User updateUser(Long id, String name, String email, String rawPassword);

    /**
     * Delete user by id.
     */
    void deleteUser(Long id);
}
