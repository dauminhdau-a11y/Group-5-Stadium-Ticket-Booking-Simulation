package strategy;

import exception.OptimisticLockException;
import exception.SeatAlreadyBookedException;
import model.Seat;
import model.Ticket;
import model.enums.SeatStatus;
import model.enums.TicketStatus;
import repository.SeatRepository;
import repository.TicketRepository;
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
