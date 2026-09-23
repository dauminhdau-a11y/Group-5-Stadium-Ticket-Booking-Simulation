package com.stadium.model;

public class Stadium extends SimpleEntity {
    private String stadiumId;
    private String name;
    private int capacity;

    public Stadium() { }
    public Stadium(String stadiumId, String name, int capacity) {
        this.stadiumId = stadiumId;
        this.name = name;
        this.capacity = capacity;
    }
    @Override public String getId() { return stadiumId; }
    public String getStadiumId() { return stadiumId; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void manageStadium() { }
    public boolean lockSeat() { return false; }
    @Override public String toCsvLine() { return String.join(",", stadiumId, name, Integer.toString(capacity)); }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        stadiumId = values[0]; name = values[1]; capacity = Integer.parseInt(values[2]);
    }
}
