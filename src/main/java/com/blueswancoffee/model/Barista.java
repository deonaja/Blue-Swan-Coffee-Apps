package com.blueswancoffee.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("BARISTA")
public class Barista extends User {
    // Additional fields for Barista can be added here
}
