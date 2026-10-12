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

    public User() {
    }

    public User(String userId, String username, String password, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
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

    public boolean login() { return false; }
    public void logout() { }
    public List<Match> searchMatch(String keyword) { return new ArrayList<>(); }
    public List<Match> findMatch(String criteria) { return new ArrayList<>(); }
    public Match viewMatchDetails(String matchId) { return null; }
    public List<Ticket> viewMyTickets() { return new ArrayList<>(); }

    @Override
    public String toCsvLine() {
        return CsvParser.encode(userId, username, password, email, role);
    }

    @Override
    public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        userId = valueAt(values, 0);
        username = valueAt(values, 1);
        password = valueAt(values, 2);
        email = valueAt(values, 3);
        role = valueAt(values, 4);
    }

    private static String valueAt(List<String> values, int index) {
        return index < values.size() ? values.get(index) : "";
    }
}
