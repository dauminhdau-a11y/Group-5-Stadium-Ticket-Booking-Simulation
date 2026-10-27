package com.ticketbooking.model;

import com.ticketbooking.model.enums.SectionType;

public class Section extends BaseEntity {
    private String sectionId;
    private String stadiumId;
    private String name;
    private SectionType type;
    private int capacity;
    private double price;

    public Section() {
        super();
    }

    public Section(String sectionId, String stadiumId, String name, SectionType type,
            int capacity, double price) {
        super(sectionId);
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    public String getSectionId() {
        return sectionId != null ? sectionId : getId();
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
        setId(sectionId);
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SectionType getType() {
        return type;
    }

    public void setType(SectionType type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toCsvLine() {
        return String.join(",", getSectionId(), stadiumId != null ? stadiumId : "",
                name != null ? name : "", type != null ? type.name() : "STANDARD",
                Integer.toString(capacity), Double.toString(price));
    }

    public Section fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid section CSV data.");
        }
        this.sectionId = parts[0].trim();
        this.stadiumId = parts[1].trim();
        this.name = parts[2].trim();
        this.type = SectionType.valueOf(parts[3].trim());
        this.capacity = Integer.parseInt(parts[4].trim());
        this.price = Double.parseDouble(parts[5].trim());
        setId(sectionId);
        return this;
    }
}