package com.blueswancoffee.controller;

import com.blueswancoffee.model.Cart;
import com.blueswancoffee.model.User;
import com.blueswancoffee.model.OrderStatus;
import com.blueswancoffee.service.CartService;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;



@ControllerAdvice
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final com.blueswancoffee.repository.OrderRepository orderRepository;

    public GlobalControllerAdvice(CartService cartService, com.blueswancoffee.repository.OrderRepository orderRepository) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    @ModelAttribute("cart")
    public Cart populateCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return cartService.getCart(user);
        }
        return null;
    }

    @ModelAttribute("hasUnpaidOrder")
    public boolean hasUnpaidOrder(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return orderRepository.existsByUserAndStatus(user, OrderStatus.CREATED);
        }
        return false;
    }
}
