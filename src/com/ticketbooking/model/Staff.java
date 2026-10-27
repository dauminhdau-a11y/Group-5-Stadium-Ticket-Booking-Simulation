package com.ticketbooking.model;

import com.ticketbooking.model.enums.Role;

public class Staff extends Account {
    private String department;
    private String position;

    public Staff() {
        super();
        setRole(Role.STAFF);
        this.department = "Operations";
        this.position = "Ticketing Staff";
    }

    public Staff(String id, String accountId, String username, String passwordHash,
            String email, boolean locked) {
        super(id, accountId, username, passwordHash, email, Role.STAFF, locked);
        this.department = "Operations";
        this.position = "Ticketing Staff";
    }

    public Staff(String id, String accountId, String username, String passwordHash,
            String email, boolean locked, String department, String position) {
        super(id, accountId, username, passwordHash, email, Role.STAFF, locked);
        this.department = department;
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}