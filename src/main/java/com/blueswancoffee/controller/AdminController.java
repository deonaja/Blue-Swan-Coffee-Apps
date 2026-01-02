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

    @Autowired
    private com.blueswancoffee.service.ReportService reportService;

    @GetMapping("/reports")
    public String viewReports(@org.springframework.web.bind.annotation.RequestParam(value = "startDate", required = false) String startDateStr,
                              @org.springframework.web.bind.annotation.RequestParam(value = "endDate", required = false) String endDateStr,
                              @org.springframework.web.bind.annotation.RequestParam(value = "month", required = false) String monthStr,
                              HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }
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
