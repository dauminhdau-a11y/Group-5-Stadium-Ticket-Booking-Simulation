package com.stadium.model;

import java.time.LocalDateTime;

public class Match extends SimpleEntity {
    private String matchId;
    private LocalDateTime startTime;
    public Match() { }
    public Match(String matchId, LocalDateTime startTime) {
        this.matchId = matchId;
        this.startTime = startTime;
    }
    @Override public String getId() { return matchId; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public boolean removeMatch() { return false; }
    public boolean createMatch() { return false; }
    public boolean updateMatch() { return false; }
    @Override public String toCsvLine() { return matchId + "," + startTime; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        matchId = values[0]; startTime = LocalDateTime.parse(values[1]);
    }
}
