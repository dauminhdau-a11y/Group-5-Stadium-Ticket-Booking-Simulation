package com.stadium.model;

import com.stadium.model.enums.UserRole;

public class Staff extends User {
    public Staff() { setRole(UserRole.STAFF); }
    public Staff(String id, String username, String passwordHash) {
        super(id, username, passwordHash, UserRole.STAFF);
    }
}
