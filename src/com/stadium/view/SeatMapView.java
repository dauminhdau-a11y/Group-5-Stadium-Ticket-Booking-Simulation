package com.stadium.view;

import com.stadium.model.Seat;
import java.util.Collection;

public class SeatMapView {
    public void render(Collection<Seat> seats) {
        seats.forEach(seat -> System.out.println(seat.getSeatNumber() + " - " + seat.getStatus()));
    }
}
