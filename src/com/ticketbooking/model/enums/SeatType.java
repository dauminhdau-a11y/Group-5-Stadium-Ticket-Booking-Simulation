package com.ticketbooking.model.enums;

public enum SeatType {
    VVIP("Stand A (VIP Lounge)", "Private lounge & snacks", 1800000.0),
    VIP("Stand A (VIP Zone)", "Central prime view", 1000000.0),
    STANDARD("Stand B (Regular)", "Panoramic overview", 500000.0),
    ECONOMY("Stand C & D (Economy)", "Behind goalposts", 250000.0),
    STANDING("Standing Area", "Standing view", 80000.0);

    private String standName;
    private String description;
    private double basePrice;

    SeatType(String standName, String description, double basePrice) {
        this.standName = standName;
        this.description = description;
        this.basePrice = basePrice;
    }

    public String getStandName() {
        return standName;
    }

    public void setStandName(String standName) {
        this.standName = standName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
