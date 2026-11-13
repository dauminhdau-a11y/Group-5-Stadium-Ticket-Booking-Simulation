package controller;

import model.Seat;
import model.SupportRequest;
import model.Ticket;
import model.Transaction;
import model.enums.PaymentMethod;
import model.enums.SeatStatus;
import model.enums.SupportStatus;
import model.enums.TicketStatus;
import model.enums.TransactionStatus;
import repository.MatchRepository;
import repository.NotificationRepository;
import repository.SeatRepository;
import repository.SupportRequestRepository;
import repository.TicketRepository;
import repository.TransactionRepository;
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
    private NotificationController notificationController;

    public StaffController() {
        this(new SeatRepository("data/seats.csv"),
                new TicketRepository("data/tickets.csv"),
                new TransactionRepository("data/transactions.csv"),
                new MatchRepository("data/matches.csv"),
                new SupportRequestRepository("data/support_requests.csv"),
                new NotificationController(new NotificationRepository()));
    }

    public StaffController(SeatRepository seatRepo, TicketRepository ticketRepo,
            TransactionRepository transactionRepo, MatchRepository matchRepo,
            SupportRequestRepository supportRepo) {
        this(seatRepo, ticketRepo, transactionRepo, matchRepo, supportRepo,
                new NotificationController(new NotificationRepository()));
    }

    public StaffController(SeatRepository seatRepo, TicketRepository ticketRepo,
            TransactionRepository transactionRepo, MatchRepository matchRepo,
            SupportRequestRepository supportRepo, NotificationController notificationController) {
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.transactionRepo = transactionRepo;
        this.matchRepo = matchRepo;
        this.supportRepo = supportRepo;
        if (notificationController == null) {
            throw new IllegalArgumentException("Notification controller must not be null.");
        }
        this.notificationController = notificationController;
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

    public double getTicketPrice(Seat seat) throws IOException {
        return ticketRepo.getPriceBySeatId(seat.getSeatId());
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
            total += ticketRepo.getPriceBySeatId(seat.getSeatId());
        }

        for (Seat s : selectedSeats) {
            s.setStatus(SeatStatus.BOOKED);
            seatRepo.save(s);

            String tckId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket tck = new Ticket(tckId, matchId, s.getSeatId(), customerIdentifier,
                    ticketRepo.getPriceBySeatId(s.getSeatId()), TicketStatus.SOLD,
                    LocalDateTime.now().toString());
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
        notificationController.addBookingConfirmedNotification(txn);
        return txn;
    }

    public boolean confirmReservationPayment(String transactionId) throws IOException {
        Transaction txn = transactionRepo.findById(transactionId);
        if (txn == null) {
            return false;
        }
        if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
            notificationController.addBookingConfirmedNotification(txn);
            return true;
        }
        if (txn.getStatus() != TransactionStatus.PENDING) {
            return false;
        }
        txn.setStatus(TransactionStatus.CONFIRMED);
        transactionRepo.save(txn);
        for (Ticket ticket : getTransactionTickets(txn)) {
            if (ticket.getStatus() == TicketStatus.RESERVED) {
                ticket.setStatus(TicketStatus.SOLD);
                ticketRepo.save(ticket);
            }
        }
        notificationController.addBookingConfirmedNotification(txn);
        return true;
    }

    public List<Transaction> getPendingApprovalTransactions() throws IOException {
        List<Transaction> pending = new ArrayList<>();
        for (Transaction txn : transactionRepo.findAll()) {
            if (txn.getStatus() == TransactionStatus.PENDING) {
                pending.add(txn);
            }
        }
        return pending;
    }

    private List<Ticket> getTransactionTickets(Transaction txn) throws IOException {
        List<Ticket> tickets = ticketRepo.findByTransactionId(txn.getTransactionId());
        if (!tickets.isEmpty()) {
            return tickets;
        }
        List<Ticket> result = new ArrayList<>();
        for (String seatId : txn.getSeatIds()) {
            Ticket ticket = ticketRepo.findBySeatId(seatId);
            if (ticket != null) {
                result.add(ticket);
            }
        }
        return result;
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
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS
                    || txn.getStatus() == TransactionStatus.REFUNDED) {
                totalTicketsSold += txn.getSeatIds().size();
                totalRevenue += txn.getNetAmount();
            }
        }
        for (Ticket ticket : ticketRepo.findAll()) {
            if (ticket.getStatus() == TicketStatus.REFUNDED) {
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

    public List<SupportRequest> getAllSupportRequests() throws IOException {
        return supportRepo.findAll();
    }

    public List<SupportRequest> getOpenSupportRequests() throws IOException {
        List<SupportRequest> list = new ArrayList<>();
        for (SupportRequest req : supportRepo.findAll()) {
            if (req.getStatus() == SupportStatus.OPEN || req.getStatus() == SupportStatus.PENDING) {
                list.add(req);
            }
        }
        return list;
    }

    public SupportRequest getSupportRequestById(String requestId) throws IOException {
        if (requestId == null || requestId.trim().isEmpty()) {
            return null;
        }
        return supportRepo.findById(requestId.trim());
    }

    public boolean respondToSupportRequest(String requestId, String responseMessage, SupportStatus status) throws IOException {
        if (requestId == null || requestId.trim().isEmpty()
                || responseMessage == null || responseMessage.trim().isEmpty()) {
            return false;
        }
        SupportRequest req = supportRepo.findById(requestId.trim());
        if (req == null) {
            return false;
        }
        req.setResponse(responseMessage.trim());
        req.setStatus(status != null ? status : SupportStatus.RESOLVED);
        supportRepo.save(req);
        return true;
    }

    public boolean respondToSupportRequest(String requestId, String responseMessage) throws IOException {
        return respondToSupportRequest(requestId, responseMessage, SupportStatus.RESOLVED);
    }
}