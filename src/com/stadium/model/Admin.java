package com.stadium.model;

import com.stadium.model.enums.UserRole;

public class Admin extends User {
    public Admin() { setRole(UserRole.ADMIN); }
    public Admin(String id, String username, String passwordHash) {
        super(id, username, passwordHash, UserRole.ADMIN);
    }
}
