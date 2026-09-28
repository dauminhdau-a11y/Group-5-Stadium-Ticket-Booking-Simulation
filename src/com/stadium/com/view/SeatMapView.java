package com.stadium.com.view;

import java.util.Collection;

import com.stadium.com.model.Seat;

public class SeatMapView {
    public void render(Collection<Seat> seats) {
        seats.forEach(seat -> System.out.println(seat.getSeatNumber() + " - " + seat.getStatus()));
    }
}
