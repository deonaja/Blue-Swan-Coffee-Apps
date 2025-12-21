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

    public DataSeeder(UserRepository userRepository, MenuItemRepository menuItemRepository) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedMenuItems();
        fixData();
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
            saveItem("Caffe Latte", "25000", "COFFEE", "Creamy espresso with steamed milk");
            saveItem("Americano", "20000", "COFFEE", "Espresso with hot water");
            saveItem("Cappuccino", "25000", "COFFEE", "Espresso with steamed milk and foam");
            saveItem("Caramel Macchiato", "28000", "COFFEE", "Espresso with vanilla and caramel");
            saveItem("Mocha Latte", "27000", "COFFEE", "Espresso with chocolate and milk");

            // NON_COFFEE
            saveItem("Chocolate Drink", "22000", "NON_COFFEE", "Rich chocolate drink");
            saveItem("Matcha Latte", "25000", "NON_COFFEE", "Japanese green tea latte");
            saveItem("Vanilla Milk", "18000", "NON_COFFEE", "Sweet vanilla flavored milk");
            saveItem("Strawberry Milk", "18000", "NON_COFFEE", "Fresh strawberry milk");
            saveItem("Lemon Tea", "17000", "NON_COFFEE", "Refreshing iced lemon tea");

            // SNACKS
            saveItem("Croissant", "15000", "SNACKS", "Buttery and flaky pastry");
            saveItem("Chocolate Muffin", "16000", "SNACKS", "Moist chocolate muffin");
            saveItem("Banana Bread", "17000", "SNACKS", "Sweet banana bread");
            saveItem("Cheese Cake Slice", "22000", "SNACKS", "Creamy cheesecake");
            saveItem("French Fries", "18000", "SNACKS", "Crispy golden fries");

            // BEANS
            saveItem("Arabica Gayo Beans", "60000", "BEANS", "Premium Gayo coffee beans");
            saveItem("Robusta Lampung Beans", "50000", "BEANS", "Strong Lampung coffee beans");
            saveItem("Toraja Beans", "65000", "BEANS", "Exotic Toraja coffee beans");
            saveItem("Bali Kintamani Beans", "70000", "BEANS", "Fruity Bali coffee beans");

            System.out.println("MenuItems seeded.");
        }
    }

    private void saveItem(String name, String price, String category, String desc) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        item.setCategory(category);
        item.setDescription(desc);
        item.setIsAvailable(true);
        // Use a default image if not specified, or mapped by name in real app
        item.setImageUrl("/images/menu/" + name.toLowerCase().replace(" ", "-") + ".jpg");
        menuItemRepository.save(item);
    }
}
