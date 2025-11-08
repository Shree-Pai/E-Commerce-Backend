package com.ecommerce.repository;

import com.ecommerce.entity.OrderEntity;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUser(User user);
}
