package controller;

import model.Seat;
import model.Ticket;
import model.Transaction;
import model.enums.TicketStatus;
import model.enums.TransactionStatus;
import repository.TicketRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TicketController {
    private TicketRepository ticketRepository;
    private TransactionController transactionController;

    public TicketController() {
        this(new TicketRepository("data/tickets.csv"));
    }

    public TicketController(TicketRepository ticketRepository) {
        this(ticketRepository, null);
    }

    public TicketController(TicketRepository ticketRepository, TransactionController transactionController) {
        this.ticketRepository = ticketRepository;
        this.transactionController = transactionController;
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }

    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() throws IOException {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(String ticketId) throws IOException {
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> getTicketsByMatch(String matchId) throws IOException {
        return ticketRepository.findByMatchId(matchId);
    }

    public List<Ticket> getTicketsByFan(String fanId) throws IOException {
        return ticketRepository.findByFanId(fanId);
    }

    public boolean createTicket(Ticket ticket) throws IOException {
        if (ticket == null || ticket.getTicketId() == null || ticket.getTicketId().trim().isEmpty()) {
            return false;
        }
        if (ticketRepository.findById(ticket.getTicketId().trim()) != null) {
            return false;
        }
        ticketRepository.save(ticket);
        return true;
    }

    public int generateTicketsForMatch(String matchId, String sectionId, double overridePrice, SeatController seatController) throws IOException {
        if (matchId == null || matchId.trim().isEmpty() || seatController == null) {
            return 0;
        }
        String mId = matchId.trim();
        List<Seat> targetSeats = (sectionId != null && !sectionId.trim().isEmpty())
                ? seatController.getSeatsBySection(mId, sectionId.trim())
                : seatController.getSeatsByMatch(mId);

        if (targetSeats.isEmpty()) {
            return 0;
        }

        // Collect existing ticket seat IDs for this match
        Set<String> existingSeatIds = new HashSet<>();
        for (Ticket t : getTicketsByMatch(mId)) {
            if (t.getSeatId() != null) {
                existingSeatIds.add(t.getSeatId());
            }
        }

        int count = 0;
        for (Seat seat : targetSeats) {
            if (!existingSeatIds.contains(seat.getSeatId())) {
                String tckId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
                double finalPrice = overridePrice > 0 ? overridePrice : seat.getPrice();
                Ticket ticket = new Ticket(tckId, mId, seat.getSeatId(), finalPrice, TicketStatus.AVAILABLE);
                ticketRepository.save(ticket);
                existingSeatIds.add(seat.getSeatId());
                count++;
            }
        }
        return count;
    }

    public boolean updateTicketPrice(String ticketId, double newPrice) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || newPrice < 0) {
            return false;
        }
        ticket.setPrice(newPrice);
        ticketRepository.save(ticket);
        return true;
    }

    public int updateTicketPricesBySection(String matchId, String sectionId, double newPrice, SeatController seatController) throws IOException {
        if (matchId == null || sectionId == null || seatController == null || newPrice < 0) {
            return 0;
        }
        List<Seat> seats = seatController.getSeatsBySection(matchId.trim(), sectionId.trim());
        Set<String> seatIds = new HashSet<>();
        for (Seat s : seats) {
            seatIds.add(s.getSeatId());
        }

        int count = 0;
        for (Ticket t : getTicketsByMatch(matchId.trim())) {
            if (t.getSeatId() != null && seatIds.contains(t.getSeatId())) {
                t.setPrice(newPrice);
                ticketRepository.save(t);
                count++;
            }
        }
        return count;
    }

    public boolean removeTicket(String ticketId) throws IOException {
        return ticketRepository.deleteById(ticketId);
    }

    public int removeTicketsBySection(String matchId, String sectionId, SeatController seatController) throws IOException {
        if (matchId == null || sectionId == null || seatController == null) {
            return 0;
        }
        List<Seat> seats = seatController.getSeatsBySection(matchId.trim(), sectionId.trim());
        Set<String> seatIds = new HashSet<>();
        for (Seat s : seats) {
            seatIds.add(s.getSeatId());
        }

        int count = 0;
        for (Ticket t : getTicketsByMatch(matchId.trim())) {
            if (t.getSeatId() != null && seatIds.contains(t.getSeatId())) {
                if (ticketRepository.deleteById(t.getTicketId())) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean refundTicket(String ticketId) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || transactionController == null
                || (ticket.getStatus() != TicketStatus.SOLD && ticket.getStatus() != TicketStatus.ACTIVE)) {
            return false;
        }
        String transactionId = ticket.getTransactionId();
        Transaction transaction = transactionId == null || transactionId.trim().isEmpty()
                ? null : transactionController.getTransactionRepo().findById(transactionId);
        if (transactionId == null || transactionId.trim().isEmpty()) {
            for (Transaction txn : transactionController.getAllTransactions()) {
                if ((txn.getStatus() == TransactionStatus.CONFIRMED
                        || txn.getStatus() == TransactionStatus.SUCCESS)
                        && ticket.getMatchId() != null
                        && ticket.getMatchId().equalsIgnoreCase(txn.getMatchId())
                        && txn.getSeatIds().contains(ticket.getSeatId())) {
                    transactionId = txn.getTransactionId();
                    transaction = txn;
                    break;
                }
            }
        }
        if (transaction == null) {
            return false;
        }
        return transactionController.refundTicket(transaction.getFanId(), ticketId).isSuccessful();
    }
}
