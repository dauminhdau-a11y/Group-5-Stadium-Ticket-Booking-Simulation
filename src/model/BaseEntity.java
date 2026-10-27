package model;

public abstract class BaseEntity {
    public abstract String toCsvLine();
    public abstract void fromCsvLine(String csvLine); 
}
