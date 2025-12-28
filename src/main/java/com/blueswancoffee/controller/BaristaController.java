package com.blueswancoffee.controller;

import com.blueswancoffee.model.Barista;
import com.blueswancoffee.model.Order;
import com.blueswancoffee.model.OrderStatus;
import com.blueswancoffee.model.User;
import com.blueswancoffee.model.Review;
import com.blueswancoffee.repository.OrderRepository;
import com.blueswancoffee.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/barista")
public class BaristaController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"BARISTA".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();

        // Filter orders: Barista sees PAID (Incoming), BREWING, and READY
        // Hiding CREATED (not paid yet), PICKED_UP/CANCELED (History), and invalid null
        // status
        orders.removeIf(order -> order.getStatus() == null ||
                order.getStatus() == OrderStatus.CREATED ||
                order.getStatus() == OrderStatus.PICKED_UP ||
                order.getStatus() == OrderStatus.CANCELED);

        // Fetch reviews for this barista
        List<Review> reviews = reviewRepository.findByBaristaIdOrderByCreatedAtDesc(user.getId());

        // Pre-calculate counts for template (SpringEL doesn't support lambdas)
        long paidCount = orders.stream().filter(o -> o.getStatus() == OrderStatus.PAID).count();
        long brewingCount = orders.stream().filter(o -> o.getStatus() == OrderStatus.BREWING).count();
        long readyCount = orders.stream().filter(o -> o.getStatus() == OrderStatus.READY).count();

        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("brewingCount", brewingCount);
        model.addAttribute("readyCount", readyCount);

        return "barista_dashboard";
    }

    @PostMapping("/order/{id}/status")
    public String updateStatus(@PathVariable UUID id, @RequestParam("status") String statusStr, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"BARISTA".equals(user.getRole())) {
            return "redirect:/login";
        }

        Order order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);
            Barista barista = (Barista) user;

            switch (newStatus) {
                case BREWING:
                    // Only start brewing if not already assigned or assigned to self
                    if (order.getBarista() == null || order.getBarista().getId().equals(barista.getId())) {
                        barista.startBrewing(order);
                    }
                    break;
                case READY:
                    barista.markReady(order);
                    break;
                case PICKED_UP:
                    // For now, assuming Barista verifies visually and we pass the correct code
                    // In a real flow, Barista might input the code provided by customer
                    barista.handover(order, order.getPickupCode());
                    break;
                default:
                    order.setStatus(newStatus);
            }

            orderRepository.save(order);
        } catch (IllegalArgumentException | ClassCastException e) {
            // Handle invalid status or user type issues
            e.printStackTrace();
        }

        return "redirect:/barista/dashboard";
    }
}
