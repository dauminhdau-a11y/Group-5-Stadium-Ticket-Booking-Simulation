package model;

import model.enums.Role;
import java.time.LocalDate;

public class Fan extends Account {
    private String phone;
    private LocalDate dateOfBirth;

    public Fan() {
        super();
        setRole(Role.FAN);
    }

    public Fan(String id, String accountId, String username, String passwordHash, String email,
            Role role, boolean locked, String phone, LocalDate dateOfBirth) {
        super(id, accountId, username, passwordHash, email, role, locked);
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
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

    @Override
    public String toCsvLine() {
        return getId() + "," + getAccountId() + "," + getUsername() + "," + getPasswordHash() + ","
                + getEmail() + "," + getRole().name() + "," + isLocked() + ","
                + (phone != null ? phone : "") + ","
                + (dateOfBirth == null ? "" : dateOfBirth.toString()); 
    }

    public Fan fromCsvLine(String line) {
        String[] values = line.split(",", -1);
        if (values.length == 4) {
            return new Fan(values[0], values[0], values[1], values[2], values[3],
                    Role.FAN, false, null, null);
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

        return new Fan(id, accountId, username, password, email, role, locked, ph, birthDate);
    }
}