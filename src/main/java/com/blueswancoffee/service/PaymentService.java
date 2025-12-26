package com.blueswancoffee.service;

import com.blueswancoffee.model.*;
import com.blueswancoffee.repository.OrderRepository;
import com.blueswancoffee.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment processPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Order already paid");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Order is canceled");
        }

        // Use ZonedDateTime to ensure correct calculation regardless of server time
        // However, since database stores LocalDateTime without zone, we act as if it is
        // in system zone.
        // For strict Jakarta, we assume the app runs in Jakarta or we treat stored time
        // as Jakarta.

        // Let's force everything to be treated as Jakarta time logic
        // Assuming createdAt was saved as local time which represents Jakarta time
        // (or we just care about relative difference so checking current time in same
        // zone is enough)

        long minutesDiff = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.QRIS);

        if (minutesDiff > 2) {
            // Expired
            order.setStatus(OrderStatus.CANCELED);
            payment.setPaymentStatus(PaymentStatus.FAILED);
        } else {
            // Success
            order.setStatus(OrderStatus.PAID);
            // Generate simple 5-char pickup code
            String code = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            order.setPickupCode(code);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    @Transactional
    public void validateAndUpdateOrderExpiry(Order order) {
        if (order.getStatus() == OrderStatus.CREATED) {
            long secondsDiff = Duration.between(order.getCreatedAt(), LocalDateTime.now()).toSeconds();
            if (secondsDiff > 120) {
                order.setStatus(OrderStatus.CANCELED);
                orderRepository.save(order);
            }
        }
    }
}
