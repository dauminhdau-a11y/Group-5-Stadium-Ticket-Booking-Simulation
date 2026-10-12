package com.view;

import java.util.Collection;
import java.util.List;

import com.model.MatchSeat;
import com.model.Seat;

public class SeatMapView {
    public void render(Collection<Seat> seats) {
        seats.forEach(seat -> System.out.println(seat.getSeatNumber() + " - " + seat.getStatus()));
    }

    public void renderMatchSeats(List<MatchSeat> seats) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats found for this match.");
            return;
        }
        System.out.printf("%-12s %-12s %-12s %s%n", "Section", "Seat", "Status", "Price");
        for (MatchSeat seat : seats) {
            System.out.printf("%-12s %-12s %-12s %,.0f%n", seat.getSectionId(), seat.getSeatNumber(),
                    seat.getStatus(), seat.getPrice());
        }
    }
}
