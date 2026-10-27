package com.ticketbooking.model;

import com.ticketbooking.model.enums.Role;

public class Staff extends Account {
    public Staff() {
        setRole(Role.STAFF);
    }

    public Staff(String id, String accountId, String username, String passwordHash,
            String email, boolean locked) {
        super(id, accountId, username, passwordHash, email, Role.STAFF, locked);
    }
}