package com.ticketbooking.controller;

import com.ticketbooking.model.Match;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.enums.PaymentMethod;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.model.enums.TransactionStatus;
import com.ticketbooking.repository.MatchRepository;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.SupportRequestRepository;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.TransactionRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffController {
    private SeatRepository seatRepo;
    private TicketRepository ticketRepo;
    private TransactionRepository transactionRepo;
    private MatchRepository matchRepo;
    private SupportRequestRepository supportRepo;

    public StaffController() {
        this(new SeatRepository("data/seats.csv"),
                new TicketRepository("data/tickets.csv"),
                new TransactionRepository("data/transactions.csv"),
                new MatchRepository("data/matches.csv"),
                new SupportRequestRepository("data/support_requests.csv"));
    }

    public StaffController(SeatRepository seatRepo, TicketRepository ticketRepo,
            TransactionRepository transactionRepo, MatchRepository matchRepo,
            SupportRequestRepository supportRepo) {
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.transactionRepo = transactionRepo;
        this.matchRepo = matchRepo;
        this.supportRepo = supportRepo;
    }

    public SeatRepository getSeatRepo() {
        return seatRepo;
    }

    public void setSeatRepo(SeatRepository seatRepo) {
        this.seatRepo = seatRepo;
    }

    public TicketRepository getTicketRepo() {
        return ticketRepo;
    }

    public void setTicketRepo(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public TransactionRepository getTransactionRepo() {
        return transactionRepo;
    }

    public void setTransactionRepo(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public MatchRepository getMatchRepo() {
        return matchRepo;
    }

    public void setMatchRepo(MatchRepository matchRepo) {
        this.matchRepo = matchRepo;
    }

    public SupportRequestRepository getSupportRepo() {
        return supportRepo;
    }

    public void setSupportRepo(SupportRequestRepository supportRepo) {
        this.supportRepo = supportRepo;
    }

    public List<Seat> getTicketFeeList(String matchId) throws IOException {
        List<Seat> result = new ArrayList<>();
        if (matchId == null) {
            return seatRepo.findAll();
        }
        for (Seat seat : seatRepo.findAll()) {
            if (matchId.equalsIgnoreCase(seat.getMatchId())) {
                result.add(seat);
            }
        }
        return result;
    }

    public Transaction sellTicketsOffline(String customerIdentifier, String matchId,
            List<String> seatIds, PaymentMethod method) throws IOException {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats selected for offline sale.");
        }
        double total = 0.0;
        List<Seat> selectedSeats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat not found: " + seatId);
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new IllegalArgumentException("Seat " + seatId + " is not available.");
            }
            selectedSeats.add(seat);
            total += seat.getPrice();
        }

        for (Seat s : selectedSeats) {
            s.setStatus(SeatStatus.BOOKED);
            seatRepo.save(s);

            String tckId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket tck = new Ticket(tckId, matchId, s.getSeatId(), customerIdentifier,
                    s.getPrice(), TicketStatus.SOLD, LocalDateTime.now().toString());
            ticketRepo.save(tck);
        }

        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        Transaction txn = new Transaction(
                txnId,
                customerIdentifier,
                matchId,
                total,
                LocalDateTime.now(),
                TransactionStatus.CONFIRMED,
                seatIds,
                method != null ? method : PaymentMethod.CASH,
                "",
                0.0);
        transactionRepo.save(txn);
        return txn;
    }

    public boolean confirmReservationPayment(String transactionId) throws IOException {
        Transaction txn = transactionRepo.findById(transactionId);
        if (txn == null) {
            return false;
        }
        txn.setStatus(TransactionStatus.CONFIRMED);
        transactionRepo.save(txn);
        return true;
    }

    public boolean confirmTicketRefund(String ticketId) throws IOException {
        Ticket ticket = ticketRepo.findById(ticketId);
        if (ticket == null || ticket.getStatus() == TicketStatus.REFUNDED) {
            return false;
        }
        ticket.setStatus(TicketStatus.REFUNDED);
        ticketRepo.save(ticket);

        if (ticket.getSeatId() != null) {
            Seat seat = seatRepo.findById(ticket.getSeatId());
            if (seat != null) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepo.save(seat);
            }
        }
        return true;
    }

    public List<Seat> getRealTimeSeatingChart(String matchId) throws IOException {
        List<Seat> result = new ArrayList<>();
        for (Seat seat : seatRepo.findAll()) {
            if (matchId == null || matchId.equalsIgnoreCase(seat.getMatchId())) {
                result.add(seat);
            }
        }
        return result;
    }

    public String generateStaffReport() throws IOException {
        int totalTicketsSold = 0;
        double totalRevenue = 0.0;
        int totalRefunds = 0;
        for (Transaction txn : transactionRepo.findAll()) {
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                totalTicketsSold += txn.getSeatIds().size();
                totalRevenue += txn.getTotalAmount();
            } else if (txn.getStatus() == TransactionStatus.REFUNDED) {
                totalRefunds++;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== STAFF SHIFT REPORT ===\n");
        sb.append("Total Tickets Sold: ").append(totalTicketsSold).append("\n");
        sb.append("Total Revenue: ").append(String.format("%,.0f VND", totalRevenue)).append("\n");
        sb.append("Total Refunds Processed: ").append(totalRefunds).append("\n");
        return sb.toString();
    }
}