package com.example.pbo_coffee_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@Controller
public class MenuController {

    @GetMapping("/")
    public String homePage() {
        return "menu"; 
    }
}