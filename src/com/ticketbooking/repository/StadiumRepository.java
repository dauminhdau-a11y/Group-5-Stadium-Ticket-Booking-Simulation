package com.ticketbooking.repository;

import com.ticketbooking.model.Stadium;

public class StadiumRepository extends CsvRepository<Stadium> {
    public StadiumRepository() {
        this("data/stadiums.csv");
    }

    public StadiumRepository(String fileName) {
        super(fileName);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "stadiumId,name,location,capacity".equalsIgnoreCase(line.trim());
    }

    @Override
    public Stadium parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 4) {
            return new Stadium(parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    Integer.parseInt(parts[3].trim()));
        } else if (parts.length >= 3) {
            return new Stadium(parts[0].trim(), parts[1].trim(),
                    Integer.parseInt(parts[2].trim()));
        }
        return new Stadium(parts[0].trim(), "Unknown", 1000);
    }
}
