package strategy;

import exception.SeatAlreadyBookedException;
import model.Seat;
import model.Ticket;
import model.enums.SeatStatus;
import model.enums.TicketStatus;
import repository.SeatRepository;
import repository.TicketRepository;
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
