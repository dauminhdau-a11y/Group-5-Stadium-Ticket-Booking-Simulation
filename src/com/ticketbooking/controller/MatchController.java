package com.ticketbooking.controller;

import com.ticketbooking.model.Match;
import com.ticketbooking.model.enums.MatchStatus;
import com.ticketbooking.repository.MatchRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatchController {
    private MatchRepository matchRepository;

    public MatchController() {
        this(new MatchRepository("data/matches.csv"));
    }

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public MatchRepository getMatchRepository() {
        return matchRepository;
    }

    public void setMatchRepository(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getAllMatches() throws IOException {
        return matchRepository.findAll();
    }

    public Match getMatchById(String matchId) throws IOException {
        return matchRepository.findById(matchId);
    }

    public List<Match> searchMatches(String keyword) throws IOException {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Match> results = new ArrayList<>();
        for (Match match : matchRepository.findAll()) {
            if (query.isEmpty()
                    || (match.getHomeTeam() != null && match.getHomeTeam().toLowerCase(Locale.ROOT).contains(query))
                    || (match.getAwayTeam() != null && match.getAwayTeam().toLowerCase(Locale.ROOT).contains(query))
                    || (match.getStadium() != null && match.getStadium().toLowerCase(Locale.ROOT).contains(query))
                    || (match.getMatchId() != null && match.getMatchId().toLowerCase(Locale.ROOT).contains(query))) {
                results.add(match);
            }
        }
        return results;
    }

    public List<Match> filterMatches(MatchStatus status, LocalDate fromDate, LocalDate toDate) throws IOException {
        List<Match> results = new ArrayList<>();
        for (Match match : matchRepository.findAll()) {
            boolean statusMatch = (status == null || match.getStatus() == status);
            boolean dateMatch = true;
            if (match.getMatchDate() != null) {
                if (fromDate != null && match.getMatchDate().isBefore(fromDate)) {
                    dateMatch = false;
                }
                if (toDate != null && match.getMatchDate().isAfter(toDate)) {
                    dateMatch = false;
                }
            }
            if (statusMatch && dateMatch) {
                results.add(match);
            }
        }
        return results;
    }

    public boolean createMatch(Match match) throws IOException {
        if (match == null || match.getMatchId() == null || match.getMatchId().trim().isEmpty()) {
            return false;
        }
        if (matchRepository.findById(match.getMatchId()) != null) {
            return false;
        }
        matchRepository.save(match);
        return true;
    }

    public boolean updateMatchDetail(String matchId, String homeTeam, String awayTeam, String stadium) throws IOException {
        Match match = matchRepository.findById(matchId);
        if (match == null) {
            return false;
        }
        if (homeTeam != null && !homeTeam.trim().isEmpty()) {
            match.setHomeTeam(homeTeam.trim());
        }
        if (awayTeam != null && !awayTeam.trim().isEmpty()) {
            match.setAwayTeam(awayTeam.trim());
        }
        if (stadium != null && !stadium.trim().isEmpty()) {
            match.setStadium(stadium.trim());
        }
        matchRepository.save(match);
        return true;
    }

    public boolean updateMatchSchedule(String matchId, LocalDate newDate, String newTime) throws IOException {
        Match match = matchRepository.findById(matchId);
        if (match == null) {
            return false;
        }
        if (newDate != null) {
            match.setMatchDate(newDate);
        }
        if (newTime != null && !newTime.trim().isEmpty()) {
            match.setMatchTime(newTime.trim());
        }
        matchRepository.save(match);
        return true;
    }

    public boolean removeMatch(String matchId) throws IOException {
        return matchRepository.deleteById(matchId);
    }
}
