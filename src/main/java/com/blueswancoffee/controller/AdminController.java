package com.blueswancoffee.controller;

import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "admin_dashboard";
    }

    @GetMapping("/menu/add")
    public String addMenuPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("menuList", menuItemRepository.findAll());
        return "admin_add_menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editMenuPage(@PathVariable("id") UUID id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        MenuItem menuItem = menuItemRepository.findById(id).orElse(null);
        if (menuItem == null) {
            return "redirect:/admin/menu/add";
        }
        model.addAttribute("user", user);
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("menuList", menuItemRepository.findAll());
        return "admin_add_menu";
    }

    @GetMapping("/menu/delete/{id}")
    public String deleteMenu(@PathVariable("id") UUID id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        menuItemRepository.deleteById(id);
        return "redirect:/admin/menu/add";
    }

    @GetMapping("/reports")
    public String viewReports(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "admin_reports";
    }

    @PostMapping("/menu/add")
    public String addMenu(@ModelAttribute MenuItem menuItem, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
        // Basic validation or default values could go here
        if (menuItem.getImageUrl() == null || menuItem.getImageUrl().isEmpty()) {
            menuItem.setImageUrl("/img/menu/1.jpg"); // Default image
        }

        menuItemRepository.save(menuItem);
        return "redirect:/admin/menu/add";
    }
}
