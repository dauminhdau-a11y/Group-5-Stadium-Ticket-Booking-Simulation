package controller;

import model.*;
import repository.CsvRepository;
import repository.MatchRepository;
import repository.VoucherRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingController {
    private SyncMechanism mechanism;
    private CsvRepository<Seat> seatRepo;
    private CsvRepository<BookingTransaction> transRepo;
    private CsvRepository<Voucher> voucherRepo;
    private CsvRepository<Match> matchRepo;

    public BookingController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<BookingTransaction> transRepo) {
        this(mechanism, seatRepo, transRepo, new VoucherRepository(), new MatchRepository());
    }

    public BookingController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<BookingTransaction> transRepo, CsvRepository<Voucher> voucherRepo) {
        this(mechanism, seatRepo, transRepo, voucherRepo, new MatchRepository());
    }

    public BookingController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<BookingTransaction> transRepo, CsvRepository<Voucher> voucherRepo,
            CsvRepository<Match> matchRepo) {
        this.mechanism = mechanism;
        this.seatRepo = seatRepo;
        this.transRepo = transRepo;
        this.voucherRepo = voucherRepo != null ? voucherRepo : new VoucherRepository();
        this.matchRepo = matchRepo != null ? matchRepo : new MatchRepository();
    }

    // --- MATCH MANAGEMENT & FILTERING ---

    public List<Match> getAllMatches() {
        return matchRepo.findAll();
    }

    public Match getMatchById(String matchId) {
        return matchRepo.findById(matchId);
    }

    public boolean isMatchSoldOut(String matchId) {
        List<Seat> seats = seatRepo.findByCondition(s -> s.getMatchId().equalsIgnoreCase(matchId));
        if (seats.isEmpty()) {
            return false;
        }
        return seats.stream().noneMatch(s -> s.getStatus() == SeatStatus.AVAILABLE);
    }

    public List<Match> filterMatches(String dateStr, String stadium, String team) {
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

    // --- SEATS & PRICING ---

    public List<Seat> getAllSeatsForMatch(String matchId) {
        return seatRepo.findByCondition(seat -> seat.getMatchId().equalsIgnoreCase(matchId));
    }

    public List<Seat> getAvailableSeats(String matchId) {
        return seatRepo.findByCondition(
                seat -> seat.getMatchId().equalsIgnoreCase(matchId) && seat.getStatus() == SeatStatus.AVAILABLE);
    }

    public List<Seat> validateAndGetSeats(String matchId, List<String> seatIds) throws Exception {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new Exception("Please enter at least 1 seat ID.");
        }
        // Transaction limit: Maximum 4 tickets per transaction
        if (seatIds.size() > 4) {
            throw new Exception("Transaction limit: You can only book a maximum of 4 tickets per transaction!");
        }

        List<Seat> seatsToBook = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (seat == null) {
                throw new Exception("Seat ID not found: " + seatId);
            }
            if (!seat.getMatchId().equalsIgnoreCase(matchId)) {
                throw new Exception("Seat " + seatId + " does not belong to match " + matchId);
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new Exception("Sorry, seat " + seatId + " is already booked.");
            }
            seatsToBook.add(seat);
        }
        return seatsToBook;
    }

    public double calculateSubtotal(List<Seat> seats) {
        double total = 0.0;
        for (Seat seat : seats) {
            total += seat.getPrice();
        }
        return total;
    }

    // --- VOUCHER MANAGEMENT ---

    public Voucher validateAndCheckVoucher(String voucherCode) throws Exception {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return null;
        }
        Voucher voucher = voucherRepo.findById(voucherCode.trim());
        if (voucher == null) {
            throw new Exception("Voucher code '" + voucherCode + "' does not exist.");
        }
        if (voucher.isUsed()) {
            throw new Exception("Voucher '" + voucherCode + "' has already been used! (Limit: 1 time/voucher).");
        }
        return voucher;
    }

    // --- ONLINE BOOKING ---

    public BookingTransaction bookSeatsOnline(String fanId, String matchId,
            List<String> seatIds, String voucherCode) throws Exception {
        if (this.mechanism == SyncMechanism.SYNCHRONIZED) {
            synchronized (this) {
                return executeBooking(fanId, matchId, seatIds, voucherCode);
            }
        } else {
            return executeBooking(fanId, matchId, seatIds, voucherCode);
        }
    }

    public BookingTransaction bookSeatsWithPayment(String fanId, String matchId,
            List<String> seatIds, String voucherCode, PaymentMethod paymentMethod) throws Exception {
        return bookSeatsOnline(fanId, matchId, seatIds, voucherCode);
    }

    public BookingTransaction bookSeats(String fanId, String matchId,
            List<String> seatIds) throws Exception {
        return bookSeatsOnline(fanId, matchId, seatIds, "");
    }

    private BookingTransaction executeBooking(String fanId, String matchId,
            List<String> seatIds, String voucherCode) throws Exception {
        List<Seat> seatsToBook = validateAndGetSeats(matchId, seatIds);

        double subtotal = calculateSubtotal(seatsToBook);
        double discount = 0.0;
        Voucher appliedVoucher = null;

        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            appliedVoucher = validateAndCheckVoucher(voucherCode);
            if (appliedVoucher != null) {
                discount = appliedVoucher.calculateDiscount(subtotal);
            }
        }

        double finalAmount = Math.max(0, subtotal - discount);

        // Update seats to BOOKED
        for (Seat seat : seatsToBook) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepo.save(seat);
        }

        // Mark voucher as used if applied
        if (appliedVoucher != null) {
            appliedVoucher.setUsed(true);
            voucherRepo.save(appliedVoucher);
        }

        String transId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        BookingTransaction transaction = new BookingTransaction(
                transId,
                fanId,
                matchId,
                finalAmount,
                LocalDateTime.now(),
                BookingStatus.CONFIRMED,
                seatIds,
                PaymentMethod.QR_CODE,
                appliedVoucher != null ? appliedVoucher.getCode() : "",
                discount);

        transRepo.save(transaction);
        return transaction;
    }
}
