package com.stadium.util;

import com.stadium.model.Seat;
import java.util.ArrayList;
import java.util.List;

public final class DataGenerator {
    private DataGenerator() { }
    public static List<Seat> sampleSeats(String matchId, int count) {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= count; i++) seats.add(new Seat("S" + i, matchId, "A", Integer.toString(i), 100.0));
        return seats;
    }
}
