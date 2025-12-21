package com.blueswancoffee.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    // Additional fields for Customer can be added here
}
