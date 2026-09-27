package com.stadium.model;

public class Section extends SimpleEntity {
    private String sectionId;
    private String stadiumId;
    private String name;
    private String type;
    private double basePrice;

    public Section() { }
    public Section(String sectionId, String stadiumId, String name, String type, double basePrice) {
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.name = name;
        this.type = type;
        this.basePrice = basePrice;
    }
    @Override public String getId() { return sectionId; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public String getStadiumId() { return stadiumId; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    @Override public String toCsvLine() {
        return String.join(",", value(sectionId), value(stadiumId), value(name), value(type), Double.toString(basePrice));
    }
    @Override public void fromCsvLine(String line) {
        String[] v = line.split(",", -1);
        sectionId = v[0]; stadiumId = v[1]; name = v[2]; type = v[3]; basePrice = Double.parseDouble(v[4]);
    }
    private String value(String s) { return s == null ? "" : s; }
}
