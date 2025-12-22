package com.blueswancoffee.controller;

import com.blueswancoffee.model.Order;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

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
            model.addAttribute("message", "Pembayaran Berhasil!");
            model.addAttribute("alertClass", "alert-success text-green-700 bg-green-100 border-green-500");
        }
        if (error != null) {
            if (error.equals("expired")) {
                model.addAttribute("message", "Pembayaran Gagal: Pesanan Kadaluarsa (> 2 Menit)");
            } else {
                model.addAttribute("message", "Pembayaran Gagal");
            }
            model.addAttribute("alertClass", "alert-danger text-red-700 bg-red-100 border-red-500");
        }

        return "profile";
    }
}
