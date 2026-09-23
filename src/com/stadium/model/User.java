package com.stadium.model;

import com.stadium.model.enums.UserRole;
import com.stadium.model.interfaces.BaseEntity;

public class User implements BaseEntity {
    private String id;
    private String username;
    private String passwordHash;
    private UserRole role;

    public User() {
    }

    public User(String id, String username, String passwordHash, UserRole role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    @Override
    public String toCsvLine() {
        return String.join(",", id == null ? "" : id, username == null ? "" : username,
                passwordHash == null ? "" : passwordHash, role == null ? "" : role.name());
    }

    @Override
    public void fromCsvLine(String csvLine) {
        String[] values = CsvValues.split(csvLine, 4);
        id = values[0];
        username = values[1];
        passwordHash = values[2];
        role = values[3].isBlank() ? null : UserRole.valueOf(values[3]);
    }

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
