package com.ticketbooking.model;

import com.ticketbooking.model.enums.Role;

public class Admin extends Account {
    private String permissions;

    public Admin() {
        super();
        setRole(Role.ADMIN);
        this.permissions = "ALL";
    }

    public Admin(String id, String accountId, String username, String passwordHash,
            String email, boolean locked) {
        super(id, accountId, username, passwordHash, email, Role.ADMIN, locked);
        this.permissions = "ALL";
    }

    public Admin(String id, String accountId, String username, String passwordHash,
            String email, boolean locked, String permissions) {
        super(id, accountId, username, passwordHash, email, Role.ADMIN, locked);
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}