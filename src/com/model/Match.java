package com.model;

import java.time.LocalDateTime;
import java.util.List;

import com.util.CsvParser;

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
    @Override public String toCsvLine() {
        return CsvParser.encode(matchId, startTime == null ? "" : startTime.toString());
    }
    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        matchId = values.get(0); startTime = LocalDateTime.parse(values.get(1));
    }
}
