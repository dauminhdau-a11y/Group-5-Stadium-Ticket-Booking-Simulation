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

public class SynchronizedStrategy implements BookingStrategy {
    private Object lockObject;

    public SynchronizedStrategy() {
        this.lockObject = new Object();
    }

    public Object getLockObject() {
        return lockObject;
    }

    public void setLockObject(Object lockObject) {
        this.lockObject = lockObject;
    }

    @Override
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception {
        synchronized (lockObject) {
            Seat currentSeat = seatRepo.findById(seat.getSeatId());
            if (currentSeat != null && currentSeat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
            }
            seat.setStatus(SeatStatus.BOOKED);
            seatRepo.save(seat);

            String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket ticket = new Ticket(ticketId, matchId, seat.getSeatId(), fanId, price,
                    TicketStatus.SOLD, LocalDateTime.now().toString());
            ticketRepo.save(ticket);
            return true;
        }
    }
}
