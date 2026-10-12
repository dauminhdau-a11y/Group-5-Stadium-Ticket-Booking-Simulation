package com.repository.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.model.MatchSeat;

public class MatchSeatRepository extends AbstractCsvRepository<MatchSeat> {
    public MatchSeatRepository() {
        super(MatchSeat.class, "seats.csv");
    }

    public MatchSeatRepository(Path dataDirectory) {
        super(MatchSeat.class, dataDirectory.resolve("seats.csv"));
    }

    public MatchSeat findByMatchAndSeat(String matchId, String seatId) {
        for (MatchSeat seat : findAll()) {
            if (seat.getMatchId().equals(matchId) && seat.getSeatId().equals(seatId)) {
                return seat;
            }
        }
        return null;
    }

    public List<MatchSeat> findByMatch(String matchId) {
        List<MatchSeat> result = new ArrayList<MatchSeat>();
        for (MatchSeat seat : findAll()) {
            if (seat.getMatchId().equals(matchId)) {
                result.add(seat);
            }
        }
        return result;
    }
}
