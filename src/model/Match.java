package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Match extends BaseEntity {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDate matchDate;
    private String matchTime;
    private String stadium;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Match() {
    }

    public Match(String matchId, String homeTeam, String awayTeam, LocalDate matchDate, String matchTime, String stadium) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.stadium = stadium;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getTitle() {
        return homeTeam + " vs " + awayTeam;
    }

    @Override
    public String toCsvLine() {
        return matchId + "," + homeTeam + "," + awayTeam + "," +
                (matchDate != null ? matchDate.format(DATE_FORMATTER) : "") + "," +
                matchTime + "," + stadium;
    }

    @Override
    public void fromCsvLine(String csvLine) {
        if (csvLine != null && !csvLine.trim().isEmpty()) {
            String[] parts = csvLine.split(",");
            if (parts.length >= 6) {
                this.matchId = parts[0].trim();
                this.homeTeam = parts[1].trim();
                this.awayTeam = parts[2].trim();
                try {
                    this.matchDate = LocalDate.parse(parts[3].trim(), DATE_FORMATTER);
                } catch (Exception e) {
                    this.matchDate = LocalDate.now();
                }
                this.matchTime = parts[4].trim();
                this.stadium = parts[5].trim();
            }
        }
    }
}
