package com.stadium.model;
import java.util.HashSet;
import java.util.Set;
public class SyncMechanism {
    private final Set<Integer> bookedSeats;
    public SyncMechanism() {
        this.bookedSeats = new HashSet<>();
    }
    public synchronized boolean tryBookSeat(int seatId) {
        if (bookedSeats.contains(seatId)) {
            return false;
        }
        bookedSeats.add(seatId);
        return true;
    }
    public synchronized int getBookedSeatCount() {
        return bookedSeats.size();
    }
    public synchronized boolean isSeatBooked(int seatId) {
        return bookedSeats.contains(seatId);
    }
    public synchronized void reset() {
        bookedSeats.clear();
    }
}