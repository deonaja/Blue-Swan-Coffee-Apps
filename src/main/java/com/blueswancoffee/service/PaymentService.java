package com.blueswancoffee.service;

import com.blueswancoffee.model.*;

import com.blueswancoffee.repository.OrderRepository;
import com.blueswancoffee.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.isExpired()) {
             order.markAsCanceled();
             
             Payment payment = new Payment();
             payment.setOrder(order);
             payment.setAmount(order.getTotalAmount());
             payment.setPaymentMethod(PaymentMethod.QRIS);
             payment.setPaymentStatus(PaymentStatus.FAILED);
             
             orderRepository.save(order);
             return paymentRepository.save(payment);
        } else {
             try {
                order.markAsPaid();
                
                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setAmount(order.getTotalAmount());
                payment.setPaymentMethod(PaymentMethod.QRIS);
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                
                orderRepository.save(order);
                return paymentRepository.save(payment);
             } catch (RuntimeException e) {
                 // Double check if expired/cancelled caught by concurrent check inside method called?
                 // Ideally markAsPaid handles logic checks.
                 throw e;
             }
        }
    }

    @Transactional
    public void validateAndUpdateOrderExpiry(Order order) {
        if (order.getStatus() == OrderStatus.CREATED && order.isExpired()) {
            order.markAsCanceled();
            orderRepository.save(order);
        }
    }
}
