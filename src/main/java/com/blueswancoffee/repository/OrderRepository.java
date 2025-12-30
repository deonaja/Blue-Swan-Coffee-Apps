package com.blueswancoffee.repository;

import com.blueswancoffee.model.Order;
import com.blueswancoffee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    java.util.Optional<Order> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") UUID id);

    boolean existsByUserAndStatus(User user, com.blueswancoffee.model.OrderStatus status);
}
