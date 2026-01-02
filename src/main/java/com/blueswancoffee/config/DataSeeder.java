package com.blueswancoffee.config;

import com.blueswancoffee.model.Admin;
import com.blueswancoffee.model.Barista;
import com.blueswancoffee.model.Customer;
import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.repository.MenuItemRepository;
import com.blueswancoffee.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final com.blueswancoffee.repository.OrderRepository orderRepository;
    private final com.blueswancoffee.repository.OrderItemRepository orderItemRepository;

    public DataSeeder(UserRepository userRepository, MenuItemRepository menuItemRepository, com.blueswancoffee.repository.OrderRepository orderRepository, com.blueswancoffee.repository.OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedMenuItems();
        fixData();
        seedDummySalesData();
    }

    private void fixData() {
        // Fix for "SNACK" vs "SNACKS" inconsistency from previous seed
        menuItemRepository.findAll().forEach(item -> {
            boolean changed = false;

            // Fix Category Pluralization
            if ("SNACK".equals(item.getCategory())) {
                item.setCategory("SNACKS");
                changed = true;
            }
            // Ensure Croissant specifically is SNACKS
            if ("Croissant".equalsIgnoreCase(item.getName()) && !"SNACKS".equals(item.getCategory())) {
                item.setCategory("SNACKS");
                changed = true;
            }

            if (changed) {
                menuItemRepository.save(item);
                System.out.println("Fixed item: " + item.getName());
            }
        });
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setName("Admin User");
            admin.setEmail("admin@blueswan.com");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            userRepository.save(admin);

            Barista barista = new Barista();
            barista.setName("Barista User");
            barista.setEmail("barista@blueswan.com");
            barista.setPassword("barista123");
            barista.setRole("BARISTA");
            userRepository.save(barista);

            Customer customer = new Customer();
            customer.setName("Customer User");
            customer.setEmail("customer@blueswan.com");
            customer.setPassword("user123");
            customer.setRole("CUSTOMER");
            userRepository.save(customer);

            System.out.println("Users seeded.");
        }
    }

    private void seedMenuItems() {
        if (menuItemRepository.count() == 0) {
            // COFFEE
            saveItem("Caffe Latte", "25000", "COFFEE", "Creamy espresso with steamed milk", "https://images.unsplash.com/photo-1570968992193-73db5279f136?auto=format&fit=crop&w=800&q=80");
            saveItem("Americano", "20000", "COFFEE", "Espresso with hot water", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=800&q=80");
            saveItem("Cappuccino", "25000", "COFFEE", "Espresso with steamed milk and foam", "https://images.unsplash.com/photo-1572442388796-11668a67e53d?auto=format&fit=crop&w=800&q=80");
            saveItem("Caramel Macchiato", "28000", "COFFEE", "Espresso with vanilla and caramel", "https://images.unsplash.com/photo-1485808191679-5f8c7c41f7bc?auto=format&fit=crop&w=800&q=80");
            saveItem("Mocha Latte", "27000", "COFFEE", "Espresso with chocolate and milk", "https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?auto=format&fit=crop&w=800&q=80");

            // NON_COFFEE
            saveItem("Chocolate Drink", "22000", "NON_COFFEE", "Rich chocolate drink", "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?auto=format&fit=crop&w=800&q=80");
            saveItem("Matcha Latte", "25000", "NON_COFFEE", "Japanese green tea latte", "https://images.unsplash.com/photo-1515825838458-f2a94b20105a?auto=format&fit=crop&w=800&q=80");
            saveItem("Vanilla Milk", "18000", "NON_COFFEE", "Sweet vanilla flavored milk", "https://images.unsplash.com/photo-1577805947697-b984381e95e3?auto=format&fit=crop&w=800&q=80");
            saveItem("Strawberry Milk", "18000", "NON_COFFEE", "Fresh strawberry milk", "https://images.unsplash.com/photo-1579954115545-a95591f28dfc?auto=format&fit=crop&w=800&q=80");
            saveItem("Lemon Tea", "17000", "NON_COFFEE", "Refreshing iced lemon tea", "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80");

            // SNACKS
            saveItem("Croissant", "15000", "SNACKS", "Buttery and flaky pastry", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800&q=80");
            saveItem("Chocolate Muffin", "16000", "SNACKS", "Moist chocolate muffin", "https://images.unsplash.com/photo-1621221763172-23c22d1ac8cb?auto=format&fit=crop&w=800&q=80");
            saveItem("Banana Bread", "17000", "SNACKS", "Sweet banana bread", "https://images.unsplash.com/photo-1610612165033-6677bae4592a?auto=format&fit=crop&w=800&q=80");
            saveItem("Cheese Cake Slice", "22000", "SNACKS", "Creamy cheesecake", "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=800&q=80");
            saveItem("French Fries", "18000", "SNACKS", "Crispy golden fries", "https://images.unsplash.com/photo-1630384060421-a431e4cad84e?auto=format&fit=crop&w=800&q=80");

            // BEANS
            saveItem("Arabica Gayo Beans", "60000", "BEANS", "Premium Gayo coffee beans", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&w=800&q=80");
            saveItem("Robusta Lampung Beans", "50000", "BEANS", "Strong Lampung coffee beans", "https://images.unsplash.com/photo-1611854779393-1b2ae54a1985?auto=format&fit=crop&w=800&q=80");
            saveItem("Toraja Beans", "65000", "BEANS", "Exotic Toraja coffee beans", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?auto=format&fit=crop&w=800&q=80");
            saveItem("Bali Kintamani Beans", "70000", "BEANS", "Fruity Bali coffee beans", "https://images.unsplash.com/photo-1587049352851-8d4e8918d119?auto=format&fit=crop&w=800&q=80");

            System.out.println("MenuItems seeded.");
        }
    }

    private void saveItem(String name, String price, String category, String desc, String imageUrl) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        item.setCategory(category);
        item.setDescription(desc);
        item.setIsAvailable(true);
        item.setImageUrl(imageUrl);
        menuItemRepository.save(item);
    }

    private void seedDummySalesData() {
        // Check if we already have data for last month to avoid duplication
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime lastMonth = now.minusMonths(1);
        
        // Basic check if data exists
        long recentSales = orderRepository.count();
        if (recentSales > 5) { 
             System.out.println("Sales data likely exists. Skipping sales seed.");
             return;
        }

        System.out.println("Seeding dummy sales data for reports...");

        // Ensure we have a customer
        Customer customer = (Customer) userRepository.findByEmail("customer@blueswan.com").orElse(null);
        if (customer == null) {
            customer = new Customer();
            customer.setName("Customer User");
            customer.setEmail("customer@blueswan.com");
            customer.setPassword("user123");
            customer.setRole("CUSTOMER");
            userRepository.save(customer);
        }

        // Generate data for 3 months prior, 2 months prior, 1 month prior
        // Month -3
        generateOrdersForMonth(customer, now.minusMonths(3), 15, 2500000); // Higher amount to look like "business"
        // Month -2
        generateOrdersForMonth(customer, now.minusMonths(2), 25, 3800000);
        // Month -1
        generateOrdersForMonth(customer, now.minusMonths(1), 40, 5200000);

        System.out.println("Dummy data seeded successfully.");
    }

    private void generateOrdersForMonth(Customer user, java.time.LocalDateTime monthDate, int count, double totalTarget) {
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < count; i++) {
            com.blueswancoffee.model.Order order = new com.blueswancoffee.model.Order();
            order.setUser(user);
            order.setStatus(com.blueswancoffee.model.OrderStatus.PICKED_UP); // Completed order
            
            // Random day in that month
            int maxDay = monthDate.getMonth().maxLength();
            if (monthDate.getMonthValue() == 2 && !monthDate.toLocalDate().isLeapYear()) maxDay = 28;
            
            int day = random.nextInt(maxDay) + 1;
            int hour = 8 + random.nextInt(12); // Open 8am to 8pm
            int minute = random.nextInt(60);
            
            java.time.LocalDateTime orderTime = monthDate.withDayOfMonth(day).withHour(hour).withMinute(minute);
            order.setCreatedAt(orderTime);
            
            orderRepository.save(order);
            
            // Create 1-3 random items for this order
            createRandomItemsForOrder(order);
            
            // Update total to match items
            BigDecimal calcTotal = order.getItems().stream()
                    .map(com.blueswancoffee.model.OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(calcTotal);
            orderRepository.save(order); // Save again with correct total
        }
    }

    private void createRandomItemsForOrder(com.blueswancoffee.model.Order order) {
        java.util.List<MenuItem> allItems = menuItemRepository.findAll();
        if (allItems.isEmpty()) return;
        
        java.util.Random random = new java.util.Random();
        int itemCount = 1 + random.nextInt(3); // 1 to 3 items
        
        for (int i = 0; i < itemCount; i++) {
             MenuItem menuItem = allItems.get(random.nextInt(allItems.size()));
             
             com.blueswancoffee.model.OrderItem ot = new com.blueswancoffee.model.OrderItem();
             ot.setOrder(order);
             ot.setProduct(menuItem);
             ot.setQuantity(1 + random.nextInt(2)); // 1 or 2 qty
             ot.setSubtotal(menuItem.getPrice().multiply(new BigDecimal(ot.getQuantity())));
             
             order.getItems().add(ot); // Maintain relationship in memory
             // Saving via cascade or repository? Order has CascadeType.ALL for items
             // But to be safe if cascade isn't perfect in unit test mocks or specific setups:
             // But Order.java defined @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
             // So saving order should save items if we added them to the list? 
             // Ideally yes, but let's confirm Order initialized items list.
        }
    }
}
