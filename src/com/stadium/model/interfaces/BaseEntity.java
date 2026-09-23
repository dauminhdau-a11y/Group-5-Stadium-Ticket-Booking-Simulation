package com.stadium.model.interfaces;

public interface BaseEntity {
    String getId();
    String toCsvLine();
    void fromCsvLine(String csvLine);
}
