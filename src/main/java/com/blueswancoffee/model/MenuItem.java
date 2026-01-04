package com.blueswancoffee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @jakarta.validation.constraints.NotBlank(message = "Name is required")
    private String name;

    @jakarta.validation.constraints.NotNull(message = "Price is required")
    @jakarta.validation.constraints.PositiveOrZero(message = "Price cannot be negative")
    private BigDecimal price;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private String category;

    @Column(name = "is_available")
    private Boolean isAvailable;
}
