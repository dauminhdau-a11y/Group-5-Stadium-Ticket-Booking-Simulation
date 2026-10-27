package com.controller;

import com.exception.InsufficientPermissionException;
import com.model.User;
import com.model.enums.UserRole;

public class SessionContext {
    private User currentUser;

    public User getUser() { return currentUser; }
    public void setUser(User user) { this.currentUser = user; }
    public void require(UserRole... allowedRoles) {
        if (currentUser == null) {
            throw new InsufficientPermissionException("No user is logged in.");
        }
        for (UserRole role : allowedRoles) {
            if (role.name().equalsIgnoreCase(currentUser.getRole())) {
                return;
            }
        }
        throw new InsufficientPermissionException("User does not have permission.");
    }
}
