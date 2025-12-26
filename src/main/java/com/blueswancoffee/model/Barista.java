package com.blueswancoffee.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.blueswancoffee.model.Brewable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("BARISTA")
public class Barista extends User implements Brewable {

    private String stationName;

    @Override
    public void startBrewing(Order order) {
        order.setStatus(OrderStatus.BREWING);
        order.setBarista(this);
    }

    @Override
    public void markReady(Order order) {
        order.setStatus(OrderStatus.READY);
    }

    public void handover(Order order, String pickupCode) {
        if (order.getPickupCode() != null && order.getPickupCode().equals(pickupCode)) {
            order.setStatus(OrderStatus.PICKED_UP);
        } else {
            throw new IllegalArgumentException("Invalid pickup code");
        }
    }
}
