package model;
public class Match{
private String matchId;
private String matchName;
private String stadiumId;
private String matchDate;
private String homeTeam;
private String awayTeam;


public Match(){}

public Match(String matchId, String matchName, String stadiumId, String matchDate, String homeTeam, String awayTeam) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.stadiumId = stadiumId;
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public static Match fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Match(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim());
    }

    public String getMatchId() { return matchId; }
    public String getMatchName() { return matchName; }
    public String getStadiumId() { return stadiumId; }
    public String getMatchDate() { return matchDate; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam(){ return awayTeam;}
}
