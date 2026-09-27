package com.stadium.controller;

import com.stadium.model.Seat;
import com.stadium.model.Section;
import com.stadium.repository.impl.SeatRepository;
import com.stadium.repository.impl.StadiumRepository;
import java.util.List;

public class StadiumController {
    private final StadiumRepository stadiumRepo;
    private final SeatRepository seatRepo;

    public StadiumController(StadiumRepository stadiumRepo, SeatRepository seatRepo) {
        this.stadiumRepo = stadiumRepo; this.seatRepo = seatRepo;
    }
    public List<Section> getSections(String stadiumId) { return List.of(); }
    public List<Seat> buildSeatMap(String sectionId) { return seatRepo.findBySectionId(sectionId); }
}
