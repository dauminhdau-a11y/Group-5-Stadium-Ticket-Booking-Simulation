package com.ticketbooking.model;

import com.ticketbooking.model.enums.Role;

public class Account extends BaseEntity{
    private String accountId;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;
    private boolean locked;
    
    public Account(){
        this.locked=false;
    }

    public Account(String id, String accountId, String username, String passwordHash, String email, Role role, boolean locked){
        super(id);
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.locked = locked;
    }
    public String getAccountId(){
        return getId();

    }
    public void setAccountId(String accountId){
        setId(accountId);
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public Role getRole(){
        return role;
    }
    public void setRole(Role role){
        this.role = role;
    }
    public String getFullName(){
        return username;
    }
    public void setFullName(String fullName){
        this.username = fullName;
    }
    public boolean isLocked(){
        return locked;
    }
    public void setLocked(boolean locked){
        this.locked = locked;
    }
    @Override 
    public String toCsvLine(){
        return getId()+","+accountId+","+username+","+passwordHash+","+email+","+role+","+locked;
    }
    @Override 
    public Account fromCsvLine(String line ){
        String[] values = line.split(",",-1);
        if (values.length != 7){
            throw new IllegalArgumentException("Invalid Account CSV data");
        }
        return new Account(values[0], values[1], values[2], values[3], values[4], Role.valueOf(values[5]), Boolean.parseBoolean(values[6]));
    
    }
}