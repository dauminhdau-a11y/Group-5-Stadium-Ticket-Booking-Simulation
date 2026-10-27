package com.ticketbooking.strategy;

import com.ticketbooking.exception.OptimisticLockException;
import com.ticketbooking.exception.SeatAlreadyBookedException;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.TicketRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public class OptimisticStrategy implements BookingStrategy {
    public OptimisticStrategy() {
    }

    @Override
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception {
        long currentVersion = seat.getVersion();
        Seat latestSeat = seatRepo.findById(seat.getSeatId());
        if (latestSeat == null) {
            latestSeat = seat;
        }

        if (latestSeat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
        }

        if (latestSeat.getVersion() != currentVersion) {
            throw new OptimisticLockException("Concurrent modification detected on seat " + seat.getSeatId());
        }

        Thread.sleep(1);
        latestSeat.setStatus(SeatStatus.BOOKED);
        seatRepo.save(latestSeat);

        String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket ticket = new Ticket(ticketId, matchId, seat.getSeatId(), fanId, price,
                TicketStatus.SOLD, LocalDateTime.now().toString());
        ticketRepo.save(ticket);
        return true;
    }
}
