package com.stadium.model;

public class Admin extends User {
    private int adminLevel;

    public Admin() { }
    public Admin(String userId, String username, String password, String email, String role, int adminLevel) {
        super(userId, username, password, email, role);
        this.adminLevel = adminLevel;
    }

    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }
    public boolean createStaffAccount() { return false; }
    public boolean createSellerAccount() { return false; }
    public boolean removeSellerAccount(String sellerId) { return false; }
    public boolean createBuyerAccount() { return false; }
    public boolean removeBuyerAccount(String buyerId) { return false; }
    public boolean lockBuyerAccount(String buyerId) { return false; }
    public double viewRevenue() { return 0.0; }
}
