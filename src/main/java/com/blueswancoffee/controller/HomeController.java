package com.blueswancoffee.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import com.blueswancoffee.repository.MenuItemRepository;
import com.blueswancoffee.model.MenuItem;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final MenuItemRepository menuItemRepository;

    public HomeController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/")
    public String home(Model model, jakarta.servlet.http.HttpSession session) {
        com.blueswancoffee.model.User user = (com.blueswancoffee.model.User) session.getAttribute("user");
        if (user != null) {
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            if ("BARISTA".equals(user.getRole())) {
                return "redirect:/barista/dashboard";
            }
        }
        List<MenuItem> products = menuItemRepository.findAll();
        // Take first 4 items as favorites for now, or randomize
        List<MenuItem> favorites = products.stream().limit(4).collect(Collectors.toList());
        model.addAttribute("favorites", favorites);
        return "index";
    }
}
