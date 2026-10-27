package com.ticketbooking.model;

import com.ticketbooking.model.enums.Role;
import java.time.LocalDate;

public class Fan extends Account {
    private String phone;
    private LocalDate dateOfBirth;
    private int loyaltyPoints;

    public Fan() {
        super();
        setRole(Role.FAN);
        this.loyaltyPoints = 0;
    }

    public Fan(String id, String accountId, String username, String passwordHash, String email,
            Role role, boolean locked, String phone, LocalDate dateOfBirth, int loyaltyPoints) {
        super(id, accountId, username, passwordHash, email, role, locked);
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMembershipLevel() {
        if (loyaltyPoints >= 1000) {
            return "DIAMOND";
        } else if (loyaltyPoints >= 500) {
            return "GOLD";
        } else if (loyaltyPoints >= 200) {
            return "SILVER";
        }
        return "STANDARD";
    }

    @Override
    public String toCsvLine() {
        return getId() + "," + getAccountId() + "," + getUsername() + "," + getPasswordHash() + ","
                + getEmail() + "," + getRole().name() + "," + isLocked() + ","
                + (phone != null ? phone : "") + ","
                + (dateOfBirth == null ? "" : dateOfBirth.toString()) + ","
                + loyaltyPoints;
    }

    public Fan fromCsvLine(String line) {
        String[] values = line.split(",", -1);
        if (values.length == 4) {
            return new Fan(values[0], values[0], values[1], values[2], values[3],
                    Role.FAN, false, null, null, 0);
        }
        if (values.length < 7) {
            throw new IllegalArgumentException("Invalid Account CSV data");
        }
        String id = values[0];
        String accountId = values[1];
        String username = values[2];
        String password = values[3];
        String email = values[4];
        Role role = Role.valueOf(values[5]);
        boolean locked = Boolean.parseBoolean(values[6]);
        String ph = values.length > 7 && !values[7].isEmpty() ? values[7] : null;
        LocalDate birthDate = values.length > 8 && !values[8].isEmpty() ? LocalDate.parse(values[8]) : null;
        int points = values.length > 9 && !values[9].isEmpty() ? Integer.parseInt(values[9]) : 0;

        return new Fan(id, accountId, username, password, email, role, locked, ph, birthDate, points);
    }
}