package com.ticketbooking.strategy;

import com.ticketbooking.exception.SeatAlreadyBookedException;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.TicketRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public class NoLockStrategy implements BookingStrategy {
    public NoLockStrategy() {
    }

    @Override
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception {
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
        }
        Thread.sleep(1);
        seat.setStatus(SeatStatus.BOOKED);
        seatRepo.save(seat);

        String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket ticket = new Ticket(ticketId, matchId, seat.getSeatId(), fanId, price,
                TicketStatus.SOLD, LocalDateTime.now().toString());
        ticketRepo.save(ticket);
        return true;
    }
}
