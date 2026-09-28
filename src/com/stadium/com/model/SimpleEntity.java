package com.stadium.com.model;

import com.stadium.com.model.interfaces.BaseEntity;

public class SimpleEntity implements BaseEntity {
    private String id;
    public SimpleEntity() { }
    public SimpleEntity(String id) { this.id = id; }
    @Override public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    @Override public String toCsvLine() { return id == null ? "" : id; }
    @Override public void fromCsvLine(String csvLine) { id = csvLine; }
}
