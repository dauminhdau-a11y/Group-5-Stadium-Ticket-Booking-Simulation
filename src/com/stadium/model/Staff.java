package com.stadium.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Staff extends User {
    private String staffId;

    public Staff() { }
    public Staff(String userId, String username, String password, String email, String role, String staffId) {
        super(userId, username, password, email, role);
        this.staffId = staffId;
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    public boolean createMatch() { return false; }
    public List<Match> viewMatchesByStadium(String stadiumId) { return new ArrayList<>(); }
    public boolean listForSale() { return false; }
    public boolean listTicketsForSale() { return false; }
    public void sellTickets() { }
    public boolean confirmTicketSold() { return false; }
    public void supportFanContact() { }
    public ShiftReport viewShiftReport() { return null; }
}
