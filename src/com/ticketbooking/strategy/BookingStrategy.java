package com.ticketbooking.strategy;

import com.ticketbooking.model.Seat;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.TicketRepository;

public interface BookingStrategy {
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception;
}
