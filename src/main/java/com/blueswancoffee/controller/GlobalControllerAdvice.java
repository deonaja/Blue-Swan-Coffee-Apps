package com.blueswancoffee.controller;

import com.blueswancoffee.model.Cart;
import com.blueswancoffee.model.User;
import com.blueswancoffee.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;

    @ModelAttribute("cart")
    public Cart populateCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return cartService.getCart(user);
        }
        return null;
    }
}
