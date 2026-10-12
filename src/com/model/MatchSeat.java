package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.model.enums.SeatStatus;
import com.util.CsvParser;

public class MatchSeat extends SimpleEntity {
    private String matchSeatId;
    private String matchId;
    private String seatId;
    private String sectionId;
    private String seatNumber;
    private double price;
    private SeatStatus status;
    private String lockedBy;
    private LocalDateTime lockedAt;

    public MatchSeat() {
        this.status = SeatStatus.AVAILABLE;
    }

    public MatchSeat(String matchSeatId, String matchId, String seatId, String sectionId, String seatNumber, double price) {
        this.matchSeatId = matchSeatId;
        this.matchId = matchId;
        this.seatId = seatId;
        this.sectionId = sectionId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    @Override public String getId() { return matchSeatId; }
    public String getMatchSeatId() { return matchSeatId; }
    public void setMatchSeatId(String matchSeatId) { this.matchSeatId = matchSeatId; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public String getLockedBy() { return lockedBy; }
    public void setLockedBy(String lockedBy) { this.lockedBy = lockedBy; }
    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }

    public boolean isAvailable() { return status == SeatStatus.AVAILABLE; }
    public void hold(String by) { this.status = SeatStatus.LOCKED; this.lockedBy = by; this.lockedAt = LocalDateTime.now(); }
    public void release() { this.status = SeatStatus.AVAILABLE; this.lockedBy = null; this.lockedAt = null; }
    public void confirm() { this.status = SeatStatus.BOOKED; }

    @Override public String toCsvLine() {
        return CsvParser.encode(matchSeatId, matchId, seatId, sectionId, seatNumber, Double.toString(price),
                status == null ? "" : status.name(), lockedBy, lockedAt == null ? "" : lockedAt.toString());
    }

    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() < 9) return;
        matchSeatId = values.get(0);
        matchId = values.get(1);
        seatId = values.get(2);
        sectionId = values.get(3);
        seatNumber = values.get(4);
        price = Double.parseDouble(values.get(5));
        status = SeatStatus.valueOf(values.get(6));
        lockedBy = values.get(7);
        lockedAt = values.get(8).isEmpty() ? null : LocalDateTime.parse(values.get(8));
    }
}
