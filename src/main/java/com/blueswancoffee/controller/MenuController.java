package com.blueswancoffee.controller;

import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.MenuItemRepository;
import com.blueswancoffee.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/menu")
    public String menu(Model model, HttpSession session) {
        List<MenuItem> allItems = menuItemRepository.findAll();
        User user = (User) session.getAttribute("user");
        
        Set<UUID> favoriteIds = favoriteService.getUserFavoriteIds(user);
        
        // Sort: favorites first, then by name
        List<MenuItem> sortedItems = allItems.stream()
                .sorted((a, b) -> {
                    boolean aFav = favoriteIds.contains(a.getId());
                    boolean bFav = favoriteIds.contains(b.getId());
                    if (aFav && !bFav) return -1;
                    if (!aFav && bFav) return 1;
                    return a.getName().compareTo(b.getName());
                })
                .collect(Collectors.toList());
        
        model.addAttribute("items", sortedItems);
        model.addAttribute("favoriteIds", favoriteIds);
        return "menu";
    }

    @GetMapping("/menu/{id}")
    public String detail(@PathVariable UUID id, Model model, HttpSession session) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        User user = (User) session.getAttribute("user");
        boolean isFavorite = favoriteService.isFavorite(user, id);
        
        model.addAttribute("item", item);
        model.addAttribute("isFavorite", isFavorite);
        return "detail";
    }

    @PostMapping("/menu/favorite/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable UUID id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Please login first"));
        }
        
        boolean isFavorited = favoriteService.toggleFavorite(user, id);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "favorited", isFavorited,
                "message", isFavorited ? "Added to favorites" : "Removed from favorites"
        ));
    }
}
