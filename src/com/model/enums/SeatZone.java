package com.model.enums;

public enum SeatZone {
    VIP("VIP", "Khu VIP", 500000, 100),
    NEAR("NEAR", "Khu gần", 350000, 350),
    EAST_WING("EAST_WING", "East Wing", 250000, 525),
    WEST_WING("WEST_WING", "West Wing", 250000, 700),
    FAR("FAR", "Khu ngoài xa", 150000, Integer.MAX_VALUE);

    private final String sectionId;
    private final String displayName;
    private final double price;
    private final int lastSeatNumber;

    SeatZone(String sectionId, String displayName, double price, int lastSeatNumber) {
        this.sectionId = sectionId;
        this.displayName = displayName;
        this.price = price;
        this.lastSeatNumber = lastSeatNumber;
    }

    public String getSectionId() {
        return sectionId;
    }

    public double getPrice() {
        return price;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLastSeatNumber() {
        return lastSeatNumber;
    }

    public static SeatZone forSeatNumber(int seatNumber) {
        for (SeatZone zone : values()) {
            if (seatNumber <= zone.lastSeatNumber) {
                return zone;
            }
        }
        return FAR;
    }

    public static String displayNameFor(String sectionId) {
        for (SeatZone zone : values()) {
            if (zone.sectionId.equals(sectionId)) {
                return zone.displayName;
            }
        }
        return sectionId;
    }

    public static int orderFor(String sectionId) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].sectionId.equals(sectionId)) {
                return i;
            }
        }
        return values().length;
    }
}