package com.ticketbooking.view;

import com.ticketbooking.controller.MatchController;
import com.ticketbooking.model.Match;
import java.util.List;
import java.util.Scanner;

public class MatchView {
    private MatchController matchController;
    private Scanner scanner;

    public MatchView(MatchController matchController, Scanner scanner) {
        this.matchController = matchController;
        this.scanner = scanner;
    }

    public MatchController getMatchController() {
        return matchController;
    }

    public void setMatchController(MatchController matchController) {
        this.matchController = matchController;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMatches(List<Match> matches) {
        if (matches == null || matches.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        System.out.println("\n------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-32s | %-12s | %-8s | %-20s%n", "Match ID", "Fixture", "Date", "Time", "Stadium");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (Match m : matches) {
            String fixture = m.getHomeTeam() + " vs " + m.getAwayTeam();
            String dateStr = m.getMatchDate() != null ? m.getMatchDate().toString() : "TBD";
            System.out.printf("%-10s | %-32s | %-12s | %-8s | %-20s%n",
                    m.getMatchId(), fixture, dateStr, m.getMatchTime(), m.getStadium());
        }
        System.out.println("------------------------------------------------------------------------------------------------");
    }

    public void displayMatchDetail(Match match) {
        if (match == null) {
            System.out.println("Match not found.");
            return;
        }
        System.out.println("\n=== MATCH DETAILS ===");
        System.out.println("Match ID: " + match.getMatchId());
        System.out.println("Teams:    " + match.getHomeTeam() + " vs " + match.getAwayTeam());
        System.out.println("Date:     " + (match.getMatchDate() != null ? match.getMatchDate() : "TBD"));
        System.out.println("Time:     " + match.getMatchTime());
        System.out.println("Stadium:  " + match.getStadium());
        System.out.println("Status:   " + match.getStatus());
        System.out.printf("Base Price: %,.0f VND%n", match.getBasePrice());
    }
}
