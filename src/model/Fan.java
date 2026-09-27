package model;

public class Fan extends BaseEntity{
    private String fanId;
    private String username;
    private String password;
    private String email;

    public Fan(String fanId, String username, String password, String email) {
        this.fanId = fanId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String getFanId() {
        return fanId;
    }
    public void setFanId(String fanId){
        this.fanId = fanId;
    }
    @Override 
    public String toCsvLine() {
        return fanId + "," + username + "," + password + "," + email;
    }
    @Override 
    public void fromCsvLine(String csvLine){
        if(csvLine != null && !csvLine.trim().isEmpty()){
            String[] parts = csvLine.split(",");
            if(parts.length == 4){
                this.fanId = parts[0].trim();
                this.username = parts[1].trim();
                this.password = parts[2].trim();
                this.email = parts[3].trim();
            }
        }
    }

}
