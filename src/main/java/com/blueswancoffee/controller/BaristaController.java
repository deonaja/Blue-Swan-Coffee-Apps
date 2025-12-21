package com.blueswancoffee.controller;

import com.blueswancoffee.model.User;
import jakarta.servlet.http.HttpSession;
import com.blueswancoffee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/barista")
public class BaristaController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"BARISTA".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "barista_dashboard";
    }

    @GetMapping("/orders")
    public String viewOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"BARISTA".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getAllOrders());
        return "barista_orders";
    }
}
