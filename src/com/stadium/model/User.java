package com.stadium.model;

import com.stadium.model.interfaces.BaseEntity;
import java.util.List;
import java.util.ArrayList;

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
        return String.join(",", value(userId), value(username), value(password), value(email), value(role));
    }

    @Override
    public void fromCsvLine(String csvLine) {
        String[] values = CsvValues.split(csvLine, 5);
        userId = values[0];
        username = values[1];
        password = values[2];
        email = values[3];
        role = values[4];
    }

    private static String value(String value) { return value == null ? "" : value; }

    static final class CsvValues {
        private CsvValues() { }
        static String[] split(String line, int size) {
            String[] result = new String[size];
            String[] values = line == null ? new String[0] : line.split(",", -1);
            for (int i = 0; i < size; i++) result[i] = i < values.length ? values[i] : "";
            return result;
        }
    }
}
