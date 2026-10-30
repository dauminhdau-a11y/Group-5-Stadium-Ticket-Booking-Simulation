package com.ticketbooking.controller;

import com.ticketbooking.model.Match;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.Voucher;
import com.ticketbooking.model.enums.PaymentMethod;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SyncMechanism;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.model.enums.TransactionStatus;
import com.ticketbooking.repository.CsvRepository;
import com.ticketbooking.repository.MatchRepository;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.TransactionRepository;
import com.ticketbooking.repository.VoucherRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionController {
    private SyncMechanism mechanism;
    private CsvRepository<Seat> seatRepo;
    private CsvRepository<Transaction> transactionRepo;
    private CsvRepository<Voucher> voucherRepo;
    private CsvRepository<Match> matchRepo;
    private TicketRepository ticketRepo;

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
        this.mechanism = mechanism;
        this.seatRepo = seatRepo;
        this.transactionRepo = transactionRepo;
        this.voucherRepo = voucherRepo != null ? voucherRepo : new VoucherRepository();
        this.matchRepo = matchRepo != null ? matchRepo : new MatchRepository();
        this.ticketRepo = new TicketRepository("data/tickets.csv");
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

        public static final int HOLD_TIMEOUT_MINUTES = 10;

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

    public double calculateSubtotal(List<Seat> seats) {
        double total = 0.0;
        for (Seat seat : seats) {
            total += seat.getPrice();
        }
        return total;
    }

    public Voucher validateAndCheckVoucher(String voucherCode) throws IOException {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return null;
        }
        Voucher voucher = voucherRepo.findById(voucherCode.trim());
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher code '" + voucherCode + "' does not exist.");
        }
        if (voucher.isUsed()) {
            throw new IllegalArgumentException("Voucher '" + voucherCode + "' has already been used.");
        }
        return voucher;
    }

    public Transaction createTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod) throws IOException {
        if (this.mechanism == SyncMechanism.SYNCHRONIZED) {
            synchronized (this) {
                return processTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod);
            }
        } else {
            return processTransaction(fanId, matchId, seatIds, voucherCode, paymentMethod);
        }
    }

    public Transaction processTransaction(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod) throws IOException {
        if (fanId == null || fanId.trim().isEmpty()) {
            throw new IllegalArgumentException("Fan ID is required.");
        }
        List<Seat> seatsForTransaction = validateAndGetSeats(fanId, matchId, seatIds);

        double subtotal = calculateSubtotal(seatsForTransaction);
        double discount = 0.0;
        Voucher appliedVoucher = null;

        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            appliedVoucher = validateAndCheckVoucher(voucherCode);
            if (appliedVoucher != null) {
                discount = appliedVoucher.calculateDiscount(subtotal);
            }
        }

        double finalAmount = Math.max(0, subtotal - discount);

        for (Seat seat : seatsForTransaction) {
            seat.confirmBooked();
            seatRepo.save(seat);

            String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket ticket = new Ticket(ticketId, matchId, seat.getSeatId(), fanId,
                    seat.getPrice(), TicketStatus.SOLD, LocalDateTime.now().toString());
            ticketRepo.save(ticket);
        }

        if (appliedVoucher != null) {
            appliedVoucher.setUsed(true);
            voucherRepo.save(appliedVoucher);
        }

        String transId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        Transaction transaction = new Transaction(
                transId,
                fanId,
                matchId,
                finalAmount,
                LocalDateTime.now(),
                TransactionStatus.CONFIRMED,
                seatIds,
                paymentMethod == null ? PaymentMethod.QR_CODE : paymentMethod,
                appliedVoucher != null ? appliedVoucher.getCode() : "",
                discount);

        transactionRepo.save(transaction);
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
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                revenue += txn.getTotalAmount();
            }
        }
        return revenue;
    }

    public boolean refundTransaction(String transactionId) throws IOException {
        Transaction txn = transactionRepo.findById(transactionId);
        if (txn == null || txn.getStatus() == TransactionStatus.REFUNDED) {
            return false;
        }
        txn.setStatus(TransactionStatus.REFUNDED);
        transactionRepo.save(txn);

        for (String seatId : txn.getSeatIds()) {
            Seat seat = seatRepo.findById(seatId);
            if (seat != null) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepo.save(seat);
            }
            Ticket ticket = ticketRepo.findBySeatId(seatId);
            if (ticket != null) {
                ticket.setStatus(TicketStatus.REFUNDED);
                ticketRepo.save(ticket);
            }
        }
        return true;
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
}
