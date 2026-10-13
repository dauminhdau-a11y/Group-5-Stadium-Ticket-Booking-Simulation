package com.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.nio.file.Path;

import com.exception.InsufficientPermissionException;
import com.exception.InvalidVoucherException;
import com.exception.MaxTicketsExceededException;
import com.exception.SeatAlreadyBookedException;
import com.exception.VoucherInvalidException;
import com.model.BookingTransaction;
import com.model.MatchSeat;
import com.model.Match;
import com.model.Notification;
import com.model.Payment;
import com.model.SeatInspectionDetails;
import com.model.ShiftReport;
import com.model.Staff;
import com.model.SupportRequest;
import com.model.Ticket;
import com.model.User;
import com.model.Voucher;
import com.model.enums.BookingStatus;
import com.model.enums.NotificationType;
import com.model.enums.PaymentMethod;
import com.model.enums.SeatStatus;
import com.model.enums.SeatZone;
import com.model.enums.SupportRequestStatus;
import com.model.enums.TicketStatus;
import com.model.enums.UserRole;
import com.repository.impl.BookingTransactionRepository;
import com.repository.impl.MatchSeatRepository;
import com.repository.impl.MatchRepository;
import com.repository.impl.NotificationRepository;
import com.repository.impl.PaymentRepository;
import com.repository.impl.StaffAccountRepository;
import com.repository.impl.SupportRequestRepository;
import com.repository.impl.TicketRepository;
import com.repository.impl.VoucherRepository;
import com.util.CsvFileLock;
import com.util.DataGenerator;
import com.util.PasswordHasher;

public class StaffController {
    private final SessionContext session;
    private final MatchSeatRepository matchSeatRepository;
    private final MatchRepository matchRepository;
    private final BookingTransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final TicketRepository ticketRepository;
    private final StaffAccountRepository accountRepository;
    private final VoucherRepository voucherRepository;
    private final PaymentRepository paymentRepository;
    private final SupportRequestRepository supportRequestRepository;

    public StaffController() {
        this(new StaffAccountRepository());
    }

    public StaffController(StaffAccountRepository accountRepository) {
        this(new SessionContext(), new MatchSeatRepository(), new BookingTransactionRepository(),
                new NotificationRepository(), new TicketRepository(), accountRepository,
                new VoucherRepository(), new MatchRepository(), new PaymentRepository(),
                new SupportRequestRepository());
    }

    public StaffController(MatchSeatRepository matchSeatRepository, TicketRepository ticketRepository,
            BookingTransactionRepository transactionRepository, VoucherRepository voucherRepository,
            NotificationRepository notificationRepository, SessionContext session) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository, ticketRepository,
                new StaffAccountRepository(), voucherRepository, new MatchRepository(), new PaymentRepository(),
                new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
                ticketRepository, new StaffAccountRepository(), new VoucherRepository(), new MatchRepository(),
                new PaymentRepository(), new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository,
            StaffAccountRepository accountRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
                ticketRepository, accountRepository, new VoucherRepository(), new MatchRepository(),
                new PaymentRepository(), new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository,
            StaffAccountRepository accountRepository,
            VoucherRepository voucherRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
                ticketRepository, accountRepository, voucherRepository, new MatchRepository(), new PaymentRepository(),
                new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository,
            StaffAccountRepository accountRepository,
            VoucherRepository voucherRepository,
            MatchRepository matchRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
                ticketRepository, accountRepository, voucherRepository, matchRepository, new PaymentRepository(),
                new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository,
            StaffAccountRepository accountRepository,
            VoucherRepository voucherRepository,
            MatchRepository matchRepository,
            PaymentRepository paymentRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
                ticketRepository, accountRepository, voucherRepository, matchRepository, paymentRepository,
                new SupportRequestRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
            BookingTransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            TicketRepository ticketRepository,
            StaffAccountRepository accountRepository,
            VoucherRepository voucherRepository,
            MatchRepository matchRepository,
            PaymentRepository paymentRepository,
            SupportRequestRepository supportRequestRepository) {
        this.session = session;
        this.matchSeatRepository = matchSeatRepository;
        this.matchRepository = matchRepository;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
        this.voucherRepository = voucherRepository;
        this.paymentRepository = paymentRepository;
        this.supportRequestRepository = supportRequestRepository;
        CsvFileLock.withLock(bookingLockPath(), () -> {
            seedSampleData();
            return null;
        });
    }

    private void seedSampleData() {
        List<Match> sampleMatches = DataGenerator.sampleMatches(10);
        matchRepository.saveAll(sampleMatches);

        Map<String, MatchSeat> existingByKey = new HashMap<String, MatchSeat>();
        for (MatchSeat seat : matchSeatRepository.findAll()) {
            existingByKey.put(seat.getMatchId() + "\u0000" + seat.getSeatId(), seat);
        }
        List<MatchSeat> missingSeats = new ArrayList<MatchSeat>();
        List<MatchSeat> seatsToUpdate = new ArrayList<MatchSeat>();
        for (MatchSeat seat : DataGenerator.sampleMatchSeats(sampleMatches, 1000)) {
            MatchSeat existing = existingByKey.get(seat.getMatchId() + "\u0000" + seat.getSeatId());
            if (existing == null) {
                missingSeats.add(seat);
            } else {
                seat.setMatchSeatId(existing.getMatchSeatId());
                seat.setStatus(existing.getStatus());
                seat.setLockedBy(existing.getLockedBy());
                seat.setLockedAt(existing.getLockedAt());
                if (!seat.getSectionId().equals(existing.getSectionId())
                        || !seat.getBlockId().equals(existing.getBlockId())
                        || seat.getRowNumber() != existing.getRowNumber()
                        || !seat.getSeatNumber().equals(existing.getSeatNumber())
                        || Double.compare(seat.getPrice(), existing.getPrice()) != 0) {
                    seatsToUpdate.add(seat);
                }
            }
        }
        if (!missingSeats.isEmpty()) {
            matchSeatRepository.saveAll(missingSeats);
        }
        if (!seatsToUpdate.isEmpty()) {
            int updated = matchSeatRepository.updateAll(seatsToUpdate);
            if (updated != seatsToUpdate.size()) {
                throw new IllegalStateException("Unable to migrate all sample seat prices.");
            }
        }

        LocalDateTime voucherExpiry = LocalDateTime.now().plusYears(1);
        List<Voucher> sampleVouchers = new ArrayList<Voucher>();
        sampleVouchers.add(new Voucher("V-001", "WELCOME10", 10000, voucherExpiry, 500));
        sampleVouchers.add(new Voucher("V-002", "MATCHDAY15", 15000, voucherExpiry, 300));
        sampleVouchers.add(new Voucher("V-003", "FAN2026", 20000, voucherExpiry, 1000));
        sampleVouchers.add(new Voucher("V-004", "VIP25", 25000, voucherExpiry, 100));
        voucherRepository.saveAll(sampleVouchers);
        Voucher voucher = voucherRepository.findById("V-001").orElse(sampleVouchers.get(0));

        User fan = new User("FAN-001", "fan01", "123456", "fan1@gmail.com", UserRole.FAN.name());

        BookingTransaction tx = new BookingTransaction("TX-001", fan.getUserId(), "STAFF-001", "M-001",
                voucher.getVoucherId(),
                500000, 10000, 490000, LocalDateTime.now().minusDays(1), BookingStatus.CONFIRMED);
        transactionRepository.save(tx);

        Ticket ticket = new Ticket("T-001", tx.getTransactionId(), "S-1", "M-001",
                500000, TicketStatus.CONFIRMED.name());
        ticketRepository.save(ticket);

        if (accountRepository != null && accountRepository.findByUsername(fan.getUsername()) == null) {
            accountRepository.save(fan);
        }

        if (supportRequestRepository != null && supportRequestRepository.findAll().isEmpty()) {
            List<SupportRequest> sampleRequests = new ArrayList<SupportRequest>();
            sampleRequests.add(new SupportRequest("SR-1001", fan.getUserId(), "", "Hỗ trợ đổi chỗ ngồi khán đài",
                    "Tôi đi cùng người thân muốn đổi sang 2 ghế liền kề nhau tại khán đài A.", "",
                    SupportRequestStatus.OPEN, LocalDateTime.now().minusHours(3), null));
            sampleRequests.add(new SupportRequest("SR-1002", fan.getUserId(), "", "Thắc mắc hóa đơn thanh toán",
                    "Tôi đã thanh toán qua chuyển khoản nhưng chưa nhận được mã QR vào email.", "",
                    SupportRequestStatus.OPEN, LocalDateTime.now().minusHours(1), null));
            supportRequestRepository.saveAll(sampleRequests);
        }
    }

    public List<Match> getMatches() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            List<Match> matches = matchRepository.findAll();
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match a, Match b) {
                    return a.getStartTime().compareTo(b.getStartTime());
                }
            });
            return matches;
        });
    }

    public List<MatchSeat> getAvailableSeats(String matchId) {
        session.require(UserRole.STAFF);
        List<MatchSeat> available = new ArrayList<MatchSeat>();
        for (MatchSeat seat : getSeatMap(matchId)) {
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                available.add(seat);
            }
        }
        return available;
    }

    public List<MatchSeat> getAvailableSeats(String matchId, String sectionId) {
        session.require(UserRole.STAFF);
        List<MatchSeat> available = new ArrayList<MatchSeat>();
        for (MatchSeat seat : getAvailableSeats(matchId)) {
            if (sectionId != null && sectionId.equals(seat.getSectionId())) {
                available.add(seat);
            }
        }
        return available;
    }

    public Map<String, Integer> getAvailableSeatCountsBySection(String matchId) {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();
            Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
            for (SeatZone zone : SeatZone.values()) {
                counts.put(zone.getSectionId(), 0);
            }
            for (MatchSeat seat : matchSeatRepository.findByMatch(matchId)) {
                if (seat.getStatus() == SeatStatus.AVAILABLE && counts.containsKey(seat.getSectionId())) {
                    counts.put(seat.getSectionId(), counts.get(seat.getSectionId()) + 1);
                }
            }
            return counts;
        });
    }

    public List<String> getAvailableVoucherCodes() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            List<String> codes = new ArrayList<String>();
            for (Voucher voucher : voucherRepository.findAll()) {
                if (voucher.hasRemainingUses() && voucher.getValidDate() != null
                        && !voucher.getValidDate().isBefore(LocalDateTime.now())) {
                    codes.add(voucher.getCode());
                }
            }
            Collections.sort(codes);
            return codes;
        });
    }

    public List<String> getAvailableVoucherSummaries() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            List<String> summaries = new ArrayList<String>();
            for (Voucher voucher : voucherRepository.findAll()) {
                if (voucher.hasRemainingUses() && voucher.getValidDate() != null
                        && !voucher.getValidDate().isBefore(LocalDateTime.now())) {
                    summaries.add(String.format("%s: discount %,.0f, %d uses left",
                            voucher.getCode(), voucher.getDiscountValue(), voucher.getRemainingUses()));
                }
            }
            Collections.sort(summaries);
            return summaries;
        });
    }

    public Payment getPaymentForTransaction(String transactionId) {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> paymentRepository.findByTransaction(transactionId));
    }

    public List<Ticket> getTicketsForTransaction(String transactionId) {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> ticketRepository.findByTransaction(transactionId));
    }

    public boolean login(String username, String password) {
        User user = accountRepository.findByUsername(username);
        if (user == null || !UserRole.STAFF.name().equalsIgnoreCase(user.getRole())
                || !PasswordHasher.verify(password, user.getPassword())) {
            return false;
        }
        session.setUser(new Staff(user.getUserId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getUserId()));
        return true;
    }

    public boolean isLoggedIn() {
        return session.getUser() != null;
    }

    public void logout() {
        session.setUser(null);
    }

    public User getCurrentUser() {
        return session.getUser();
    }

    public void showDashboard() {
        System.out.println("Staff dashboard");
    }

    public double calculateOfflineSaleTotal(String matchId, List<String> selectedSeatIds, String voucherCode) {
        return calculateOfflineSaleTotal(matchId, selectedSeatIds, voucherCode, null);
    }

    public double calculateOfflineSaleTotal(String matchId, List<String> selectedSeatIds,
            String voucherCode, String buyerContact) {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();
            validateSelectedSeats(selectedSeatIds);
            Voucher voucher = findUsableVoucher(voucherCode, buyerContact);
            double originalAmount = 0;
            for (String seatId : selectedSeatIds) {
                MatchSeat seat = matchSeatRepository.findByMatchAndSeat(matchId, seatId);
                if (seat == null) {
                    throw new IllegalArgumentException("Seat " + seatId + " does not belong to match " + matchId + ".");
                }
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new SeatAlreadyBookedException("Seat " + seatId + " is already booked or unavailable.");
                }
                originalAmount += seat.getPrice();
            }
            return Math.max(0, originalAmount - (voucher == null ? 0 : voucher.getDiscountValue()));
        });
    }

    public synchronized BookingTransaction sellTicketOffline(String staffId, String matchId,
            List<String> selectedSeatIds, String voucherCode) {
        String buyerName = "Walk-in customer";
        String buyerContact = "Not provided";
        double amount = calculateOfflineSaleTotal(matchId, selectedSeatIds, voucherCode);
        return sellTicketOffline(staffId, matchId, selectedSeatIds, voucherCode, buyerName, buyerContact,
                PaymentMethod.CASH.name(), amount, "");
    }

    public synchronized BookingTransaction sellTicketOffline(String staffId, String matchId,
            List<String> selectedSeatIds, String voucherCode,
            String buyerName, String buyerContact) {
        double amount = calculateOfflineSaleTotal(matchId, selectedSeatIds, voucherCode);
        return sellTicketOffline(staffId, matchId, selectedSeatIds, voucherCode, buyerName, buyerContact,
                PaymentMethod.CASH.name(), amount, "");
    }

    public synchronized BookingTransaction sellTicketOffline(String staffId, String matchId,
            List<String> selectedSeatIds, String voucherCode,
            String buyerName, String buyerContact,
            String paymentMethod, double amountReceived,
            String paymentReference) {
        return CsvFileLock.withLock(bookingLockPath(),
                () -> sellTicketOfflineLocked(staffId, matchId, selectedSeatIds, voucherCode, buyerName, buyerContact,
                        paymentMethod, amountReceived, paymentReference));
    }

    private BookingTransaction sellTicketOfflineLocked(String staffId, String matchId,
            List<String> selectedSeatIds, String voucherCode,
            String buyerName, String buyerContact, String paymentMethod,
            double amountReceived, String paymentReference) {
        session.require(UserRole.STAFF);
        expireStalePaymentsLocked();
        if (!session.getUser().getUserId().equals(staffId)) {
            throw new InsufficientPermissionException("Staff ID does not match the signed-in account.");
        }
        if (buyerName == null || buyerName.trim().isEmpty() || buyerName.trim().length() > 120
                || buyerContact == null || buyerContact.trim().isEmpty() || buyerContact.trim().length() > 120) {
            throw new IllegalArgumentException(
                    "Buyer name and phone/email are required (maximum 120 characters each).");
        }
        validateSelectedSeats(selectedSeatIds);
        Voucher voucher = findUsableVoucher(voucherCode, buyerContact);

        List<MatchSeat> selectedSeats = new ArrayList<MatchSeat>();
        double originalAmount = 0;
        for (String seatId : selectedSeatIds) {
            MatchSeat seat = matchSeatRepository.findByMatchAndSeat(matchId, seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat " + seatId + " does not belong to match " + matchId + ".");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat " + seatId + " is already booked or unavailable.");
            }
            selectedSeats.add(seat);
            originalAmount += seat.getPrice();
        }

        double discount = (voucher == null) ? 0 : voucher.getDiscountValue();
        double finalAmount = Math.max(0, originalAmount - discount);
        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(paymentMethod == null ? "" : paymentMethod.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Choose CASH, CARD, or BANK_TRANSFER.");
        }
        if (!Double.isFinite(amountReceived) || amountReceived < finalAmount) {
            throw new IllegalArgumentException("Amount received must be at least the amount due.");
        }
        if (method != PaymentMethod.CASH && Math.abs(amountReceived - finalAmount) > 0.005) {
            throw new IllegalArgumentException("Card and bank-transfer payments must match the amount due.");
        }
        String reference = paymentReference == null ? "" : paymentReference.trim();
        if (method != PaymentMethod.CASH && reference.isEmpty()) {
            throw new IllegalArgumentException("A payment reference is required for card or bank transfer.");
        }
        if (method != PaymentMethod.CASH) {
            for (Payment existingPayment : paymentRepository.findAll()) {
                if (reference.equalsIgnoreCase(existingPayment.getReference())
                        && !"EXPIRED".equals(existingPayment.getStatus())
                        && !"FAILED".equals(existingPayment.getStatus())) {
                    throw new IllegalArgumentException("This payment reference has already been recorded.");
                }
            }
        }
        double changeAmount = method == PaymentMethod.CASH ? amountReceived - finalAmount : 0;
        boolean electronicPayment = method != PaymentMethod.CASH;
        String saleId = UUID.randomUUID().toString();
        String txId = "TX-" + saleId;
        BookingTransaction tx = new BookingTransaction(
                txId,
                "WALKIN-" + saleId,
                staffId,
                matchId,
                voucher == null ? "" : voucher.getVoucherId(),
                originalAmount,
                discount,
                finalAmount,
                LocalDateTime.now(),
                electronicPayment ? BookingStatus.PENDING : BookingStatus.CONFIRMED);
        tx.setBuyerName(buyerName.trim());
        tx.setBuyerContact(buyerContact.trim());
        List<Ticket> createdTickets = new ArrayList<Ticket>();
        boolean voucherUpdated = false;
        Payment payment = new Payment("PAY-" + UUID.randomUUID().toString(), txId, finalAmount,
                method.name(), electronicPayment ? "PENDING" : "PAID", amountReceived, changeAmount,
                reference, LocalDateTime.now());
        boolean paymentSaved = false;
        try {
            if (!transactionRepository.save(tx)) {
                throw new IllegalStateException("Unable to create transaction.");
            }
            for (MatchSeat seat : selectedSeats) {
                if (electronicPayment) {
                    seat.hold(txId);
                } else {
                    seat.confirm();
                }
                if (!matchSeatRepository.update(seat)) {
                    throw new IllegalStateException("Unable to update seat " + seat.getSeatId() + ".");
                }
                Ticket ticket = new Ticket("T-" + UUID.randomUUID().toString(), txId,
                        seat.getSeatId(), matchId, seat.getPrice(),
                        electronicPayment ? TicketStatus.PENDING.name() : TicketStatus.CONFIRMED.name());
                if (!ticketRepository.save(ticket)) {
                    throw new IllegalStateException("Unable to create ticket for seat " + seat.getSeatId() + ".");
                }
                createdTickets.add(ticket);
            }
            if (voucher != null) {
                if (!voucher.recordUse()) {
                    throw new InvalidVoucherException("Voucher usage limit has been reached.");
                }
                if (!voucherRepository.update(voucher)) {
                    throw new IllegalStateException("Unable to mark voucher as used.");
                }
                voucherUpdated = true;
            }
            if (!paymentRepository.save(payment)) {
                throw new IllegalStateException("Unable to save payment.");
            }
            paymentSaved = true;
        } catch (RuntimeException exception) {
            for (MatchSeat seat : selectedSeats) {
                seat.release();
                matchSeatRepository.update(seat);
            }
            for (Ticket ticket : createdTickets) {
                ticketRepository.deleteById(ticket.getTicketId());
            }
            transactionRepository.deleteById(txId);
            if (paymentSaved) {
                paymentRepository.deleteById(payment.getPaymentId());
            }
            if (voucherUpdated) {
                voucher.releaseUse();
                voucherRepository.update(voucher);
            }
            throw exception;
        }

        return tx;
    }

    private void validateSelectedSeats(List<String> selectedSeatIds) {
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Please choose at least one seat.");
        }
        if (selectedSeatIds.size() > 4) {
            throw new MaxTicketsExceededException("At most 4 seats per transaction.");
        }
        if (selectedSeatIds.size() != new HashSet<String>(selectedSeatIds).size()) {
            throw new IllegalArgumentException("Duplicate seats selected.");
        }
    }

    private Voucher findUsableVoucher(String voucherCode, String buyerContact) {
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            return null;
        }
        for (Voucher candidate : voucherRepository.findAll()) {
            if (candidate.getCode().equalsIgnoreCase(voucherCode.trim())) {
                if (!candidate.hasRemainingUses() || candidate.getValidDate() == null
                        || candidate.getValidDate().isBefore(LocalDateTime.now())) {
                    break;
                }
                if (buyerHasUsedVoucher(candidate.getVoucherId(), buyerContact)) {
                    throw new InvalidVoucherException("This buyer has already used this voucher.");
                }
                return candidate;
            }
        }
        throw new InvalidVoucherException("Voucher is invalid, expired, or has reached its usage limit.");
    }

    private boolean buyerHasUsedVoucher(String voucherId, String buyerContact) {
        if (buyerContact == null || buyerContact.trim().isEmpty()) {
            return false;
        }
        String normalizedContact = buyerContact.trim().toLowerCase(java.util.Locale.ROOT);
        for (BookingTransaction transaction : transactionRepository.findAll()) {
            if (voucherId.equals(transaction.getVoucherId())
                    && transaction.getStatus() != BookingStatus.CANCELLED
                    && transaction.getBuyerContact() != null
                    && normalizedContact
                            .equals(transaction.getBuyerContact().trim().toLowerCase(java.util.Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private void expireStalePaymentsLocked() {
        LocalDateTime expiration = LocalDateTime.now().minusMinutes(15);
        for (Payment payment : paymentRepository.findAll()) {
            if (!"PENDING".equals(payment.getStatus()) || payment.getCreatedAt() == null
                    || payment.getCreatedAt().isAfter(expiration)) {
                continue;
            }
            BookingTransaction tx = transactionRepository.findById(payment.getTransactionId()).orElse(null);
            if (tx == null || tx.getStatus() != BookingStatus.PENDING) {
                continue;
            }
            for (Ticket ticket : ticketRepository.findByTransaction(tx.getTransactionId())) {
                ticket.setStatus(TicketStatus.CANCELLED.name());
                ticketRepository.update(ticket);
                MatchSeat seat = matchSeatRepository.findByMatchAndSeat(tx.getMatchId(), ticket.getSeatId());
                if (seat != null && seat.getStatus() == SeatStatus.LOCKED
                        && tx.getTransactionId().equals(seat.getLockedBy())) {
                    seat.release();
                    matchSeatRepository.update(seat);
                }
            }
            tx.setStatus(BookingStatus.CANCELLED);
            transactionRepository.update(tx);
            payment.setStatus("EXPIRED");
            paymentRepository.update(payment);
            for (Voucher voucher : voucherRepository.findAll()) {
                if (tx.getVoucherId() != null && tx.getVoucherId().equals(voucher.getVoucherId())) {
                    voucher.releaseUse();
                    voucherRepository.update(voucher);
                    break;
                }
            }
        }
    }

    public synchronized void confirmPayment(String transactionId) {
        CsvFileLock.withLock(bookingLockPath(), () -> {
            confirmPaymentLocked(transactionId);
            return null;
        });
    }

    private void confirmPaymentLocked(String transactionId) {
        session.require(UserRole.STAFF);
        expireStalePaymentsLocked();
        BookingTransaction tx = findTransaction(transactionId);
        if (tx.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Transaction is not pending.");
        }
        Payment payment = paymentRepository.findByTransaction(transactionId);
        if (payment == null || !"PENDING".equals(payment.getStatus())) {
            throw new IllegalStateException("No pending electronic payment is linked to this transaction.");
        }
        if (Math.abs(payment.getAmount() - tx.getFinalAmount()) > 0.005) {
            throw new IllegalStateException("The payment amount does not match the transaction total.");
        }
        String reference = payment.getReference() == null ? "" : payment.getReference().trim();
        if (reference.isEmpty()) {
            throw new IllegalStateException(
                    "The payment reference is missing. Verify the external transfer/card receipt first.");
        }
        List<Ticket> tickets = ticketRepository.findByTransaction(transactionId);
        if (tickets.isEmpty()) {
            throw new IllegalStateException("No tickets are linked to this transaction.");
        }
        List<MatchSeat> seats = new ArrayList<MatchSeat>();
        for (Ticket ticket : tickets) {
            if (!TicketStatus.PENDING.name().equals(ticket.getStatus())) {
                throw new IllegalStateException("Ticket " + ticket.getTicketId() + " is not pending payment.");
            }
            MatchSeat seat = matchSeatRepository.findByMatchAndSeat(tx.getMatchId(), ticket.getSeatId());
            if (seat == null || (seat.getStatus() != SeatStatus.LOCKED && seat.getStatus() != SeatStatus.BOOKED)) {
                throw new IllegalStateException(
                        "The reserved seat for ticket " + ticket.getTicketId() + " could not be verified.");
            }
            seats.add(seat);
        }
        tx.setStatus(BookingStatus.CONFIRMED);
        tx.setStaffId(session.getUser().getUserId());
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            ticket.setStatus(TicketStatus.CONFIRMED.name());
            if (!ticketRepository.update(ticket)) {
                throw new IllegalStateException("Unable to confirm ticket " + ticket.getTicketId() + ".");
            }
            seats.get(i).confirm();
            if (!matchSeatRepository.update(seats.get(i))) {
                throw new IllegalStateException("Unable to confirm the seat for ticket " + ticket.getTicketId() + ".");
            }
        }
        if (!transactionRepository.update(tx)) {
            throw new IllegalStateException("Unable to update transaction " + transactionId + ".");
        }
        payment.setStatus("PAID");
        if (!paymentRepository.update(payment)) {
            throw new IllegalStateException("Unable to confirm payment " + payment.getPaymentId() + ".");
        }
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), tx.getFanId(),
                NotificationType.PAYMENT_CONFIRMED, "Payment confirmed for transaction " + transactionId,
                LocalDateTime.now(), false));
    }

    public synchronized void confirmRefund(String transactionId) {
        CsvFileLock.withLock(bookingLockPath(), () -> {
            confirmRefundLocked(transactionId);
            return null;
        });
    }

    private void confirmRefundLocked(String transactionId) {
        session.require(UserRole.STAFF);
        BookingTransaction tx = findTransaction(transactionId);
        if (tx.getStatus() != BookingStatus.REFUND_REQUESTED) {
            throw new IllegalStateException("Transaction is not in refund request status.");
        }
        Payment payment = paymentRepository.findByTransaction(transactionId);
        if (payment == null) {
            throw new IllegalStateException("Refund cannot be processed without a payment record.");
        }
        if (!"PAID".equals(payment.getStatus())) {
            throw new IllegalStateException(
                    "Refund is only allowed after the original payment has been confirmed as paid.");
        }
        if (Math.abs(payment.getAmount() - tx.getFinalAmount()) > 0.005) {
            throw new IllegalStateException("The refund payment amount does not match the original transaction total.");
        }
        List<Ticket> tickets = ticketRepository.findByTransaction(transactionId);
        if (tickets.isEmpty()) {
            throw new IllegalStateException("No tickets are linked to this transaction.");
        }

        List<MatchSeat> seats = new ArrayList<MatchSeat>();
        for (Ticket ticket : tickets) {
            if (!TicketStatus.CONFIRMED.name().equals(ticket.getStatus())) {
                throw new IllegalStateException("Ticket " + ticket.getTicketId() + " is not eligible for refund.");
            }
            MatchSeat seat = matchSeatRepository.findByMatchAndSeat(tx.getMatchId(), ticket.getSeatId());
            if (seat == null || seat.getStatus() != SeatStatus.BOOKED) {
                throw new IllegalStateException(
                        "The booked seat for ticket " + ticket.getTicketId() + " could not be verified.");
            }
            seats.add(seat);
        }

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            ticket.setStatus(TicketStatus.REFUNDED.name());
            if (!ticketRepository.update(ticket)) {
                throw new IllegalStateException("Unable to refund ticket " + ticket.getTicketId() + ".");
            }
            seats.get(i).release();
            if (!matchSeatRepository.update(seats.get(i))) {
                throw new IllegalStateException("Unable to release the seat for ticket " + ticket.getTicketId() + ".");
            }
        }
        tx.setStatus(BookingStatus.REFUNDED);
        if (!transactionRepository.update(tx)) {
            throw new IllegalStateException("Unable to update transaction " + transactionId + ".");
        }
        if (payment != null) {
            payment.setStatus("REFUNDED");
            paymentRepository.update(payment);
        }
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), tx.getFanId(),
                NotificationType.REFUND_CONFIRMED, "Refund confirmed for transaction " + transactionId,
                LocalDateTime.now(), false));
    }

    public List<BookingTransaction> getPendingPayments() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();
            List<BookingTransaction> pending = new ArrayList<BookingTransaction>();
            for (BookingTransaction tx : transactionRepository.findAll()) {
                if (tx.getStatus() == BookingStatus.PENDING) {
                    pending.add(tx);
                }
            }
            return pending;
        });
    }

    public List<BookingTransaction> getRefundRequests() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            List<BookingTransaction> requests = new ArrayList<BookingTransaction>();
            for (BookingTransaction tx : transactionRepository.findAll()) {
                if (tx.getStatus() == BookingStatus.REFUND_REQUESTED) {
                    requests.add(tx);
                }
            }
            return requests;
        });
    }

    public List<MatchSeat> getSeatMap(String matchId) {
        session.require(UserRole.STAFF);
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match id cannot be empty.");
        }
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();
            List<MatchSeat> seats = matchSeatRepository.findByMatch(matchId.trim());
            Collections.sort(seats, new Comparator<MatchSeat>() {
                @Override
                public int compare(MatchSeat a, MatchSeat b) {
                    int sectionComparison = Integer.compare(SeatZone.orderFor(a.getSectionId()),
                            SeatZone.orderFor(b.getSectionId()));
                    if (sectionComparison != 0)
                        return sectionComparison;
                    int blockComparison = a.getBlockId().compareTo(b.getBlockId());
                    if (blockComparison != 0)
                        return blockComparison;
                    int rowComparison = Integer.compare(a.getRowNumber(), b.getRowNumber());
                    return rowComparison != 0 ? rowComparison : a.getSeatNumber().compareTo(b.getSeatNumber());
                }
            });
            return seats;
        });
    }

    public Match getMatch(String matchId) {
        session.require(UserRole.STAFF);
        if (matchId == null || matchId.trim().isEmpty()) {
            return null;
        }
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            return matchRepository.findById(matchId.trim()).orElse(null);
        });
    }

    public SeatInspectionDetails inspectSeat(String matchId, String seatQuery) {
        session.require(UserRole.STAFF);
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match id cannot be empty.");
        }
        if (seatQuery == null || seatQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Seat query cannot be empty.");
        }
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();
            Match match = matchRepository.findById(matchId.trim()).orElse(null);
            List<MatchSeat> seats = matchSeatRepository.findByMatch(matchId.trim());
            String query = seatQuery.trim();
            MatchSeat matchedSeat = null;

            for (MatchSeat seat : seats) {
                if (seat.getSeatId() != null && seat.getSeatId().equalsIgnoreCase(query)) {
                    matchedSeat = seat;
                    break;
                }
            }
            if (matchedSeat == null) {
                for (MatchSeat seat : seats) {
                    if (seat.getMatchSeatId() != null && seat.getMatchSeatId().equalsIgnoreCase(query)) {
                        matchedSeat = seat;
                        break;
                    }
                }
            }
            if (matchedSeat == null) {
                for (MatchSeat seat : seats) {
                    if (seat.getSeatNumber() != null && seat.getSeatNumber().equalsIgnoreCase(query)) {
                        matchedSeat = seat;
                        break;
                    }
                }
            }
            if (matchedSeat == null) {
                for (MatchSeat seat : seats) {
                    String fullKey = (seat.getSectionId() + "-" + seat.getBlockId() + "-R" + seat.getRowNumber() + "-" + seat.getSeatNumber()).toLowerCase();
                    if (fullKey.contains(query.toLowerCase())) {
                        matchedSeat = seat;
                        break;
                    }
                }
            }

            if (matchedSeat == null) {
                return null;
            }

            Ticket ticket = null;
            BookingTransaction tx = null;
            long remainingSeconds = 0;

            if (matchedSeat.getStatus() == SeatStatus.BOOKED) {
                for (Ticket t : ticketRepository.findByMatch(matchId.trim())) {
                    if (matchedSeat.getSeatId().equalsIgnoreCase(t.getSeatId())
                            && TicketStatus.CONFIRMED.name().equalsIgnoreCase(t.getStatus())) {
                        ticket = t;
                        break;
                    }
                }
                if (ticket != null && ticket.getTransactionId() != null && !ticket.getTransactionId().isEmpty()) {
                    tx = transactionRepository.findById(ticket.getTransactionId()).orElse(null);
                }
            } else if (matchedSeat.getStatus() == SeatStatus.LOCKED) {
                if (matchedSeat.getLockedAt() != null) {
                    LocalDateTime expiresAt = matchedSeat.getLockedAt().plusMinutes(15);
                    remainingSeconds = Math.max(0, Duration.between(LocalDateTime.now(), expiresAt).getSeconds());
                }
                if (matchedSeat.getLockedBy() != null && !matchedSeat.getLockedBy().isEmpty()) {
                    tx = transactionRepository.findById(matchedSeat.getLockedBy()).orElse(null);
                }
            }

            return new SeatInspectionDetails(matchedSeat, match, ticket, tx, remainingSeconds);
        });
    }

    public List<BookingTransaction> getTicketBuyerList(String matchId) {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            List<BookingTransaction> list = new ArrayList<BookingTransaction>();
            for (BookingTransaction transaction : transactionRepository.findByMatch(matchId)) {
                if (transaction.getStatus() == BookingStatus.CONFIRMED) {
                    list.add(transaction);
                }
            }
            Collections.sort(list, new Comparator<BookingTransaction>() {
                @Override
                public int compare(BookingTransaction a, BookingTransaction b) {
                    return a.getBookingDate().compareTo(b.getBookingDate());
                }
            });
            return list;
        });
    }

    public ShiftReport getShiftReport(String staffId, LocalDateTime from, LocalDateTime to) {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            expireStalePaymentsLocked();

            ShiftReport report = new ShiftReport();
            report.setReportId("RPT-" + UUID.randomUUID().toString());
            report.setStaffId(staffId);
            report.setStaffName(session.getUser() != null ? session.getUser().getUsername() : staffId);
            report.setShiftStart(from);
            report.setShiftEnd(to);

            // --- Filter transactions in the time window for this staff ---
            List<BookingTransaction> shiftTransactions = new ArrayList<BookingTransaction>();
            for (BookingTransaction tx : transactionRepository.findAll()) {
                if (!tx.getStaffId().equals(staffId))
                    continue;
                if (tx.getBookingDate().isBefore(from) || tx.getBookingDate().isAfter(to))
                    continue;
                shiftTransactions.add(tx);
            }

            // --- Transaction counts by status ---
            int confirmed = 0, pending = 0, cancelled = 0, refundRequested = 0, refunded = 0;
            double grossAmount = 0, discountAmount = 0, refundedAmount = 0;
            double cashRevenue = 0, cardRevenue = 0, bankRevenue = 0;

            for (BookingTransaction tx : shiftTransactions) {
                grossAmount += tx.getOriginalAmount();
                discountAmount += tx.getDiscountAmount();

                switch (tx.getStatus()) {
                    case CONFIRMED:
                        confirmed++;
                        // Look up the payment to classify revenue by method
                        Payment payment = paymentRepository.findByTransaction(tx.getTransactionId());
                        if (payment != null && "PAID".equals(payment.getStatus())) {
                            String method = payment.getMethod();
                            if ("CASH".equals(method)) {
                                cashRevenue += tx.getFinalAmount();
                            } else if ("CARD".equals(method)) {
                                cardRevenue += tx.getFinalAmount();
                            } else if ("BANK_TRANSFER".equals(method)) {
                                bankRevenue += tx.getFinalAmount();
                            } else {
                                cashRevenue += tx.getFinalAmount(); // fallback
                            }
                        } else {
                            // Confirmed but no PAID payment record — count as cash (legacy)
                            cashRevenue += tx.getFinalAmount();
                        }
                        break;
                    case PENDING:
                        pending++;
                        break;
                    case CANCELLED:
                        cancelled++;
                        break;
                    case REFUND_REQUESTED:
                        refundRequested++;
                        break;
                    case REFUNDED:
                        refunded++;
                        refundedAmount += tx.getFinalAmount();
                        break;
                }
            }

            report.setTotalTransactions(shiftTransactions.size());
            report.setConfirmedTransactions(confirmed);
            report.setPendingTransactions(pending);
            report.setCancelledTransactions(cancelled);
            report.setRefundRequestedTransactions(refundRequested);
            report.setRefundedTransactions(refunded);

            report.setCashRevenue(cashRevenue);
            report.setCardRevenue(cardRevenue);
            report.setBankTransferRevenue(bankRevenue);
            report.setTotalGrossAmount(grossAmount);
            report.setTotalDiscountAmount(discountAmount);
            report.setRefundedAmount(refundedAmount);

            // --- Ticket counts: collect tickets for the shift transactions ---
            Set<String> txIds = new HashSet<String>();
            Set<String> matchIds = new HashSet<String>();
            for (BookingTransaction tx : shiftTransactions) {
                txIds.add(tx.getTransactionId());
                matchIds.add(tx.getMatchId());
            }

            int totalTickets = 0, confirmedTickets = 0, pendingTickets = 0;
            int cancelledTickets = 0, refundedTickets = 0;
            for (Ticket ticket : ticketRepository.findAll()) {
                if (!txIds.contains(ticket.getTransactionId()))
                    continue;
                totalTickets++;
                String status = ticket.getStatus();
                if ("CONFIRMED".equals(status)) {
                    confirmedTickets++;
                } else if ("PENDING".equals(status)) {
                    pendingTickets++;
                } else if ("CANCELLED".equals(status)) {
                    cancelledTickets++;
                } else if ("REFUNDED".equals(status)) {
                    refundedTickets++;
                }
            }

            report.setTotalTickets(totalTickets);
            report.setConfirmedTickets(confirmedTickets);
            report.setPendingTickets(pendingTickets);
            report.setCancelledTickets(cancelledTickets);
            report.setRefundedTickets(refundedTickets);

            // --- Seat snapshot across all matches this staff touched ---
            int available = 0, booked = 0, locked = 0;
            for (String matchId : matchIds) {
                for (MatchSeat seat : matchSeatRepository.findByMatch(matchId)) {
                    switch (seat.getStatus()) {
                        case AVAILABLE:
                            available++;
                            break;
                        case BOOKED:
                            booked++;
                            break;
                        case LOCKED:
                            locked++;
                            break;
                    }
                }
            }
            report.setSeatsAvailable(available);
            report.setSeatsBooked(booked);
            report.setSeatsLocked(locked);

            return report;
        });
    }

    public void supportContact(String fanId, String message) {
        session.require(UserRole.STAFF);
        if (fanId == null || fanId.trim().isEmpty() || message == null || message.trim().isEmpty()
                || message.length() > 1000) {
            throw new IllegalArgumentException("Fan id and a message of at most 1000 characters are required.");
        }
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), fanId.trim(),
                NotificationType.SUPPORT_MESSAGE, message.trim(), LocalDateTime.now(), false));
    }

    public SupportRequest createSupportRequest(String fanId, String subject, String message) {
        if (fanId == null || fanId.trim().isEmpty()) {
            throw new IllegalArgumentException("Fan ID is required.");
        }
        if (subject == null || subject.trim().isEmpty() || subject.length() > 200) {
            throw new IllegalArgumentException("Subject must be between 1 and 200 characters.");
        }
        if (message == null || message.trim().isEmpty() || message.length() > 2000) {
            throw new IllegalArgumentException("Message must be between 1 and 2000 characters.");
        }
        String reqId = "SR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SupportRequest req = new SupportRequest(reqId, fanId.trim(), "", subject.trim(), message.trim(), "",
                SupportRequestStatus.OPEN, LocalDateTime.now(), null);
        supportRequestRepository.save(req);
        return req;
    }

    public List<SupportRequest> getPendingSupportRequests() {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        return supportRequestRepository.findByStatus(SupportRequestStatus.OPEN);
    }

    public List<SupportRequest> getAllSupportRequests() {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        List<SupportRequest> list = supportRequestRepository.findAll();
        Collections.sort(list, new Comparator<SupportRequest>() {
            @Override
            public int compare(SupportRequest a, SupportRequest b) {
                if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
        });
        return list;
    }

    public SupportRequest resolveSupportRequest(String requestId, String replyMessage, boolean approved) {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID is required.");
        }
        if (replyMessage == null || replyMessage.trim().isEmpty() || replyMessage.length() > 1000) {
            throw new IllegalArgumentException("Reply message must be between 1 and 1000 characters.");
        }
        List<SupportRequest> all = supportRequestRepository.findAll();
        SupportRequest req = null;
        for (SupportRequest r : all) {
            if (r.getRequestId().equalsIgnoreCase(requestId.trim())) {
                req = r;
                break;
            }
        }
        if (req == null) {
            throw new IllegalArgumentException("Support request not found: " + requestId);
        }
        if (req.getStatus() != SupportRequestStatus.OPEN) {
            throw new IllegalStateException("Support request is already processed (" + req.getStatus() + ").");
        }

        String staffIdentifier = session.getUser() != null ? session.getUser().getUserId() : "STAFF";
        String staffName = session.getUser() != null ? session.getUser().getUsername() : "Staff";
        req.setStaffId(staffIdentifier);
        req.setResponse(replyMessage.trim());
        req.setStatus(approved ? SupportRequestStatus.RESOLVED : SupportRequestStatus.REJECTED);
        req.setResolvedAt(LocalDateTime.now());
        supportRequestRepository.update(req);

        // Notify Fan
        String statusDesc = approved ? "RESOLVED" : "REJECTED";
        String notiText = String.format("Support Request [%s] was %s by staff %s. Response: %s",
                req.getSubject(), statusDesc, staffName, replyMessage.trim());
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), req.getFanId(),
                NotificationType.SUPPORT_MESSAGE, notiText, LocalDateTime.now(), false));

        return req;
    }

    public static class FanRegistrationResult {
        private final User user;
        private final String rawPassword;
        private final int initialPoints;

        public FanRegistrationResult(User user, String rawPassword, int initialPoints) {
            this.user = user;
            this.rawPassword = rawPassword;
            this.initialPoints = initialPoints;
        }

        public User getUser() {
            return user;
        }

        public String getRawPassword() {
            return rawPassword;
        }

        public int getInitialPoints() {
            return initialPoints;
        }
    }

    public FanRegistrationResult registerFanAtCounter(String fullName, String phoneNumber, String email,
            String customUsername, String customPassword) {
        session.require(UserRole.STAFF);

        // 1. Validate Full Name
        if (fullName == null || fullName.trim().isEmpty() || fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Họ và tên khách hàng là bắt buộc (tối đa 100 ký tự).");
        }
        String cleanName = fullName.trim();

        // 2. Validate Phone Number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại là bắt buộc để định danh Cổ động viên.");
        }
        String cleanPhone = phoneNumber.trim().replaceAll("[^0-9]", "");
        if (!cleanPhone.matches("^0[0-9]{9}$")) {
            throw new IllegalArgumentException(
                    "Số điện thoại không hợp lệ (yêu cầu 10 chữ số, bắt đầu bằng số 0, ví dụ: 0912345678).");
        }
        if (accountRepository.findByPhone(cleanPhone) != null) {
            throw new IllegalArgumentException("Số điện thoại này đã được đăng ký cho tài khoản khác: " + cleanPhone);
        }

        // 3. Validate Email
        String cleanEmail = "";
        if (email != null && !email.trim().isEmpty()) {
            cleanEmail = email.trim();
            if (!cleanEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                throw new IllegalArgumentException("Định dạng email không hợp lệ: " + cleanEmail);
            }
            if (accountRepository.findByEmail(cleanEmail) != null) {
                throw new IllegalArgumentException("Email này đã được sử dụng cho tài khoản khác: " + cleanEmail);
            }
        }

        // 4. Handle Username
        String finalUsername;
        if (customUsername == null || customUsername.trim().isEmpty()) {
            finalUsername = "fan" + cleanPhone.substring(cleanPhone.length() - 6);
        } else {
            finalUsername = customUsername.trim();
            if (!finalUsername.matches("[A-Za-z0-9._-]{3,32}")) {
                throw new IllegalArgumentException("Username phải từ 3-32 ký tự hợp lệ (chữ, số, ., _, -).");
            }
        }
        if (accountRepository.findByUsername(finalUsername) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + finalUsername);
        }

        // 5. Handle Password
        String finalPassword;
        if (customPassword == null || customPassword.trim().isEmpty()) {
            int randomSuffix = 1000 + (int) (Math.random() * 9000);
            finalPassword = "Fan@" + randomSuffix;
        } else {
            finalPassword = customPassword.trim();
            if (finalPassword.length() < 8) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
            }
        }

        // 6. Generate readable Fan ID
        String fanId = "FAN-" + cleanPhone.substring(cleanPhone.length() - 6);
        if (accountRepository.findById(fanId) != null) {
            fanId = "FAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        // 7. Initial loyalty points
        int welcomePoints = 50;

        // 8. Create User/Fan and persist
        User user = new User(fanId, finalUsername, PasswordHasher.hash(finalPassword),
                cleanEmail, UserRole.FAN.name(), cleanName, cleanPhone, welcomePoints);
        if (!accountRepository.save(user)) {
            throw new IllegalStateException("Không thể lưu tài khoản CĐV vào hệ thống.");
        }

        // 9. Send Welcome notification
        String welcomeMsg = String.format(
                "Chào mừng %s gia nhập Hội Cổ động viên! Bạn được tặng %d điểm tích lũy khởi đầu. Mã CĐV: %s.",
                cleanName, welcomePoints, fanId);
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), fanId,
                NotificationType.REMINDER, welcomeMsg, LocalDateTime.now(), false));

        return new FanRegistrationResult(user, finalPassword, welcomePoints);
    }

    public User createFanAccount(String username, String password, String email) {
        session.require(UserRole.STAFF);
        if (username == null || !username.trim().matches("[A-Za-z0-9._-]{3,32}")
                || password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Username must be 3-32 valid characters and password at least 8 characters.");
        }
        if (accountRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("That username is already assigned.");
        }
        String id = "FAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        User user = new User(id, username.trim(), PasswordHasher.hash(password),
                email == null ? "" : email.trim(), UserRole.FAN.name(), username.trim(), "", 50);
        if (!accountRepository.save(user)) {
            throw new IllegalStateException("Unable to save the fan account.");
        }
        return user;
    }

    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> result = new ArrayList<Notification>();
        for (Notification notification : notificationRepository.findAll()) {
            if (notification.getUserId().equals(userId)) {
                result.add(notification);
            }
        }
        return result;
    }

    private Path bookingLockPath() {
        return accountRepository.getDataDirectory().resolve(".booking-operations.lock");
    }

    public BookingTransaction findTransaction(String transactionId) {
        for (BookingTransaction tx : transactionRepository.findAll()) {
            if (tx.getTransactionId().equals(transactionId)) {
                return tx;
            }
        }
        throw new IllegalArgumentException("Transaction not found: " + transactionId);
    }
}
