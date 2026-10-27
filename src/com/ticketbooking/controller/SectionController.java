package com.ticketbooking.controller;

import com.ticketbooking.model.Section;
import com.ticketbooking.model.enums.SectionType;
import com.ticketbooking.repository.SectionRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SectionController {
    private SectionRepository sectionRepository;

    public SectionController() {
        this(new SectionRepository("data/sections.csv"));
    }

    public SectionController(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public SectionRepository getSectionRepository() {
        return sectionRepository;
    }

    public void setSectionRepository(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> getAllSections() throws IOException {
        return sectionRepository.findAll();
    }

    public List<Section> getSectionsByStadium(String stadiumId) throws IOException {
        List<Section> results = new ArrayList<>();
        if (stadiumId == null) {
            return results;
        }
        for (Section sec : sectionRepository.findAll()) {
            if (stadiumId.equalsIgnoreCase(sec.getStadiumId())) {
                results.add(sec);
            }
        }
        return results;
    }

    public Section getSectionById(String id) throws IOException {
        return sectionRepository.findById(id);
    }

    public boolean createSection(Section section) throws IOException {
        if (section == null || section.getSectionId() == null || section.getSectionId().trim().isEmpty()) {
            return false;
        }
        if (sectionRepository.findById(section.getSectionId()) != null) {
            return false;
        }
        sectionRepository.save(section);
        return true;
    }

    public boolean updateSection(String sectionId, String name, SectionType type, int capacity, double price) throws IOException {
        Section section = sectionRepository.findById(sectionId);
        if (section == null) {
            return false;
        }
        if (name != null && !name.trim().isEmpty()) {
            section.setName(name.trim());
        }
        if (type != null) {
            section.setType(type);
        }
        if (capacity > 0) {
            section.setCapacity(capacity);
        }
        if (price >= 0) {
            section.setPrice(price);
        }
        sectionRepository.save(section);
        return true;
    }

    public boolean removeSection(String sectionId) throws IOException {
        return sectionRepository.deleteById(sectionId);
    }
}
