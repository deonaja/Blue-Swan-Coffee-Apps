package com.blueswancoffee.controller;

import com.blueswancoffee.model.Cart;
import com.blueswancoffee.model.User;
import com.blueswancoffee.service.CartService;
import com.blueswancoffee.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = cartService.getCart(user);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam UUID productId,
            @RequestParam int quantity,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        cartService.addToCart(user, productId, quantity);
        return "redirect:/menu";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        orderService.checkout(user);
        // Ideally redirect to an orders list or confirmation page.
        // Since we don't have an order history page yet, redirecting to menu with a
        // param or just menu.
        return "redirect:/menu";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam UUID productId, @RequestParam int quantity, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        cartService.updateItemQuantity(user, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam UUID productId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        cartService.removeItem(user, productId);
        return "redirect:/cart";
    }
}
