package com.ticketbooking.model;

import com.ticketbooking.model.enums.MatchStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Match extends BaseEntity {
    private String matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDate matchDate;
    private String matchTime;
    private String stadium;
    private MatchStatus status;
    private double basePrice;

    public Match() {
        super();
        this.status = MatchStatus.SCHEDULED;
        this.basePrice = 200000.0;
    }

    public Match(String matchId, String homeTeam, String awayTeam, LocalDate matchDate,
            String matchTime, String stadium) {
        super(matchId);
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.stadium = stadium;
        this.status = MatchStatus.SCHEDULED;
        this.basePrice = 200000.0;
    }

    public Match(String matchId, String homeTeam, String awayTeam, LocalDate matchDate,
            String matchTime, String stadium, MatchStatus status, double basePrice) {
        super(matchId);
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.matchTime = matchTime;
        this.stadium = stadium;
        this.status = status;
        this.basePrice = basePrice;
    }

    public String getMatchId() {
        return matchId != null ? matchId : getId();
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
        setId(matchId);
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

    public MatchStatus getStatus() {
        return status != null ? status : MatchStatus.SCHEDULED;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getTitle() {
        return (homeTeam != null ? homeTeam : "Team 1") + " vs " + (awayTeam != null ? awayTeam : "Team 2");
    }

    @Override
    public String toCsvLine() {
        return String.join(",", getMatchId(), homeTeam != null ? homeTeam : "",
                awayTeam != null ? awayTeam : "",
                matchDate == null ? "" : matchDate.toString(),
                matchTime != null ? matchTime : "",
                stadium != null ? stadium : "");
    }

    public Match fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid match CSV data.");
        }
        LocalDate date;
        try {
            date = parts[3].trim().isEmpty() ? null
                    : LocalDate.parse(parts[3].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid match date: " + parts[3], e);
        }
        this.matchId = parts[0].trim();
        this.homeTeam = parts[1].trim();
        this.awayTeam = parts[2].trim();
        this.matchDate = date;
        this.matchTime = parts[4].trim();
        this.stadium = parts[5].trim();
        setId(matchId);
        return this;
    }
}