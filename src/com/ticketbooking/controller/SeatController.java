package com.ticketbooking.controller;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SeatType;
import com.ticketbooking.repository.SeatRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeatController {
    private SeatRepository seatRepository;

    public SeatController() {
        this(new SeatRepository("data/seats.csv"));
    }

    public SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public SeatRepository getSeatRepository() {
        return seatRepository;
    }

    public void setSeatRepository(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllSeats() throws IOException {
        return seatRepository.findAll();
    }

    public List<Seat> getSeatsByMatch(String matchId) throws IOException {
        List<Seat> results = new ArrayList<>();
        if (matchId == null) {
            return results;
        }
        for (Seat seat : seatRepository.findAll()) {
            if (matchId.equalsIgnoreCase(seat.getMatchId())) {
                results.add(seat);
            }
        }
        return results;
    }

    public Seat getSeatById(String seatId) throws IOException {
        return seatRepository.findById(seatId);
    }

    public List<Seat> searchSeats(String keyword) throws IOException {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Seat> results = new ArrayList<>();
        for (Seat seat : seatRepository.findAll()) {
            if (query.isEmpty()
                    || (seat.getSeatId() != null && seat.getSeatId().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getMatchId() != null && seat.getMatchId().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getSeatType() != null && seat.getSeatType().name().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getStatus() != null && seat.getStatus().name().toLowerCase(Locale.ROOT).contains(query))) {
                results.add(seat);
            }
        }
        return results;
    }

    public boolean createSeat(Seat seat) throws IOException {
        if (seat == null || seat.getSeatId() == null || seat.getSeatId().trim().isEmpty()) {
            return false;
        }
        if (seatRepository.findById(seat.getSeatId()) != null) {
            return false;
        }
        seatRepository.save(seat);
        return true;
    }

    public boolean updateSeat(String seatId, SeatStatus status, SeatType type, double price) throws IOException {
        Seat seat = seatRepository.findById(seatId);
        if (seat == null) {
            return false;
        }
        if (status != null) {
            seat.setStatus(status);
        }
        if (type != null) {
            seat.setSeatType(type);
        }
        if (price >= 0) {
            seat.setPrice(price);
        }
        seatRepository.save(seat);
        return true;
    }

    public boolean removeSeat(String seatId) throws IOException {
        return seatRepository.deleteById(seatId);
    }
}
