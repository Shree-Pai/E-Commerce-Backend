package com.ecommerce.service.impl;

import com.ecommerce.entity.User;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(com.ecommerce.entity.Role.CUSTOMER);
        return userRepository.save(u);
    }

    @Override
    public User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    // --- New methods ---

    /**
     * Login: returns user if password matches, otherwise null.
     * In production you might return JWT or throw authentication exception.
     */
    @Override
    public User login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(null);
    }

    /**
     * Update user — updates name, email and optionally password.
     * If newEmail collides with existing other user, throws IllegalArgumentException.
     */
    @Override
    @Transactional
    public User updateUser(Long id, String name, String email, String rawPassword) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (email != null && !email.equals(u.getEmail())) {
            // check uniqueness
            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Email already in use: " + email);
                }
            });
            u.setEmail(email);
        }

        if (name != null) u.setName(name);

        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(u);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userRepository.delete(u);
    }
}
