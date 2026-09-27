/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author tanda
 */
public class Section {
  private String sectionId;
  private String sectionName;
  private double basePrice;
  private String stadiumId;
  
  public Section(){}
  public Section(String sectionId,String sectionName, double basePrice, String stadiumId) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.basePrice = basePrice;
        this.stadiumId = stadiumId;
    }

    public static Section fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;
        return new Section(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()), parts[3].trim());
    }

    public String getSectionId() { return sectionId; }
    public String getSectionName() { return sectionName; }
    public double getBasePrice() { return basePrice; }
    public String getStadiumId() { return stadiumId; }
}
  

