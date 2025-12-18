package com.blueswancoffee.controller;

import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping("/menu")
    public String menu(Model model) {
        List<MenuItem> items = menuItemRepository.findAll();
        model.addAttribute("items", items);
        return "menu";
    }
}
