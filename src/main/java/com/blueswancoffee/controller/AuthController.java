package com.blueswancoffee.controller;

import com.blueswancoffee.model.User;
import com.blueswancoffee.service.AuthService;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            if ("BARISTA".equals(user.getRole())) {
                return "redirect:/barista/dashboard";
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        User user = authService.login(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            
            // Set Spring Security Context
            org.springframework.security.core.GrantedAuthority authority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole());
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication = 
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(user, null, java.util.Collections.singletonList(authority));
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            // Manually save context to session to ensure persistence across requests
            session.setAttribute("SPRING_SECURITY_CONTEXT", org.springframework.security.core.context.SecurityContextHolder.getContext());

            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            if ("BARISTA".equals(user.getRole())) {
                return "redirect:/barista/dashboard";
            }
            return "redirect:/menu";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        try {
            com.blueswancoffee.model.Customer customer = new com.blueswancoffee.model.Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setRole("CUSTOMER");
            authService.register(customer);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/";
    }
}
