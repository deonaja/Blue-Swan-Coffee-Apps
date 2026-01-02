package com.blueswancoffee.service;

import com.blueswancoffee.model.Order;
import com.blueswancoffee.model.OrderStatus;
import com.blueswancoffee.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    // Old getDashboardStats removed
    // ...

    private double calculateGrowth(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous).divide(previous, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    public Map<String, Object> getChartData(LocalDateTime start, LocalDateTime end) {
        List<OrderStatus> nonSalesStatuses = Arrays.asList(OrderStatus.CREATED, OrderStatus.CANCELED, OrderStatus.PENDING);
        List<Order> orders = orderRepository.findSalesOrders(nonSalesStatuses, start, end);

        boolean groupByDay = java.time.temporal.ChronoUnit.DAYS.between(start, end) <= 35;
        
        // Use LinkedHashMap to preserve order of dates
        Map<String, BigDecimal> salesData = new LinkedHashMap<>();
        
        if (groupByDay) {
            // Initialize all days in range with 0
            for (LocalDateTime date = start; !date.isAfter(end); date = date.plusDays(1)) {
                String key = date.toLocalDate().toString(); // YYYY-MM-DD
                salesData.put(key, BigDecimal.ZERO);
            }
            
            // Fill with data
            for (Order o : orders) {
                String key = o.getCreatedAt().toLocalDate().toString();
                salesData.merge(key, o.getTotalAmount(), BigDecimal::add);
            }
        } else {
             // Initialize all months in range
             // Start from the first day of the start month to ensure we catch everything
             LocalDateTime tempDate = start.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
             // Safety counter to prevent infinite loop if something goes weird
             int safety = 0;
             while (!tempDate.isAfter(end) && safety < 1000) {
                 String key = tempDate.toLocalDate().toString();
                 salesData.put(key, BigDecimal.ZERO);
                 tempDate = tempDate.plusMonths(1);
                 safety++;
             }
             
             // Fill with data
             for (Order o : orders) {
                 // Double check order is within our buckets (it should be due to query)
                 String key = o.getCreatedAt().getMonth().name().substring(0, 3) + " " + o.getCreatedAt().getYear();
                 salesData.merge(key, o.getTotalAmount(), BigDecimal::add);
             }
        }

        Map<String, Object> result = new HashMap<>();
        // Need to be specific about types for unchecked warning? Javascript side just needs JSON array.
        // It's Map<String, Object> so the caller casts it.
        result.put("labels", new ArrayList<>(salesData.keySet()));
        result.put("values", new ArrayList<>(salesData.values()));
        return result;
    }

    // Keep existing methods if needed or refactor to use this one. 
    // For simplicity, let's keep getDashboardStats but update getMonthlySales_ DEPRECATED in favor of new one.
    
    public List<BigDecimal> getMonthlySalesData() {
        // Default to this year
         LocalDateTime now = LocalDateTime.now();
         LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
         Map<String, Object> data = getChartData(startOfYear, now);
         return (List<BigDecimal>) data.get("values");
    }

    public Map<String, Object> getDashboardStats(LocalDateTime start, LocalDateTime end) {
        List<OrderStatus> nonSalesStatuses = Arrays.asList(OrderStatus.CREATED, OrderStatus.CANCELED, OrderStatus.PENDING);
        
        // Current Range Data
        List<Order> currentOrders = orderRepository.findSalesOrders(nonSalesStatuses, start, end);
        
        BigDecimal totalRevenue = currentOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Previous Range Data (for growth comparison)
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (daysDiff == 0) daysDiff = 1; // Avoid zero
        LocalDateTime prevStart = start.minusDays(daysDiff);
        LocalDateTime prevEnd = start; // previous ends where current starts
        
        List<Order> prevOrders = orderRepository.findSalesOrders(nonSalesStatuses, prevStart, prevEnd);
        BigDecimal prevRevenue = prevOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        // Calculate Growth
        double revenueGrowth = calculateGrowth(prevRevenue, totalRevenue);
        double orderGrowth = calculateGrowth(BigDecimal.valueOf(prevOrders.size()), BigDecimal.valueOf(currentOrders.size()));
        
        // Active Customers
        long activeCustomers = currentOrders.stream().map(o -> o.getUser().getId()).distinct().count();
        long prevActiveCustomers = prevOrders.stream().map(o -> o.getUser().getId()).distinct().count();
        double customerGrowth = calculateGrowth(BigDecimal.valueOf(prevActiveCustomers), BigDecimal.valueOf(activeCustomers));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalOrders", currentOrders.size());
        stats.put("activeCustomers", activeCustomers);
        stats.put("revenueGrowth", revenueGrowth);
        stats.put("orderGrowth", orderGrowth);
        stats.put("activeCustomersGrowth", customerGrowth); // Now numeric %
        
        return stats;
    }
    
    // Default method for backward compatibility if needed, but we should switch to using the ranged one
    public Map<String, Object> getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
         LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
         return getDashboardStats(startOfMonth, now);
    }
    
    public List<Map<String, Object>> getProductStats(LocalDateTime start, LocalDateTime end) {
        List<OrderStatus> nonSalesStatuses = Arrays.asList(OrderStatus.CREATED, OrderStatus.CANCELED, OrderStatus.PENDING);
        List<Order> orders = orderRepository.findSalesOrders(nonSalesStatuses, start, end);
        
        // Map Product ID -> Stats
        Map<UUID, Map<String, Object>> productMap = new HashMap<>();
        
        for (Order order : orders) {
            if (order.getItems() == null) continue;
            for (com.blueswancoffee.model.OrderItem item : order.getItems()) {
                if (item.getProduct() == null) continue;
                
                UUID pid = item.getProduct().getId();
                productMap.putIfAbsent(pid, new HashMap<>());
                Map<String, Object> pStats = productMap.get(pid);
                
                pStats.putIfAbsent("name", item.getProduct().getName());
                pStats.putIfAbsent("category", item.getProduct().getCategory());
                pStats.putIfAbsent("image", item.getProduct().getImageUrl());
                
                int currentQty = (int) pStats.getOrDefault("quantity", 0);
                pStats.put("quantity", currentQty + item.getQuantity());
                
                // You could also track revenue per product if needed
            }
        }
        
        // Convert to list and sort by quantity desc
        List<Map<String, Object>> sortedProducts = new ArrayList<>(productMap.values());
        sortedProducts.sort((p1, p2) -> ((Integer) p2.get("quantity")).compareTo((Integer) p1.get("quantity")));
        
        // Return top 5
        return sortedProducts.stream().limit(5).collect(Collectors.toList());
    }

    public byte[] generateCsvContent(LocalDateTime start, LocalDateTime end) {
        List<OrderStatus> nonSalesStatuses = Arrays.asList(OrderStatus.CREATED, OrderStatus.CANCELED, OrderStatus.PENDING);
        List<Order> orders = orderRepository.findSalesOrders(nonSalesStatuses, start, end);
        
        // Group by Date for aggregation
        Map<String, List<Order>> ordersByDay = orders.stream()
            .collect(Collectors.groupingBy(o -> o.getCreatedAt().toLocalDate().toString()));
            
        StringBuilder csv = new StringBuilder();
        // UTF-8 BOM for Excel compatibility?? Maybe just standard CSV.
        csv.append("Date,Total Orders,Total Revenue,Active Customers\n");
        
        // Iterate day by day
        // Cap at 1000 days safety
        int safety = 0;
        for (LocalDateTime date = start; !date.isAfter(end) && safety < 1000; date = date.plusDays(1)) {
             String key = date.toLocalDate().toString();
             List<Order> daily = ordersByDay.getOrDefault(key, Collections.emptyList());
             
             long count = daily.size();
             BigDecimal revenue = daily.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
             long activeCust = daily.stream().map(o -> o.getUser().getId()).distinct().count();
             
             if (count > 0 || !groupByDay(start, end)) { 
                 // If displaying a long range, maybe omit zero days? 
                 // User likely wants continuous timeline. 
                 // Let's print all days if it's a "dashboard" export.
                 csv.append(key).append(",")
                    .append(count).append(",")
                    .append(revenue).append(",")
                    .append(activeCust).append("\n");
             } else if (groupByDay(start, end)) {
                 csv.append(key).append(",0,0,0\n");
             }
             safety++;
        }
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    
    private boolean groupByDay(LocalDateTime start, LocalDateTime end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end) <= 60;
    }
}
