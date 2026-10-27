package com.ticketbooking.controller;

import com.ticketbooking.model.Stadium;
import com.ticketbooking.repository.StadiumRepository;
import java.io.IOException;
import java.util.List;

public class StadiumController {
    private StadiumRepository stadiumRepository;

    public StadiumController() {
        this(new StadiumRepository("data/stadiums.csv"));
    }

    public StadiumController(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    public StadiumRepository getStadiumRepository() {
        return stadiumRepository;
    }

    public void setStadiumRepository(StadiumRepository stadiumRepository) {
        this.stadiumRepository = stadiumRepository;
    }

    public List<Stadium> getAllStadiums() throws IOException {
        return stadiumRepository.findAll();
    }

    public Stadium getStadiumById(String id) throws IOException {
        return stadiumRepository.findById(id);
    }

    public boolean createStadium(Stadium stadium) throws IOException {
        if (stadium == null || stadium.getStadiumId() == null || stadium.getStadiumId().trim().isEmpty()) {
            return false;
        }
        if (stadiumRepository.findById(stadium.getStadiumId()) != null) {
            return false;
        }
        stadiumRepository.save(stadium);
        return true;
    }

    public boolean updateStadium(String stadiumId, String name, String location, int capacity) throws IOException {
        Stadium stadium = stadiumRepository.findById(stadiumId);
        if (stadium == null) {
            return false;
        }
        if (name != null && !name.trim().isEmpty()) {
            stadium.setName(name.trim());
        }
        if (location != null && !location.trim().isEmpty()) {
            stadium.setLocation(location.trim());
        }
        if (capacity > 0) {
            stadium.setCapacity(capacity);
        }
        stadiumRepository.save(stadium);
        return true;
    }

    public boolean removeStadium(String stadiumId) throws IOException {
        return stadiumRepository.deleteById(stadiumId);
    }
}
