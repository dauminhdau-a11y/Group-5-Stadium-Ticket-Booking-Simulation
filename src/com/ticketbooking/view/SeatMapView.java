package com.ticketbooking.view;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.enums.SeatStatus;
import java.util.List;

public class SeatMapView {
    public SeatMapView() {
    }

    public void displaySeatMap(String matchId, List<Seat> seats) {
        System.out.println("\n========== SEAT MAP FOR MATCH: " + matchId + " ==========");
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats available to display.");
            return;
        }

        int availableCount = 0;
        int bookedCount = 0;
        int total = seats.size();

        int printedInLine = 0;
        for (Seat seat : seats) {
            String statusSymbol;
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                statusSymbol = "[O] " + seat.getSeatId();
                availableCount++;
            } else {
                statusSymbol = "[X] " + seat.getSeatId();
                bookedCount++;
            }
            System.out.printf("%-16s", statusSymbol);
            printedInLine++;
            if (printedInLine % 6 == 0) {
                System.out.println();
            }
            if (printedInLine >= 120) {
                System.out.println("... (" + (total - printedInLine) + " more seats)");
                break;
            }
        }
        if (printedInLine % 6 != 0) {
            System.out.println();
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("Legend: [O] AVAILABLE   [X] BOOKED / LOCKED");
        System.out.printf("Total Seats: %d | Available: %d | Booked: %d%n", total, availableCount, bookedCount);
        System.out.println("==========================================================");
    }
}
