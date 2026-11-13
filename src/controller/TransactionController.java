package controller;

import model.Match;
import model.Seat;
import model.Ticket;
import model.Transaction;
import model.Voucher;
import model.enums.PaymentMethod;
import model.enums.SeatStatus;
import model.enums.SyncMechanism;
import model.enums.TicketStatus;
import model.enums.TransactionStatus;
import repository.CsvRepository;
import repository.MatchRepository;
import repository.NotificationRepository;
import repository.TicketRepository;
import repository.VoucherRepository;
import util.RefundPolicy;
import util.TicketRefundCalculator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionController {
    public class RefundResult {
        private boolean successful;
        private String message;
        private double refundAmount;

        private RefundResult(boolean successful, String message, double refundAmount) {
            this.successful = successful;
            this.message = message;
            this.refundAmount = refundAmount;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getMessage() {
            return message;
        }

        public double getRefundAmount() {
            return refundAmount;
        }
    }

    public static class RefundableTicket {
        private Ticket ticket;
        private double voucherDiscount;
        private double refundAmount;

        public RefundableTicket(Ticket ticket, double voucherDiscount, double refundAmount) {
            this.ticket = ticket;
            this.voucherDiscount = voucherDiscount;
            this.refundAmount = refundAmount;
        }

        public Ticket getTicket() {
            return ticket;
        }

        public double getVoucherDiscount() {
            return voucherDiscount;
        }

        public double getRefundAmount() {
            return refundAmount;
        }
    }

    private SyncMechanism mechanism;
    private CsvRepository<Seat> seatRepo;
    private CsvRepository<Transaction> transactionRepo;
    private CsvRepository<Voucher> voucherRepo;
    private CsvRepository<Match> matchRepo;
    private TicketRepository ticketRepo;
    private NotificationController notificationController;
    private TicketRefundCalculator ticketRefundCalculator = new TicketRefundCalculator();

    public TransactionController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<Transaction> transactionRepo) {
        this(mechanism, seatRepo, transactionRepo, new VoucherRepository(), new MatchRepository());
    }

    public TransactionController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<Transaction> transactionRepo, CsvRepository<Voucher> voucherRepo) {
        this(mechanism, seatRepo, transactionRepo, voucherRepo, new MatchRepository());
    }

    public TransactionController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<Transaction> transactionRepo, CsvRepository<Voucher> voucherRepo,
            CsvRepository<Match> matchRepo) {
        this(mechanism, seatRepo, transactionRepo, voucherRepo, matchRepo,
                new NotificationController(new NotificationRepository()));
    }

    public TransactionController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<Transaction> transactionRepo, CsvRepository<Voucher> voucherRepo,
            CsvRepository<Match> matchRepo, NotificationController notificationController) {
        this.mechanism = mechanism;
        this.seatRepo = seatRepo;
        this.transactionRepo = transactionRepo;
        this.voucherRepo = voucherRepo != null ? voucherRepo : new VoucherRepository();
        this.matchRepo = matchRepo != null ? matchRepo : new MatchRepository();
        this.ticketRepo = new TicketRepository("data/tickets.csv", seatRepo);
        this.notificationController = notificationController != null ? notificationController
                : new NotificationController(new NotificationRepository());
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }

    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public SyncMechanism getMechanism() {
        return mechanism;
    }

    public void setMechanism(SyncMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public CsvRepository<Seat> getSeatRepo() {
        return seatRepo;
    }

    public void setSeatRepo(CsvRepository<Seat> seatRepo) {
        this.seatRepo = seatRepo;
    }

    public CsvRepository<Transaction> getTransactionRepo() {
        return transactionRepo;
    }

    public void setTransactionRepo(CsvRepository<Transaction> transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public CsvRepository<Voucher> getVoucherRepo() {
        return voucherRepo;
    }

    public void setVoucherRepo(CsvRepository<Voucher> voucherRepo) {
        this.voucherRepo = voucherRepo;
    }

    public CsvRepository<Match> getMatchRepo() {
        return matchRepo;
    }

    public void setMatchRepo(CsvRepository<Match> matchRepo) {
        this.matchRepo = matchRepo;
    }

    public TicketRepository getTicketRepo() {
        return ticketRepo;
    }

    public void setTicketRepo(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public List<Match> getAllMatches() throws IOException {
        return matchRepo.findAll();
    }

    public Match getMatchById(String matchId) throws IOException {
        return matchRepo.findById(matchId);
    }

    public boolean isMatchSoldOut(String matchId) throws IOException {
        List<Seat> seats = seatRepo.findByCondition(s -> s.getMatchId().equalsIgnoreCase(matchId));
        if (seats.isEmpty()) {
            return false;
        }
        return seats.stream().noneMatch(s -> s.getStatus() == SeatStatus.AVAILABLE);
    }

    public List<Match> filterMatches(String dateStr, String stadium, String team) throws IOException {
        List<Match> all = getAllMatches();
        return all.stream().filter(m -> {
            boolean matchDate = true;
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                matchDate = m.getMatchDate() != null && m.getMatchDate().toString().contains(dateStr.trim());
            }

            boolean matchStadium = true;
            if (stadium != null && !stadium.trim().isEmpty()) {
                matchStadium = m.getStadium() != null && m.getStadium().toLowerCase().contains(stadium.trim().toLowerCase());
            }

            boolean matchTeam = true;
            if (team != null && !team.trim().isEmpty()) {
                String q = team.trim().toLowerCase();
                matchTeam = (m.getHomeTeam() != null && m.getHomeTeam().toLowerCase().contains(q)) ||
                            (m.getAwayTeam() != null && m.getAwayTeam().toLowerCase().contains(q));
            }

            return matchDate && matchStadium && matchTeam;
        }).collect(Collectors.toList());
    }

    public int HOLD_TIMEOUT_MINUTES = 10;

    public synchronized int releaseExpiredSeats() throws IOException {
        List<Seat> allSeats = seatRepo.findAll();
        int releasedCount = 0;
        for (Seat seat : allSeats) {
            if (seat.getStatus() == SeatStatus.LOCKED && seat.isHoldExpired(HOLD_TIMEOUT_MINUTES)) {
                seat.unlock();
                seatRepo.save(seat);
                releasedCount++;
            }
        }
        return releasedCount;
    }

    public synchronized List<Seat> holdSeatsForPayment(String fanId, String matchId, List<String> seatIds) throws IOException {
        releaseExpiredSeats();

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Please enter at least 1 seat ID.");
        }
        if (seatIds.size() > 4) {
            throw new IllegalArgumentException("Transaction limit: You can select at most 4 tickets.");
        }

        List<Seat> seatsToHold = new ArrayList<>();
        List<String> uniqueSeatIds = seatIds.stream().distinct().collect(Collectors.toList());
        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new IllegalArgumentException("A seat cannot be selected more than once.");
        }

        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat ID not found: " + seatId);
            }
            if (seat.getMatchId() != null && !seat.getMatchId().isEmpty() && !seat.getMatchId().equalsIgnoreCase(matchId)) {
                throw new IllegalArgumentException("Seat " + seatId + " does not belong to match " + matchId);
            }

            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new IllegalArgumentException("Seat " + seatId + " has already been booked by another customer.");
            }

            if (seat.getStatus() == SeatStatus.LOCKED) {
                if (seat.getLockedBy() != null && !seat.getLockedBy().equalsIgnoreCase(fanId)) {
                    long remSeconds = seat.getRemainingHoldSeconds(HOLD_TIMEOUT_MINUTES);
                    long remMinutes = remSeconds / 60;
                    throw new IllegalArgumentException("Seat " + seatId + " is currently LOCKED by another customer for checkout (~" + (remMinutes + 1) + " minute(s) remaining). Please choose another seat!");
                }
            }

            seatsToHold.add(seat);
        }

        for (Seat seat : seatsToHold) {
            seat.lockForHold(fanId);
            seatRepo.save(seat);
        }

        return seatsToHold;
    }

    public synchronized void cancelHoldSeats(String fanId, List<String> seatIds) throws IOException {
        if (seatIds == null) return;
        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (seat != null && seat.getStatus() == SeatStatus.LOCKED) {
                if (seat.getLockedBy() == null || seat.getLockedBy().equalsIgnoreCase(fanId)) {
                    seat.unlock();
                    seatRepo.save(seat);
                }
            }
        }
    }

    public List<Seat> getAllSeatsForMatch(String matchId) throws IOException {
        releaseExpiredSeats();
        return seatRepo.findByCondition(seat -> seat.getMatchId().equalsIgnoreCase(matchId));
    }

    public List<Seat> getAvailableSeats(String matchId) throws IOException {
        return seatRepo.findByCondition(
                seat -> seat.getMatchId().equalsIgnoreCase(matchId) && seat.getStatus() == SeatStatus.AVAILABLE);
    }

    public List<Seat> validateAndGetSeats(String matchId, List<String> seatIds) throws IOException {
        return validateAndGetSeats(null, matchId, seatIds);
    }

    public List<Seat> validateAndGetSeats(String fanId, String matchId, List<String> seatIds) throws IOException {
        releaseExpiredSeats();

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Please enter at least 1 seat ID.");
        }
        if (seatIds.size() > 4) {
            throw new IllegalArgumentException("Transaction limit: You can select at most 4 tickets.");
        }

        List<Seat> seatsForTransaction = new ArrayList<>();
        List<String> uniqueSeatIds = seatIds.stream().distinct().collect(Collectors.toList());
        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new IllegalArgumentException("A seat cannot be selected more than once.");
        }
        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat ID not found: " + seatId);
            }
            if (seat.getMatchId() != null && !seat.getMatchId().isEmpty() && !seat.getMatchId().equalsIgnoreCase(matchId)) {
                throw new IllegalArgumentException("Seat " + seatId + " does not belong to match " + matchId);
            }
            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new IllegalArgumentException("Seat " + seatId + " has already been booked by another customer.");
            }
            if (seat.getStatus() == SeatStatus.LOCKED) {
                if (fanId != null && seat.getLockedBy() != null && !seat.getLockedBy().equalsIgnoreCase(fanId)) {
                    long remSeconds = seat.getRemainingHoldSeconds(HOLD_TIMEOUT_MINUTES);
                    long remMinutes = remSeconds / 60;
                    throw new IllegalArgumentException("Seat " + seatId + " is currently LOCKED by another customer for checkout (~" + (remMinutes + 1) + " minute(s) remaining). Please choose another seat!");
                }
            }
            seatsForTransaction.add(seat);
        }
        return seatsForTransaction;
    }

    public double calculateSubtotal(List<Seat> seats) throws IOException {
        double total = 0.0;
        for (Seat seat : seats) {
            total += ticketRepo.getPriceBySeatId(seat.getSeatId());
        }
        return total;
    }

    public double getTicketPrice(Seat seat) throws IOException {
        return ticketRepo.getPriceBySeatId(seat.getSeatId());
    }

    public Voucher validateAndCheckVoucher(String voucherCode) throws IOException {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return null;
        }
        Voucher voucher = voucherRepo.findById(voucherCode.trim());
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher code '" + voucherCode + "' does not exist.");
        }
        return voucher;
    }

    public Voucher validateAndCheckVoucher(String voucherCode, double orderAmount) throws IOException {
        Voucher voucher = validateAndCheckVoucher(voucherCode);
        if (voucher != null && !voucher.isApplicableTo(orderAmount)) {
            throw new IllegalArgumentException(String.format(
                    "Order must be at least %,.0f VND to use voucher '%s'.",
                    voucher.getMinimumOrderAmount(), voucher.getCode()));
        }
        return voucher;
    }

    public List<Voucher> getApplicableVouchers(double orderAmount) throws IOException {
        List<Voucher> applicableVouchers = new ArrayList<>();
        for (Voucher voucher : voucherRepo.findAll()) {
            if (voucher.isApplicableTo(orderAmount)) {
                applicableVouchers.add(voucher);
            }
        }
        return applicableVouchers;
    }

    public Transaction createTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod) throws IOException {
        return createTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod, false);
    }

    public Transaction createTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod, boolean autoConfirm) throws IOException {
        if (this.mechanism == SyncMechanism.SYNCHRONIZED) {
            synchronized (this) {
                return processTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod, autoConfirm);
            }
        } else {
            return processTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod, autoConfirm);
        }
    }

    public Transaction processTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod) throws IOException {
        return processTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod, false);
    }

    public Transaction processTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod, boolean autoConfirm) throws IOException {
        if (fanId == null || fanId.trim().isEmpty()) {
            throw new IllegalArgumentException("Fan ID is required.");
        }
        List<Seat> seatsForTransaction = validateAndGetSeats(fanId, matchId, seatIds);

        double subtotal = calculateSubtotal(seatsForTransaction);
        double discount = 0.0;
        Voucher appliedVoucher = null;

        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            appliedVoucher = validateAndCheckVoucher(voucherCode, subtotal);
            if (appliedVoucher != null) {
                discount = appliedVoucher.calculateDiscount(subtotal);
            }
        }

        double finalAmount = Math.max(0, subtotal - discount);
        String transId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);

        TransactionStatus initialTxnStatus = autoConfirm ? TransactionStatus.CONFIRMED : TransactionStatus.PENDING;
        TicketStatus initialTicketStatus = autoConfirm ? TicketStatus.SOLD : TicketStatus.RESERVED;

        for (Seat seat : seatsForTransaction) {
            seat.confirmBooked();
            seatRepo.save(seat);

            String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket ticket = new Ticket(ticketId, transId, seat.getSeatId(), matchId,
                    ticketRepo.getPriceBySeatId(seat.getSeatId()), initialTicketStatus.name());
            ticketRepo.save(ticket);
        }

        Transaction transaction = new Transaction(
                transId,
                fanId,
                matchId,
                finalAmount,
                LocalDateTime.now(),
                initialTxnStatus,
                seatIds,
                paymentMethod == null ? PaymentMethod.QR_CODE : paymentMethod,
                appliedVoucher != null ? appliedVoucher.getCode() : "",
                discount);

        transactionRepo.save(transaction);
        if (initialTxnStatus == TransactionStatus.CONFIRMED && notificationController != null) {
            notificationController.addBookingConfirmedNotification(transaction);
        }
        return transaction;
    }

    public List<Transaction> getTransactionsByFan(String fanId) throws IOException {
        List<Transaction> results = new ArrayList<>();
        if (fanId == null) {
            return results;
        }
        for (Transaction txn : transactionRepo.findAll()) {
            if (fanId.equalsIgnoreCase(txn.getFanId())) {
                results.add(txn);
            }
        }
        return results;
    }

    public List<Transaction> getAllTransactions() throws IOException {
        return transactionRepo.findAll();
    }

    public double calculateTotalRevenue() throws IOException {
        double revenue = 0.0;
        for (Transaction txn : transactionRepo.findAll()) {
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS
                    || txn.getStatus() == TransactionStatus.REFUNDED) {
                revenue += txn.getNetAmount();
            }
        }
        return revenue;
    }

    public List<RefundableTicket> getRefundableTicketsByFan(String fanId) throws IOException {
        List<RefundableTicket> refundable = new ArrayList<>();
        RefundPolicy refundPolicy = new RefundPolicy();
        for (Transaction txn : getTransactionsByFan(fanId)) {
            if (txn.getStatus() != TransactionStatus.CONFIRMED
                    && txn.getStatus() != TransactionStatus.SUCCESS
                    && txn.getStatus() != TransactionStatus.REFUND_REQUESTED) {
                continue;
            }
            List<Ticket> tickets = getTransactionTickets(txn);
            boolean previousRequestPending = txn.getStatus() == TransactionStatus.REFUND_REQUESTED
                    || tickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.REFUND_REQUESTED);
            boolean deadlineAllowed = false;
            try {
                deadlineAllowed = refundPolicy.isRefundAllowed(
                        matchRepo.findById(txn.getMatchId()), LocalDateTime.now());
            } catch (IllegalArgumentException e) {
                if (!previousRequestPending) {
                    continue;
                }
            }
            for (Ticket ticket : tickets) {
                boolean ticketRequestPending = ticket.getStatus() == TicketStatus.REFUND_REQUESTED;
                if (!ticketRequestPending && (!deadlineAllowed
                        || (ticket.getStatus() != TicketStatus.SOLD && ticket.getStatus() != TicketStatus.ACTIVE))) {
                    continue;
                }
                double voucherDiscount = ticketRefundCalculator.getVoucherDiscount(ticket, txn, tickets);
                double refundAmount = ticketRefundCalculator.calculateRefundAmount(ticket, txn, tickets);
                refundable.add(new RefundableTicket(ticket, voucherDiscount, refundAmount));
            }
        }
        return refundable;
    }

    public RefundResult refundTicket(String fanId, String ticketId) throws IOException {
        Ticket ticket = ticketRepo.findById(ticketId);
        if (ticket == null) {
            return new RefundResult(false, "Ticket not found.", 0.0);
        }
        Transaction txn = findTransactionForTicket(fanId, ticket);
        if (txn == null || (txn.getStatus() != TransactionStatus.CONFIRMED
                && txn.getStatus() != TransactionStatus.SUCCESS
                && txn.getStatus() != TransactionStatus.REFUND_REQUESTED)) {
            return new RefundResult(false, "Ticket is not part of a paid and confirmed booking owned by this fan.", 0.0);
        }
        boolean previousRequestPending = ticket.getStatus() == TicketStatus.REFUND_REQUESTED;
        if (!previousRequestPending && ticket.getStatus() != TicketStatus.SOLD
                && ticket.getStatus() != TicketStatus.ACTIVE) {
            return new RefundResult(false, "Only sold tickets that have not been refunded can be requested.", 0.0);
        }

        if (!previousRequestPending) {
            try {
                if (!new RefundPolicy().isRefundAllowed(
                        matchRepo.findById(txn.getMatchId()), LocalDateTime.now())) {
                    return new RefundResult(false, "The refund request deadline has passed.", 0.0);
                }
            } catch (IllegalArgumentException e) {
                return new RefundResult(false, "The refund request deadline has passed or match schedule is unavailable.", 0.0);
            }
        }

        List<Ticket> transactionTickets = getTransactionTickets(txn);
        double refundAmount = ticketRefundCalculator.calculateRefundAmount(ticket, txn, transactionTickets);
        ticket.setStatus(TicketStatus.REFUNDED);
        ticketRepo.save(ticket);

        txn.setRefundedAmount(txn.getRefundedAmount() + refundAmount);
        boolean allRefunded = !transactionTickets.isEmpty();
        for (Ticket transactionTicket : getTransactionTickets(txn)) {
            if (transactionTicket.getStatus() != TicketStatus.REFUNDED) {
                allRefunded = false;
                break;
            }
        }
        txn.setStatus(allRefunded ? TransactionStatus.REFUNDED : TransactionStatus.CONFIRMED);
        transactionRepo.save(txn);

        Seat seat = seatRepo.findById(ticket.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepo.save(seat);
        }
        return new RefundResult(true, "Ticket refunded immediately; staff approval is not required.", refundAmount);
    }

    public Transaction findTransactionForTicket(String fanId, Ticket ticket) throws IOException {
        if (fanId == null || fanId.trim().isEmpty()) {
            return null;
        }
        for (Transaction txn : getTransactionsByFan(fanId)) {
            if (txn.getMatchId() == null || ticket.getMatchId() == null
                    || !txn.getMatchId().equalsIgnoreCase(ticket.getMatchId())
                    || !txn.getSeatIds().contains(ticket.getSeatId())) {
                continue;
            }
            if (ticket.getTransactionId() != null && !ticket.getTransactionId().trim().isEmpty()
                    && !ticket.getTransactionId().equalsIgnoreCase(txn.getTransactionId())) {
                continue;
            }
            return txn;
        }
        return null;
    }

    public boolean confirmReservationPayment(String transactionId) throws IOException {
        Transaction txn = transactionRepo.findById(transactionId);
        if (txn == null || txn.getStatus() != TransactionStatus.PENDING) {
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
        return true;
    }

    public List<Ticket> getTransactionTickets(Transaction txn) throws IOException {
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
}
