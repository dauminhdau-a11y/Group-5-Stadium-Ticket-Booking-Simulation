package com.ticketbooking.repository;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Section;
import com.ticketbooking.model.enums.SeatType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatRepository extends CsvRepository<Seat> {
    private SectionRepository sectionRepository;

    public SeatRepository() {
        this("data/seats.csv");
    }

    public SeatRepository(String fileName) {
        this(fileName, new SectionRepository());
    }

    public SeatRepository(String fileName, SectionRepository sectionRepository) {
        super(fileName);
        this.sectionRepository = sectionRepository;
    }

    public SectionRepository getSectionRepository() {
        return sectionRepository;
    }

    public void setSectionRepository(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public Seat parseLine(String line) {
        return new Seat().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line) {
        String header = line.trim();
        return "seatId,sectionId,rowLabel,seatNumber,state,version".equalsIgnoreCase(header)
                || "seatId,matchId,status,seatType,price,version".equalsIgnoreCase(header);
    }

    @Override
    public List<Seat> findAll() throws IOException {
        List<Seat> seats = super.findAll();
        if (sectionRepository != null) {
            Map<String, Section> sectionsById = new HashMap<>();
            for (Section section : sectionRepository.findAll()) {
                sectionsById.put(section.getSectionId(), section);
            }
            for (Seat seat : seats) {
                if (seat.getSectionId() != null && !seat.getSectionId().isEmpty()) {
                    Section section = sectionsById.get(seat.getSectionId());
                    if (section != null) {
                        SeatType seatType = SeatType.valueOf(section.getType().name());
                        seat.setSectionDetails(seatType, section.getPrice());
                    }
                }
            }
        }
        return seats;
    }
}