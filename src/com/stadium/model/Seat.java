package com.stadium.model;

import com.stadium.model.enums.SeatStatus;
import com.stadium.model.interfaces.BaseEntity;

public class Seat implements BaseEntity {
    private String seatId;
    private String matchId;
    private String sectionId;
    private String seatNumber;
    private double price;
    private SeatStatus status;
    private long version;
    private long lockTimestamp;

    public Seat() { status = SeatStatus.AVAILABLE; }
    public Seat(String seatId, String matchId, String sectionId, String seatNumber, double price) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.sectionId = sectionId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }
    public Seat(String seatId, String matchId, String sectionId, String seatNumber,
                double price, SeatStatus status, long version, long lockTimestamp) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.sectionId = sectionId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.version = version;
        this.lockTimestamp = lockTimestamp;
    }

    @Override public String getId() { return seatId; }
    public String getSeatId() { return seatId; }
    public void setSeatId(String value) { seatId = value; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String value) { matchId = value; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String value) { sectionId = value; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String value) { seatNumber = value; }
    public double getPrice() { return price; }
    public void setPrice(double value) { price = value; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus value) { status = value; }
    public long getVersion() { return version; }
    public void setVersion(long value) { version = value; }
    public long getLockTimestamp() { return lockTimestamp; }
    public void setLockTimestamp(long value) { lockTimestamp = value; }
    public boolean lockSeat(String lockedBy) { return false; }
    public void unlockSeat() { }
    public void bookSeat() { }

    @Override public String toCsvLine() {
        return String.join(",", seatId, matchId, sectionId, seatNumber, Double.toString(price),
                status.name(), Long.toString(version), Long.toString(lockTimestamp));
    }
    @Override public void fromCsvLine(String line) {
        String[] v = line.split(",", -1);
        seatId = v[0]; matchId = v[1]; sectionId = v[2]; seatNumber = v[3];
        price = Double.parseDouble(v[4]); status = SeatStatus.valueOf(v[5]);
        version = Long.parseLong(v[6]); lockTimestamp = Long.parseLong(v[7]);
    }
}
