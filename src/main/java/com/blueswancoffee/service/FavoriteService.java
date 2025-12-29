package com.blueswancoffee.service;

import com.blueswancoffee.model.Favorite;
import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import com.blueswancoffee.repository.FavoriteRepository;
import com.blueswancoffee.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Toggle favorite status for a menu item
     * @return true if now favorited, false if unfavorited
     */
    @Transactional
    public boolean toggleFavorite(User user, UUID menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (favoriteRepository.existsByUserAndMenuItem(user, menuItem)) {
            // Already favorited, so unfavorite
            favoriteRepository.deleteByUserAndMenuItem(user, menuItem);
            return false;
        } else {
            // Not favorited, so add to favorites
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setMenuItem(menuItem);
            favoriteRepository.save(favorite);
            return true;
        }
    }

    /**
     * Check if a menu item is favorited by user
     */
    public boolean isFavorite(User user, UUID menuItemId) {
        if (user == null) return false;
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);
        if (menuItem == null) return false;
        return favoriteRepository.existsByUserAndMenuItem(user, menuItem);
    }

    /**
     * Get set of favorited menu item IDs for a user
     */
    public Set<UUID> getUserFavoriteIds(User user) {
        if (user == null) return new HashSet<>();
        return favoriteRepository.findMenuItemIdsByUser(user);
    }
}
