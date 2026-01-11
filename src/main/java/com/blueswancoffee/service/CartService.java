package com.blueswancoffee.service;

import com.blueswancoffee.model.Cart;
import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.CartRepository;
import com.blueswancoffee.repository.MenuItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;

    public CartService(CartRepository cartRepository, MenuItemRepository menuItemRepository) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public void addToCart(User user, UUID productId, int quantity) {
        Cart cart = getCart(user);
        
        MenuItem product = menuItemRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cart.addItem(product, quantity);
        cartRepository.save(cart);
    }

    @Transactional
    public Cart getCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            try {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepository.save(newCart);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Retry in case another thread created it
                return cartRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Cart creation failed", e));
            }
        });
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.clear();
        cartRepository.save(cart);
    }



    @Transactional
    public void removeItem(User user, UUID productId) {
        Cart cart = getCart(user);
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    @Transactional
    public void updateItemQuantity(User user, UUID productId, int quantity) {
        Cart cart = getCart(user);
        cart.updateQuantity(productId, quantity);
        cartRepository.save(cart);
    }
}
