package model;

import model.enums.Stand;

public class Section extends BaseEntity {
    private String sectionId;
    private String stadiumId;
    private String name;
    private Stand stand;
    private int capacity;
    private double price;

    public Section() {
        super();
    }

    public Section(String sectionId, String stadiumId, String name, Stand stand,
            int capacity, double price) {
        super(sectionId);
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.name = name;
        this.stand = stand;
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

    public Stand getStand() {
        return stand;
    }

    public void setStand(Stand stand) {
        this.stand = stand;
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
                name != null ? name : "", stand != null ? stand.name() : "",
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
        this.stand = parseStand(parts[3].trim());
        this.capacity = Integer.parseInt(parts[4].trim());
        this.price = Double.parseDouble(parts[5].trim());
        setId(sectionId);
        return this;
    }

    private Stand parseStand(String value) {
        try {
            return Stand.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException legacyType) {
            switch (value.toUpperCase()) {
                case "VIP":
                case "VVIP":
                    return Stand.A;
                case "STANDARD":
                    return Stand.B;
                case "ECONOMY":
                    return Stand.C;
                case "STANDING":
                    return Stand.D;
                default:
                    throw legacyType;
            }
        }
    }
}