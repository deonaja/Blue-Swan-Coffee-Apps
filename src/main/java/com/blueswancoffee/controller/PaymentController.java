package com.blueswancoffee.controller;

import com.blueswancoffee.model.Order;

import com.blueswancoffee.model.Payment;
import com.blueswancoffee.model.PaymentStatus;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.OrderRepository;
import com.blueswancoffee.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/{orderId}")
    public String paymentPage(@PathVariable UUID orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validate Ownership
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders";
        }

        // Validate Status check removed to allow viewing history details
        // if (order.getStatus() != OrderStatus.CREATED) {
        // return "redirect:/orders";
        // }

        // Check for expiry
        paymentService.validateAndUpdateOrderExpiry(order);

        model.addAttribute("order", order);
        // Send deadline as Epoch Millis to avoid JS parsing issues
        // Assuming createdAt is valid. Add 2 minutes.
        // We need to convert LocalDateTime to Instant (ZoneOffset)
        // Let's assume the system is in local zone.
        java.time.ZonedDateTime deadlineZDT = order.getCreatedAt().plusMinutes(2)
                .atZone(java.time.ZoneId.systemDefault());
        model.addAttribute("deadline", deadlineZDT.toInstant().toEpochMilli());

        return "payment";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam UUID orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Payment payment = paymentService.processPayment(orderId);
            if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
                return "redirect:/profile?success";
            } else {
                return "redirect:/profile?error=expired";
            }
        } catch (RuntimeException e) {
            // Handle cases like already paid or logic errors
            return "redirect:/profile?error=" + e.getMessage();
        }
    }
}
