package com.blueswancoffee.controller;

import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;

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

    private final MenuItemRepository menuItemRepository;
    private final com.blueswancoffee.service.ReportService reportService;

    public AdminController(MenuItemRepository menuItemRepository, com.blueswancoffee.service.ReportService reportService) {
        this.menuItemRepository = menuItemRepository;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Role check handled by Spring Security
        model.addAttribute("user", user);
        return "admin_dashboard";
    }

    @GetMapping("/menu/add")
    public String addMenuPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Role check handled by Spring Security
        model.addAttribute("user", user);
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("menuList", menuItemRepository.findAll());
        return "admin_add_menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String editMenuPage(@PathVariable("id") UUID id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Role check handled by Spring Security
        MenuItem menuItem = menuItemRepository.findById(id).orElse(null);
        if (menuItem == null) {
            return "redirect:/admin/menu/add";
        }
        model.addAttribute("user", user);
        model.addAttribute("menuItem", menuItem);
        model.addAttribute("menuList", menuItemRepository.findAll());
        return "admin_add_menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenu(@PathVariable("id") UUID id,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // Role check handled by Spring Security
        try {
            menuItemRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully!");
        } catch (Exception e) {
            // Likely a foreign key constraint violation (item is in an order)
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete menu item. It may be part of an existing order.");
        }
        return "redirect:/admin/menu/add";
    }

    @GetMapping("/reports")
    public String viewReports(@org.springframework.web.bind.annotation.RequestParam(value = "startDate", required = false) String startDateStr,
                              @org.springframework.web.bind.annotation.RequestParam(value = "endDate", required = false) String endDateStr,
                              @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) String monthStr,
                              HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        // Role check handled by Spring Security
        model.addAttribute("user", user);
        
        java.time.LocalDateTime end = java.time.LocalDateTime.now();
        java.time.LocalDateTime start = end.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0); // Default this month
        
        boolean isMonthFilter = false;

        // Priority 1: Specific Month Filter (YYYY-MM)
        if (monthStr != null && !monthStr.isEmpty()) {
            try {
                // Parse YYYY-MM
                java.time.YearMonth ym = java.time.YearMonth.parse(monthStr);
                start = ym.atDay(1).atStartOfDay();
                end = ym.atEndOfMonth().atTime(23, 59, 59);
                isMonthFilter = true;
                model.addAttribute("selectedMonth", monthStr);
            } catch (Exception e) {
                // Ignore malformed input
            }
        } 
        // Priority 2: Custom Range
        else if (startDateStr != null && !startDateStr.isEmpty()) { // Only check start, end is optional (defaults to now/today)
             try {
                start = java.time.LocalDate.parse(startDateStr).atStartOfDay();
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    end = java.time.LocalDate.parse(endDateStr).atTime(23, 59, 59);
                }
            } catch (Exception e) {
                // Ignore
            }
        } else {
             // Default: This Month (matches the user's "per month" interest)
             model.addAttribute("selectedMonth", java.time.YearMonth.now().toString());
             isMonthFilter = true;
        }
        
        model.addAttribute("filterType", isMonthFilter ? "month" : "range");
        
        // Add Report Data
        // 1. KPI Stats (Revenue, Orders, Customers) - now dynamic based on range
        model.addAllAttributes(reportService.getDashboardStats(start, end));
        
        // 2. Chart Data
        java.util.Map<String, Object> chartData = reportService.getChartData(start, end);
        @SuppressWarnings("unchecked")
        java.util.List<java.math.BigDecimal> sales = (java.util.List<java.math.BigDecimal>) chartData.get("values");
        @SuppressWarnings("unchecked")
        java.util.List<String> labels = (java.util.List<String>) chartData.get("labels");
        
        model.addAttribute("monthlySales", sales);
        model.addAttribute("monthLabels", labels);
        
        // 3. Popular Products - now dynamic based on range
        model.addAttribute("popularProducts", reportService.getProductStats(start, end));
        
        // Pass selected dates back to view for input fields
        model.addAttribute("startDate", start.toLocalDate());
        model.addAttribute("endDate", end.toLocalDate());
        
        java.math.BigDecimal maxSales = sales.stream().max(java.math.BigDecimal::compareTo).orElse(java.math.BigDecimal.ONE);
        if (maxSales.compareTo(java.math.BigDecimal.ZERO) == 0) maxSales = java.math.BigDecimal.ONE;
        model.addAttribute("maxSales", maxSales);
        
        return "admin_reports";
    }

    @PostMapping("/menu/add")
    public String addMenu(@jakarta.validation.Valid @ModelAttribute MenuItem menuItem,
                          org.springframework.validation.BindingResult bindingResult,
                          @org.springframework.web.bind.annotation.RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
                          HttpSession session,
                          Model model) {
        User user = (User) session.getAttribute("user");
        // Role check handled by Spring Security

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("menuList", menuItemRepository.findAll());
            // If editing, we need to make sure we don't lose the ID or existing data
            return "admin_add_menu";
        }

        // Handle Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Generate unique filename
                String originalFilename = imageFile.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = UUID.randomUUID().toString() + ext;
                
                // Define upload path - Relative to runtime directory
                // Ideally this should be externalized to application.properties
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
                
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                
                java.nio.file.Path filePath = uploadPath.resolve(filename);
                java.nio.file.Files.copy(imageFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                menuItem.setImageUrl("/img/menu/" + filename);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                // Handle error (maybe redirect with error message)
            }
        } 
        
        // If editing and no new file, keep existing image logic is handled by @ModelAttribute if hidden field is present on form?
        // Actually, if 'imageUrl' field is bound, it will have the value from the form (which matches existing if we put a hidden input or just the text input).
        // If we remove the text input for user, we should ensure the existing URL is preserved if no new file is uploaded.
        // We will check if it's null/empty after binding.
        
        if (menuItem.getImageUrl() == null || menuItem.getImageUrl().isEmpty()) {
             // If creating new and no image uploaded, default
             if (menuItem.getId() == null) {
                 menuItem.setImageUrl("/img/menu/1.jpg");
             } else {
                 // If editing and no new image, we hope the existing one was passed via hidden field.
                 // If not, we might need to fetch it from DB. 
                 // To be safe, let's fetch from DB if ID is present and URL is empty of new file provided/bound.
                 MenuItem existing = menuItemRepository.findById(menuItem.getId()).orElse(null);
                 if (existing != null) {
                     menuItem.setImageUrl(existing.getImageUrl());
                 }
             }
        }

        menuItemRepository.save(menuItem);
        return "redirect:/admin/menu/add";
    }
    @GetMapping("/reports/export/csv")
    public org.springframework.http.ResponseEntity<byte[]> exportCsv(
            @org.springframework.web.bind.annotation.RequestParam(value = "startDate", required = false) String startDateStr,
            @org.springframework.web.bind.annotation.RequestParam(value = "endDate", required = false) String endDateStr,
            @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) String monthStr) {
        
        java.time.LocalDateTime end = java.time.LocalDateTime.now();
        java.time.LocalDateTime start = end.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        if (monthStr != null && !monthStr.isEmpty()) {
            try {
                java.time.YearMonth ym = java.time.YearMonth.parse(monthStr);
                start = ym.atDay(1).atStartOfDay();
                end = ym.atEndOfMonth().atTime(23, 59, 59);
            } catch (Exception e) {}
        } else if (startDateStr != null && !startDateStr.isEmpty()) {
             try {
                start = java.time.LocalDate.parse(startDateStr).atStartOfDay();
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    end = java.time.LocalDate.parse(endDateStr).atTime(23, 59, 59);
                }
            } catch (Exception e) {}
        }
        
        byte[] content = reportService.generateCsvContent(start, end);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        String filename = "report_" + start.toLocalDate() + "_to_" + end.toLocalDate() + ".csv";
        headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
        
        return new org.springframework.http.ResponseEntity<>(content, headers, org.springframework.http.HttpStatus.OK);
    }
}
