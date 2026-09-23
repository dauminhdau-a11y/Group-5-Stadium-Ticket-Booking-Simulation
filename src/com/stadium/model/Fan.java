package com.stadium.model;

import com.stadium.model.enums.UserRole;

public class Fan extends User {
    public Fan() { setRole(UserRole.FAN); }
    public Fan(String id, String username, String passwordHash) {
        super(id, username, passwordHash, UserRole.FAN);
    }
}
