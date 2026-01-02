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
    
    org.springframework.data.domain.Page<Order> findByUserOrderByCreatedAtDesc(User user, org.springframework.data.domain.Pageable pageable);

    List<Order> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    java.util.Optional<Order> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") UUID id);

    boolean existsByUserAndStatus(User user, com.blueswancoffee.model.OrderStatus status);

    List<Order> findByStatusNotAndCreatedAtBetween(com.blueswancoffee.model.OrderStatus status, java.time.LocalDateTime start, java.time.LocalDateTime end);
    
    // Helper to find valid sales (not cancelled, not pending/created if we define those as unpaid)
    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE o.status NOT IN :statuses AND o.createdAt BETWEEN :start AND :end")
    List<Order> findSalesOrders(@org.springframework.data.repository.query.Param("statuses") List<com.blueswancoffee.model.OrderStatus> statuses, 
                                @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, 
                                @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}
