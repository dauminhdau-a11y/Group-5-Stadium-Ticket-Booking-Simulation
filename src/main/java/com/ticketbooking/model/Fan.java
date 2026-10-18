package com.ticketbooking.model;

import java.time.LocalDate;

import com.ticketbooking.model.enums.Role;

public class Fan extends Account{
    private String phone;
    private LocalDate dateOfBirth;
    private int loyaltyPoints;
    public Fan(){
        super();
        setRole(Role.FAN);
        this.loyaltyPoints = 0;
    }
    public Fan(String id, String accountId, String username, String passwordHash, String email, Role role, boolean locked, String phone, LocalDate dateOfBirth, int loyaltyPoints){
        super(id, accountId, username, passwordHash, email, role, locked);
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.loyaltyPoints = loyaltyPoints;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }
    public int getLoyaltyPoints(){
        return loyaltyPoints;
    }
    public void setLoyaltyPoints(int loyaltyPoints){
        this.loyaltyPoints = loyaltyPoints;
    }
    @Override 
    public String toCsvLine(){
        return getId()+","+getAccountId()+","+getUsername()+","+getPasswordHash()+","+getEmail()+","+getRole().name()+","+isLocked()+","+phone+","+(dateOfBirth==null?"":dateOfBirth)+","+loyaltyPoints;
    }
    @Override 
    public Fan fromCsvLine(String line ){
        String[] values = line.split(",",-1);
        if (values.length == 4){
            return new Fan(values[0], values[0], values[1], values[2], values[3],
                    Role.FAN, false, null, null, 0);
        }
        if (values.length != 10){
            throw new IllegalArgumentException("Invalid Account CSV data");
        }
        Role role = Role.valueOf(values[5]);
        if (role != Role.FAN){
            throw new IllegalArgumentException("Role in Fan CSV must be FAN");
        }
        LocalDate birthDate = values[8].isEmpty() ? null : LocalDate.parse(values[8]);

        return new Fan(values[0], values[1], values[2], values[3], values[4], role,
                Boolean.parseBoolean(values[6]), values[7], birthDate, Integer.parseInt(values[9]));
    
    }
}