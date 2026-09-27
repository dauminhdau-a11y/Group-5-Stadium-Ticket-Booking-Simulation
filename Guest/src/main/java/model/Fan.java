
package model;
public class Fan{
private String fanId;
private String username;
private String fullName;
private String email;
private String phone;
private String password;

public Fan(){}

public Fan(String fanId, String username, String fullName, String email, String phone, String password) {
        this.fanId = fanId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public static Fan fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Fan(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim());
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%s", fanId, username, fullName, email, phone, password);
    }

    // Getters & Setters
    public String getFanId() { return fanId; }
    public void setFanId(String fanId) { this.fanId = fanId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password;}
}