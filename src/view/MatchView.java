package view;

import controller.MatchController;
import model.Match;
import model.Seat;
import model.enums.MatchStatus;
import model.enums.SeatStatus;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public void displayMatches(List<Match> matches) throws IOException {
        displayMatches(matches, LocalDateTime.now());
    }

    public void displayMatches(List<Match> matches, LocalDateTime currentTime) throws IOException {
        if (matches == null || matches.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        Map<String, List<Seat>> seatsByMatch = matchController.getSeatsByMatch();
        String separator = repeat("-", 137);
        System.out.println("\n" + separator);
        System.out.printf("%-10s | %-43s | %-28s | %-16s | %-12s | %-10s%n",
                "Match ID", "Fixture", "Stadium", "Kickoff", "Seats Left", "Status");
        System.out.println(separator);
        for (Match m : matches) {
            String fixture = m.getTitle();
            String kickoff = m.getMatchDate() == null || m.getMatchTime() == null
                    || m.getMatchTime().trim().isEmpty()
                    ? "TBD"
                    : m.getMatchDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            + " " + m.getMatchTime();
            List<Seat> matchSeats = seatsByMatch.get(m.getMatchId().toUpperCase(Locale.ROOT));
            MatchStatus status = matchController.getCurrentStatus(m, currentTime);
            int availableSeats = 0;
            if (status != MatchStatus.FINISHED && matchSeats != null) {
                for (Seat seat : matchSeats) {
                    if (seat.getStatus() == SeatStatus.AVAILABLE) {
                        availableSeats++;
                    }
                }
            }
            String seatsLeft = availableSeats + "/" + (matchSeats == null ? 0 : matchSeats.size());
            System.out.printf("%-10s | %-43s | %-28s | %-16s | %-12s | %-10s%n",
                    m.getMatchId(), fixture, m.getStadium() == null ? "-" : m.getStadium(), kickoff,
                    seatsLeft, status);
        }
        System.out.println(separator);
    }

    public void filterMatches() throws IOException {
        System.out.println("\n--- Filter Matches ---");
        System.out.println("Status: 1. SCHEDULED  2. UPCOMING  3. ONGOING  4. FINISHED  5. CANCELLED  6. Any");
        System.out.print("Choose status (1-6): ");
        String statusChoice = readLine();
        statusChoice = statusChoice == null ? null : statusChoice.trim();

        try {
            MatchStatus status;
            if ("1".equals(statusChoice)) status = MatchStatus.SCHEDULED;
            else if ("2".equals(statusChoice)) status = MatchStatus.UPCOMING;
            else if ("3".equals(statusChoice)) status = MatchStatus.ONGOING;
            else if ("4".equals(statusChoice)) status = MatchStatus.FINISHED;
            else if ("5".equals(statusChoice)) status = MatchStatus.CANCELLED;
            else if ("6".equals(statusChoice)) status = null;
            else throw new IllegalArgumentException("Choose a status from 1 to 6.");

            LocalDate fromDate = readOptionalDate("From date (YYYY-MM-DD, blank for any): ");
            LocalDate toDate = readOptionalDate("To date (YYYY-MM-DD, blank for any): ");
            Double minPrice = readOptionalPrice("Minimum ticket price (VND, blank for any): ");
            Double maxPrice = readOptionalPrice("Maximum ticket price (VND, blank for any): ");

            System.out.print("Stadium contains (blank for any): ");
            String stadium = readLine();
            System.out.print("Only matches with available seats? (1. Yes  2. Any): ");
            String seatChoice = readLine();
            boolean onlyAvailableSeats;
            if (seatChoice == null || seatChoice.trim().isEmpty() || "2".equals(seatChoice.trim())) {
                onlyAvailableSeats = false;
            } else if ("1".equals(seatChoice.trim())) {
                onlyAvailableSeats = true;
            } else {
                throw new IllegalArgumentException("Choose 1 or 2 for seat availability.");
            }

            List<Match> matches = matchController.filterMatches(status, fromDate, toDate,
                    minPrice, maxPrice, stadium, onlyAvailableSeats, LocalDateTime.now());
            displayMatches(matches);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            System.out.println("Invalid filter: " + e.getMessage());
        }
    }

    public LocalDate readOptionalDate(String prompt) {
        System.out.print(prompt);
        String value = readLine();
        return value == null || value.trim().isEmpty()
                ? null : LocalDate.parse(value.trim());
    }

    public Double readOptionalPrice(String prompt) {
        System.out.print(prompt);
        String value = readLine();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        double price = Double.parseDouble(value.trim());
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be finite and non-negative.");
        }
        return price;
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    public String repeat(String value, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(value);
        }
        return result.toString();
    }
}
