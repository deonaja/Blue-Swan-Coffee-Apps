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
    @org.springframework.web.bind.annotation.ResponseBody
    public Object addToCart(@RequestParam UUID productId,
            @RequestParam int quantity,
            HttpSession session,
            jakarta.servlet.http.HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // If AJAX, return 401
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return org.springframework.http.ResponseEntity.status(401).body("Unauthorized");
            }
            return "redirect:/login";
        }
        
        cartService.addToCart(user, productId, quantity);
        
        // If AJAX request, return JSON with new count
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            Cart cart = cartService.getCart(user);
            int totalItems = cart.getItems().stream().mapToInt(com.blueswancoffee.model.CartItem::getQuantity).sum();
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("count", totalItems); // or cart.getItems().size() if badge counts unique items. Usually it's total quantity. 
            // Just for safety let's assume we want just simple not empty indicator or total items. 
            // The user said "icon cartnya jadi ada merahnya gitu" -> usually implies indicator or count.
            // I'll return count.
            return response;
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/menu");
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        com.blueswancoffee.model.Order order = orderService.checkout(user);
        // Redirect to payment page for the new order
        return "redirect:/payment/" + order.getId();
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
