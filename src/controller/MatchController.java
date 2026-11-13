package controller;

import model.Match;
import model.Seat;
import model.enums.MatchStatus;
import repository.MatchRepository;
import repository.SeatRepository;
import model.enums.SeatStatus;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MatchController {
    private MatchRepository matchRepository;
    private SeatRepository seatRepository;

    public MatchController() {
        this(new MatchRepository("data/matches.csv"), new SeatRepository("data/seats.csv"));
    }

    public MatchController(MatchRepository matchRepository) {
        this(matchRepository, new SeatRepository("data/seats.csv"));
    }

    public MatchController(MatchRepository matchRepository, SeatRepository seatRepository) {
        this.matchRepository = matchRepository;
        this.seatRepository = seatRepository != null
                ? seatRepository
                : new SeatRepository("data/seats.csv");
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

    public MatchStatus getCurrentStatus(Match match) {
        return getCurrentStatus(match, LocalDateTime.now());
    }

    public MatchStatus getCurrentStatus(Match match, LocalDateTime currentTime) {
        if (match == null) {
            throw new IllegalArgumentException("Match must not be null.");
        }
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time must not be null.");
        }
        if (match.getStatus() == MatchStatus.CANCELLED) {
            return MatchStatus.CANCELLED;
        }
        if (match.getMatchDate() == null || match.getMatchTime() == null
                || match.getMatchTime().trim().isEmpty()) {
            return MatchStatus.SCHEDULED;
        }

        LocalTime kickoffTime;
        try {
            kickoffTime = LocalTime.parse(match.getMatchTime().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid kickoff time for match "
                    + match.getMatchId() + ": " + match.getMatchTime(), e);
        }
        LocalDateTime kickoff = LocalDateTime.of(match.getMatchDate(), kickoffTime);
        if (currentTime.isBefore(kickoff)) {
            return MatchStatus.UPCOMING;
        }
        return currentTime.isBefore(kickoff.plusHours(2))
                ? MatchStatus.ONGOING
                : MatchStatus.FINISHED;
    }

    public Map<String, List<Seat>> getSeatsByMatch() throws IOException {
        Map<String, List<Seat>> seatsByMatch = new HashMap<>();
        for (Seat seat : seatRepository.findAll()) {
            if (seat.getMatchId() == null || seat.getMatchId().trim().isEmpty()) {
                continue;
            }
            String matchId = seat.getMatchId().toUpperCase(Locale.ROOT);
            List<Seat> matchSeats = seatsByMatch.get(matchId);
            if (matchSeats == null) {
                matchSeats = new ArrayList<>();
                seatsByMatch.put(matchId, matchSeats);
            }
            matchSeats.add(seat);
        }
        return seatsByMatch;
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
        return filterMatches(status, fromDate, toDate, null, null, null, false,
                LocalDateTime.now());
    }

    public List<Match> filterMatches(MatchStatus status, LocalDate fromDate, LocalDate toDate,
            LocalDateTime currentTime) throws IOException {
        return filterMatches(status, fromDate, toDate, null, null, null, false, currentTime);
    }

    public List<Match> filterMatches(MatchStatus status, LocalDate fromDate, LocalDate toDate,
            Double minPrice, Double maxPrice, String stadium, boolean onlyAvailableSeats,
            LocalDateTime currentTime) throws IOException {
        if (currentTime == null) {
            throw new IllegalArgumentException("Current time must not be null.");
        }
        if ((minPrice != null && (!Double.isFinite(minPrice) || minPrice < 0))
                || (maxPrice != null && (!Double.isFinite(maxPrice) || maxPrice < 0))) {
            throw new IllegalArgumentException("Ticket prices must be finite and non-negative.");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum price must not exceed maximum price.");
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        boolean filterByPrice = minPrice != null || maxPrice != null;
        Map<String, List<Seat>> seatsByMatch = onlyAvailableSeats
                ? getSeatsByMatch() : new HashMap<String, List<Seat>>();
        Set<String> matchesInPriceRange = new HashSet<>();
        if (filterByPrice) {
            for (Seat seat : seatRepository.findAll()) {
                if (seat.getMatchId() != null && !seat.getMatchId().trim().isEmpty()
                        && isWithinPriceRange(seat.getPrice(), minPrice, maxPrice)) {
                    matchesInPriceRange.add(seat.getMatchId().toUpperCase(Locale.ROOT));
                }
            }
        }
        String stadiumQuery = stadium == null ? "" : stadium.trim().toLowerCase(Locale.ROOT);

        List<Match> results = new ArrayList<>();
        for (Match match : matchRepository.findAll()) {
            boolean statusMatch = (status == null || getCurrentStatus(match, currentTime) == status);
            boolean dateMatch = (fromDate == null && toDate == null)
                    || (match.getMatchDate() != null
                            && (fromDate == null || !match.getMatchDate().isBefore(fromDate))
                            && (toDate == null || !match.getMatchDate().isAfter(toDate)));
            boolean priceMatch = !filterByPrice
                    || matchesInPriceRange.contains(match.getMatchId().toUpperCase(Locale.ROOT));
            boolean stadiumMatch = stadiumQuery.isEmpty()
                    || (match.getStadium() != null
                            && match.getStadium().toLowerCase(Locale.ROOT).contains(stadiumQuery));
            List<Seat> matchSeats = seatsByMatch.get(match.getMatchId().toUpperCase(Locale.ROOT));
            boolean seatsMatch = !onlyAvailableSeats
                    || (matchSeats != null && matchSeats.stream()
                            .anyMatch(seat -> seat.getStatus() == SeatStatus.AVAILABLE));
            if (statusMatch && dateMatch && priceMatch && stadiumMatch && seatsMatch) {
                results.add(match);
            }
        }
        return results;
    }

    private boolean isWithinPriceRange(double price, Double minPrice, Double maxPrice) {
        return Double.isFinite(price) && price >= 0
                && (minPrice == null || price >= minPrice)
                && (maxPrice == null || price <= maxPrice);
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
