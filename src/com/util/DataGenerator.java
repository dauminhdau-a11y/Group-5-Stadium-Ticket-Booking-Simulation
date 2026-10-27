package com.util;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.model.Match;
import com.model.MatchSeat;
import com.model.Seat;
import com.model.enums.SeatStatus;
import com.model.enums.SeatZone;

public final class DataGenerator {
    private DataGenerator() { }
    public static List<Seat> sampleSeats(String matchId, int count) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= count; i++) seats.add(new Seat("S" + i, matchId, "A", Integer.toString(i), 100.0));
        return seats;
    }

    public static List<Match> sampleMatches(int count) {
        List<Match> matches = new ArrayList<Match>();
        for (int i = 1; i <= count; i++) {
            String matchId = String.format("M-%03d", i);
            matches.add(new Match(matchId, LocalDateTime.now().plusDays(i)));
        }
        return matches;
    }

    public static List<MatchSeat> sampleMatchSeats(List<Match> matches, int seatsPerMatch) {
        List<MatchSeat> seats = new ArrayList<MatchSeat>(matches.size() * seatsPerMatch);
        for (Match match : matches) {
            for (int number = 1; number <= seatsPerMatch; number++) {
                SeatZone zone = SeatZone.forSeatNumber(number);
                MatchSeat seat = createPhysicalSeat(match.getMatchId(), number, zone);
                if ("M-001".equals(match.getMatchId()) && number <= 2) {
                    seat.setStatus(SeatStatus.BOOKED);
                }
                seats.add(seat);
            }
        }
        return seats;
    }

    private static MatchSeat createPhysicalSeat(String matchId, int number, SeatZone zone) {
        int zoneStart;
        String blockId;
        int rowNumber;
        int seatNumber;

        switch (zone) {
            case VIP:
                zoneStart = number - 1;
                blockId = "VIP-C";
                rowNumber = zoneStart / 10 + 1;
                seatNumber = zoneStart % 10 + 1;
                break;
            case NEAR:
                zoneStart = number - 101;
                blockId = String.format("NEAR-%02d", zoneStart / 50 + 1);
                rowNumber = zoneStart % 50 / 10 + 1;
                seatNumber = zoneStart % 10 + 1;
                break;
            case EAST_WING:
                zoneStart = number - 351;
                blockId = "EAST-01";
                rowNumber = zoneStart / 25 + 1;
                seatNumber = zoneStart % 25 + 1;
                break;
            case WEST_WING:
                zoneStart = number - 526;
                blockId = "WEST-01";
                rowNumber = zoneStart / 25 + 1;
                seatNumber = zoneStart % 25 + 1;
                break;
            default:
                zoneStart = number - 701;
                blockId = String.format("FAR-%02d", zoneStart / 100 + 1);
                rowNumber = zoneStart % 100 / 10 + 1;
                seatNumber = zoneStart % 10 + 1;
                break;
        }

        return new MatchSeat(matchId + "-S-" + number, matchId, "S-" + number,
                zone.getSectionId(), blockId, rowNumber, String.format("S%02d", seatNumber), zone.getPrice());
    }
}
