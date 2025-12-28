package com.blueswancoffee.model;

public interface Brewable {
    void startBrewing(Order order);

    void markReady(Order order);
}
