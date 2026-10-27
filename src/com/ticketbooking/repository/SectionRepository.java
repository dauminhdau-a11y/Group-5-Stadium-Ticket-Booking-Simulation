package com.ticketbooking.repository;

import com.ticketbooking.model.Section;

public class SectionRepository extends CsvRepository<Section> {
    public SectionRepository() {
        this("data/sections.csv");
    }

    public SectionRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Section parseLine(String line) {
        return new Section().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "sectionId,stadiumId,name,type,capacity,price".equalsIgnoreCase(line.trim());
    }
}