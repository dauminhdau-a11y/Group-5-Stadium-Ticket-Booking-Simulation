package model;

public class Fan extends BaseEntity{
    private String fanid;
    private String username;
    private String password;
    private String email;

    public Fan(String fanid, String username, String password, String email) {
        this.fanid = fanid;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String toCsvLine() {
        return fanid + "," + username + "," + password + "," + email;
    }
    @Override 
    public void fromCsvLine(String csvLine){
        if(csvLine != null && !csvLine.trim().isEmpty()){
            String[] parts = csvLine.split(",");
            if(parts.length == 4){
                this.fanid = parts[0].trim();
                this.username = parts[1].trim();
                this.password = parts[2].trim();
                this.email = parts[3].trim();
            }
        }
    }

}
