package com.blueswancoffee.repository;

import com.blueswancoffee.model.Favorite;
import com.blueswancoffee.model.MenuItem;
import com.blueswancoffee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndMenuItem(User user, MenuItem menuItem);

    boolean existsByUserAndMenuItem(User user, MenuItem menuItem);

    void deleteByUserAndMenuItem(User user, MenuItem menuItem);

    @Query("SELECT f.menuItem.id FROM Favorite f WHERE f.user = :user")
    Set<UUID> findMenuItemIdsByUser(User user);
}
