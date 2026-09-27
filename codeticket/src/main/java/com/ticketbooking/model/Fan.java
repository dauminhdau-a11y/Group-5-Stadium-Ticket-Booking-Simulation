package com.ticketbooking.model;

public class Fan {
    private String fanId;
    private String username;
    private String password;
    private String email;

    public Fan(){

    }
    public Fan(String fanId, String username, String password, String email){
        this.fanId = fanId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String getFanId(){
        return fanId;
    }
    public void setFanId(String fanId){
        this.fanId = fanId;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String toCsvLine(){
        return String.join(",",escape(fanId),escape(username),escape(password),escape(email));
    }
    public void fromCsvLine(String csvLine){
        if (csvLine==null||csvLine.trim().isEmpty()){
            throw new IllegalArgumentException("CSV line cannot be empty.");
        }
        String[] parts = csvLine.split(",",-1);
        if (parts.length!=4){
            throw new IllegalArgumentException("Invalid Fan CSV. Expected 4 fields but got "+parts.length);
        }
        fanId = unescape(parts[0].trim());
        username = unescape(parts[1].trim());
        password = unescape(parts[2].trim());
        email = unescape(parts[3].trim());
    }
    public String escape(String value){
        if (value==null){
            return "";
        }
        return value.replace("\\","\\\\").replace(",","\\,");
    }
    public String unescape(String value){
        if (value==null){
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean isEscaping = false;
        for (char c : value.toCharArray()){
            if (isEscaping){
                result.append(c);
                isEscaping = false;
            } else if (c=='\\'){
                isEscaping = true;
            } else {
                result.append(c);
            }
        }
        if (isEscaping){
            result.append('\\');
        }
        return result.toString();
    }
    @Override 
    public String toString(){
        return "Fan{"+"fanId='"+fanId+'\''+", username='"+username+'\''+",email='"+email+'\''+'}';
    }
}