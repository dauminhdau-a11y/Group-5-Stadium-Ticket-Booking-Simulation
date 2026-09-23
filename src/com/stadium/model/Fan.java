package com.stadium.model;

import java.util.ArrayList;
import java.util.List;

public class Fan extends User {
    private int loyaltyPoints;

    public Fan() { }
    public Fan(String userId, String username, String password, String email, String role, int loyaltyPoints) {
        super(userId, username, password, email, role);
        this.loyaltyPoints = loyaltyPoints;
    }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public boolean register() { return false; }
    public void viewProfile() { }
    public Booking bookTickets(String matchId, String seatId) { return null; }
    public List<Booking> viewBookingHistory() { return new ArrayList<>(); }
    public boolean refundTicket(String ticketId) { return false; }
    public void notifyUser() { }
}
