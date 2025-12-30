package com.blueswancoffee.controller;

import com.blueswancoffee.model.Order;

import com.blueswancoffee.model.Review;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.OrderRepository;
import com.blueswancoffee.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("orders", orders);

        if (success != null) {
            if (success.equals("review_saved")) {
                model.addAttribute("message", "Terima kasih atas review Anda!");
                model.addAttribute("alertClass", "alert-success text-green-700 bg-green-100 border-green-500");
            } else {
                model.addAttribute("message", "Pembayaran Berhasil!");
                model.addAttribute("alertClass", "alert-success text-green-700 bg-green-100 border-green-500");
            }
        }
        if (error != null) {
            if (error.equals("expired")) {
                model.addAttribute("message", "Pembayaran Gagal: Pesanan Kadaluarsa (> 2 Menit)");
            } else if (error.equals("no_barista")) {
                model.addAttribute("message", "Gagal: Pesanan belum diproses barista.");
            } else {
                model.addAttribute("message", "Pembayaran Gagal");
            }
            model.addAttribute("alertClass", "alert-danger text-red-700 bg-red-100 border-red-500");
        }

        return "profile";
    }

    @PostMapping("/order/{id}/review")
    public String submitReview(@PathVariable UUID id,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/profile?error=unauthorized";
        }

        if (order.getReview() != null) {
            return "redirect:/profile?error=already_reviewed";
        }

        if (order.getBarista() == null) {
            // Cannot review if no barista assigned (though technically COMPLETED orders
            // should have one)
            return "redirect:/profile?error=no_barista";
        }

        Review review = new Review();
        review.setOrder(order);
        review.setUser(user);
        review.setBarista(order.getBarista());
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);

        return "redirect:/profile?success=review_saved";
    }

    @GetMapping("/orders/{id}/details")
    public String orderDetail(@org.springframework.web.bind.annotation.PathVariable("id") java.util.UUID id,
            HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/profile?error=Unauthorized";
        }

        model.addAttribute("order", order);
        return "order-detail";
    }
}
