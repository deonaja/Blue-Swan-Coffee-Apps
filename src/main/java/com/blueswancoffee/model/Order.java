package com.blueswancoffee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "pickup_code")
    private String pickupCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "barista_id")
    private Barista barista;

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Review review;

    // --- Domain Logic ---

    public boolean isExpired() {
        if (this.status != OrderStatus.CREATED) return false;
        long secondsDiff = Duration.between(this.createdAt, LocalDateTime.now()).toSeconds();
        return secondsDiff > 120; // 2 minutes expiry
    }

    public void markAsPaid() {
        if (this.status == OrderStatus.PAID) {
            throw new RuntimeException("Order already paid");
        }
        if (this.status == OrderStatus.CANCELED) {
            throw new RuntimeException("Order is canceled");
        }
        this.status = OrderStatus.PAID;
        generatePickupCode();
    }

    public void markAsCanceled() {
        this.status = OrderStatus.CANCELED;
    }

    private void generatePickupCode() {
        this.pickupCode = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}
