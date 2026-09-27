package com.stadium.repository.impl;

import com.stadium.model.Seat;
import java.util.List;

public class SeatRepository extends AbstractCsvRepository<Seat> {
    public List<Seat> findBySectionId(String sectionId) {
        return findByCondition(seat -> sectionId != null && sectionId.equals(seat.getSectionId()));
    }
}
