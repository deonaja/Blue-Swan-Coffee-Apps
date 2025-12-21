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
            MenuItem m1 = new MenuItem();
            m1.setName("Americano");
            m1.setPrice(new BigDecimal("20000"));
            m1.setCategory("COFFEE");
            m1.setIsAvailable(true);
            m1.setDescription("Classic Americano");
            m1.setImageUrl("/images/americano.jpg"); // Placeholder
            menuItemRepository.save(m1);

            MenuItem m2 = new MenuItem();
            m2.setName("Caffe Latte");
            m2.setPrice(new BigDecimal("25000"));
            m2.setCategory("COFFEE");
            m2.setIsAvailable(true);
            m2.setDescription("Creamy Latte");
            m2.setImageUrl("/images/latte.jpg"); // Placeholder
            menuItemRepository.save(m2);

            MenuItem m3 = new MenuItem();
            m3.setName("Croissant");
            m3.setPrice(new BigDecimal("15000"));
            m3.setCategory("SNACK");
            m3.setIsAvailable(true);
            m3.setDescription("Buttery Croissant");
            m3.setImageUrl("/images/croissant.jpg"); // Placeholder
            menuItemRepository.save(m3);

            System.out.println("MenuItems seeded.");
        }
    }
}
