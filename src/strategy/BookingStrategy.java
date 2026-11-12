package strategy;

import model.Seat;
import repository.SeatRepository;
import repository.TicketRepository;

public interface BookingStrategy {
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception;
}
