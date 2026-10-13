package com.model;

import java.util.List;

import com.model.interfaces.BaseEntity;

import java.util.ArrayList;

import com.util.CsvParser;

public class User implements BaseEntity {
    private String userId;
    private String username;
    private String password;
    private String email;
    private String role;
    private String fullName = "";
    private String phoneNumber = "";
    private int loyaltyPoints = 0;

    public User() {
    }

    public User(String userId, String username, String password, String email, String role) {
        this(userId, username, password, email, role, "", "", 0);
    }

    public User(String userId, String username, String password, String email, String role,
                String fullName, String phoneNumber, int loyaltyPoints) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.fullName = fullName == null ? "" : fullName;
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber;
        this.loyaltyPoints = loyaltyPoints;
    }

    @Override
    public String getId() { return userId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setId(String id) { this.userId = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName == null ? "" : fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber == null ? "" : phoneNumber; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public boolean login() { return false; }
    public void logout() { }
    public List<Match> searchMatch(String keyword) { return new ArrayList<>(); }
    public List<Match> findMatch(String criteria) { return new ArrayList<>(); }
    public Match viewMatchDetails(String matchId) { return null; }
    public List<Ticket> viewMyTickets() { return new ArrayList<>(); }

    @Override
    public String toCsvLine() {
        return CsvParser.encode(userId, username, password, email, role,
                fullName == null ? "" : fullName,
                phoneNumber == null ? "" : phoneNumber,
                Integer.toString(loyaltyPoints));
    }

    @Override
    public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        userId = valueAt(values, 0);
        username = valueAt(values, 1);
        password = valueAt(values, 2);
        email = valueAt(values, 3);
        role = valueAt(values, 4);
        fullName = valueAt(values, 5);
        phoneNumber = valueAt(values, 6);
        String pts = valueAt(values, 7);
        try {
            loyaltyPoints = pts.isEmpty() ? 0 : Integer.parseInt(pts);
        } catch (NumberFormatException e) {
            loyaltyPoints = 0;
        }
    }

    private static String valueAt(List<String> values, int index) {
        return index < values.size() ? values.get(index) : "";
    }
}
