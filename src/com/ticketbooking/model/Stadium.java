package com.ticketbooking.model;

public class Stadium extends BaseEntity {
    private String stadiumId;
    private String name;
    private String location;
    private int capacity;

    public Stadium() {
        super();
    }

    public Stadium(String stadiumId, String name, String location, int capacity) {
        super(stadiumId);
        this.stadiumId = stadiumId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public Stadium(String stadiumId, String name, int capacity) {
        this(stadiumId, name, "Default Location", capacity);
    }

    @Override
    public String getId() {
        return stadiumId != null ? stadiumId : super.getId();
    }

    public String getStadiumId() {
        return stadiumId != null ? stadiumId : getId();
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
        setId(stadiumId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void manageStadium() {
    }

    public boolean lockSeat() {
        return true;
    }

    @Override
    public String toCsvLine() {
        return getStadiumId() + "," + (name != null ? name : "") + ","
                + (location != null ? location : "") + "," + capacity;
    }

    public void fromCsvLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return;
        }
        String[] values = csvLine.split(",", -1);
        if (values.length >= 4) {
            stadiumId = values[0].trim();
            name = values[1].trim();
            location = values[2].trim();
            try {
                capacity = Integer.parseInt(values[3].trim());
            } catch (NumberFormatException e) {
                capacity = 0;
            }
        } else if (values.length >= 3) {
            stadiumId = values[0].trim();
            name = values[1].trim();
            try {
                capacity = Integer.parseInt(values[2].trim());
            } catch (NumberFormatException e) {
                capacity = 0;
            }
        }
        setId(stadiumId);
    }
}