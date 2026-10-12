package com.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Path;

import com.exception.InsufficientPermissionException;
import com.exception.InvalidVoucherException;
import com.exception.MaxTicketsExceededException;
import com.exception.SeatAlreadyBookedException;
import com.exception.VoucherInvalidException;
import com.model.BookingTransaction;
import com.model.MatchSeat;
import com.model.Notification;
import com.model.Staff;
import com.model.Ticket;
import com.model.User;
import com.model.Voucher;
import com.model.enums.BookingStatus;
import com.model.enums.NotificationType;
import com.model.enums.SeatStatus;
import com.model.enums.TicketStatus;
import com.model.enums.UserRole;
import com.repository.impl.BookingTransactionRepository;
import com.repository.impl.MatchSeatRepository;
import com.repository.impl.NotificationRepository;
import com.repository.impl.StaffAccountRepository;
import com.repository.impl.TicketRepository;
import com.repository.impl.VoucherRepository;
import com.util.CsvFileLock;
import com.util.PasswordHasher;

public class StaffController {
    private final SessionContext session;
    private final MatchSeatRepository matchSeatRepository;
    private final BookingTransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final TicketRepository ticketRepository;
    private final StaffAccountRepository accountRepository;
    private final VoucherRepository voucherRepository;

    public StaffController() {
        this(new StaffAccountRepository());
    }

    public StaffController(StaffAccountRepository accountRepository) {
        this(new SessionContext(), new MatchSeatRepository(), new BookingTransactionRepository(),
                new NotificationRepository(), new TicketRepository(), accountRepository, new VoucherRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
                          BookingTransactionRepository transactionRepository,
                          NotificationRepository notificationRepository,
                          TicketRepository ticketRepository) {
        this(session, matchSeatRepository, transactionRepository, notificationRepository,
            ticketRepository, new StaffAccountRepository(), new VoucherRepository());
    }

    public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
                          BookingTransactionRepository transactionRepository,
                          NotificationRepository notificationRepository,
                          TicketRepository ticketRepository,
                          StaffAccountRepository accountRepository) {
                this(session, matchSeatRepository, transactionRepository, notificationRepository,
                    ticketRepository, accountRepository, new VoucherRepository());
                }

                public StaffController(SessionContext session, MatchSeatRepository matchSeatRepository,
                          BookingTransactionRepository transactionRepository,
                          NotificationRepository notificationRepository,
                          TicketRepository ticketRepository,
                          StaffAccountRepository accountRepository,
                          VoucherRepository voucherRepository) {
        this.session = session;
        this.matchSeatRepository = matchSeatRepository;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
        this.ticketRepository = ticketRepository;
        this.accountRepository = accountRepository;
                this.voucherRepository = voucherRepository;
        CsvFileLock.withLock(bookingLockPath(), () -> {
            seedSampleData();
            return null;
        });
    }

    private void seedSampleData() {
        for (int i = 1; i <= 4; i++) {
            MatchSeat seat = new MatchSeat("MS-" + i, "M-001", "S-" + i, "VIP", "A" + i, 150000 + (i * 25000));
            seat.setStatus(i <= 2 ? SeatStatus.BOOKED : SeatStatus.AVAILABLE);
            matchSeatRepository.save(seat);
        }

        Voucher voucher = new Voucher("V-001", "WELCOME10", 10000, LocalDateTime.now().plusDays(30));
        voucherRepository.save(voucher);

        User fan = new User("FAN-001", "fan01", "123456", "fan1@gmail.com", UserRole.FAN.name());

        BookingTransaction tx = new BookingTransaction("TX-001", fan.getUserId(), "STAFF-001", "M-001", voucher.getVoucherId(),
                300000, 10000, 290000, LocalDateTime.now().minusDays(1), BookingStatus.CONFIRMED);
        transactionRepository.save(tx);

        Ticket ticket = new Ticket("T-001", tx.getTransactionId(), "S-1", "M-001",
            150000, TicketStatus.CONFIRMED.name());
        ticketRepository.save(ticket);
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

    public synchronized BookingTransaction sellTicketOffline(String staffId, String matchId, List<String> selectedSeatIds, String voucherCode) {
        return CsvFileLock.withLock(bookingLockPath(), () ->
                sellTicketOfflineLocked(staffId, matchId, selectedSeatIds, voucherCode));
    }

    private BookingTransaction sellTicketOfflineLocked(String staffId, String matchId,
                                                        List<String> selectedSeatIds, String voucherCode) {
        session.require(UserRole.STAFF);
        if (!session.getUser().getUserId().equals(staffId)) {
            throw new InsufficientPermissionException("Staff ID does not match the signed-in account.");
        }
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            throw new IllegalArgumentException("Please choose at least one seat.");
        }
        if (selectedSeatIds.size() > 4) {
            throw new MaxTicketsExceededException("At most 4 seats per transaction.");
        }
        if (selectedSeatIds.size() != new java.util.HashSet<String>(selectedSeatIds).size()) {
            throw new IllegalArgumentException("Duplicate seats selected.");
        }

        Voucher voucher = null;
        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            for (Voucher candidate : voucherRepository.findAll()) {
                if (candidate.getCode().equalsIgnoreCase(voucherCode.trim())) {
                    voucher = candidate;
                    break;
                }
            }
            if (voucher == null || voucher.isUsed() || voucher.getValidDate() == null
                    || voucher.getValidDate().isBefore(LocalDateTime.now())) {
                throw new InvalidVoucherException("Voucher is invalid or expired.");
            }
        }

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
                BookingStatus.CONFIRMED
        );
        List<Ticket> createdTickets = new ArrayList<Ticket>();
        boolean voucherUpdated = false;
        try {
            if (!transactionRepository.save(tx)) {
                throw new IllegalStateException("Unable to create transaction.");
            }
            for (MatchSeat seat : selectedSeats) {
                seat.confirm();
                if (!matchSeatRepository.update(seat)) {
                    throw new IllegalStateException("Unable to update seat " + seat.getSeatId() + ".");
                }
                Ticket ticket = new Ticket("T-" + UUID.randomUUID().toString(), txId,
                        seat.getSeatId(), matchId, seat.getPrice(), TicketStatus.CONFIRMED.name());
                if (!ticketRepository.save(ticket)) {
                    throw new IllegalStateException("Unable to create ticket for seat " + seat.getSeatId() + ".");
                }
                createdTickets.add(ticket);
            }
            if (voucher != null) {
                voucher.setUsed(true);
                if (!voucherRepository.update(voucher)) {
                    throw new IllegalStateException("Unable to mark voucher as used.");
                }
                voucherUpdated = true;
            }
        } catch (RuntimeException exception) {
            for (MatchSeat seat : selectedSeats) {
                seat.release();
                matchSeatRepository.update(seat);
            }
            for (Ticket ticket : createdTickets) {
                ticketRepository.deleteById(ticket.getTicketId());
            }
            transactionRepository.deleteById(txId);
            if (voucherUpdated) {
                voucher.setUsed(false);
                voucherRepository.update(voucher);
            }
            throw exception;
        }

        return tx;
    }

    public synchronized void confirmPayment(String transactionId) {
        CsvFileLock.withLock(bookingLockPath(), () -> {
            confirmPaymentLocked(transactionId);
            return null;
        });
    }

    private void confirmPaymentLocked(String transactionId) {
        session.require(UserRole.STAFF);
        BookingTransaction tx = findTransaction(transactionId);
        if (tx.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Transaction is not pending.");
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
                throw new IllegalStateException("The reserved seat for ticket " + ticket.getTicketId() + " could not be verified.");
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
                throw new IllegalStateException("The booked seat for ticket " + ticket.getTicketId() + " could not be verified.");
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
        notificationRepository.save(new Notification("N-" + UUID.randomUUID().toString(), tx.getFanId(),
                NotificationType.REFUND_CONFIRMED, "Refund confirmed for transaction " + transactionId,
                LocalDateTime.now(), false));
    }

    public List<BookingTransaction> getPendingPayments() {
        session.require(UserRole.STAFF);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
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
            List<MatchSeat> seats = matchSeatRepository.findByMatch(matchId.trim());
            Collections.sort(seats, new Comparator<MatchSeat>() {
                @Override public int compare(MatchSeat a, MatchSeat b) {
                    int sectionComparison = a.getSectionId().compareTo(b.getSectionId());
                    return sectionComparison != 0 ? sectionComparison : a.getSeatNumber().compareTo(b.getSeatNumber());
                }
            });
            return seats;
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
                @Override public int compare(BookingTransaction a, BookingTransaction b) {
                    return a.getBookingDate().compareTo(b.getBookingDate());
                }
            });
            return list;
        });
    }

    public Map<String, Double> getShiftReport(String staffId, LocalDateTime from, LocalDateTime to) {
        session.require(UserRole.STAFF, UserRole.ADMIN);
        return CsvFileLock.withLock(bookingLockPath(), () -> {
            Map<String, Double> report = new LinkedHashMap<String, Double>();
            double revenue = 0;
            int count = 0;
            for (BookingTransaction tx : transactionRepository.findAll()) {
                if (!tx.getStaffId().equals(staffId)) continue;
                if (tx.getStatus() != BookingStatus.CONFIRMED) continue;
                if (tx.getBookingDate().isBefore(from) || tx.getBookingDate().isAfter(to)) continue;
                count++;
                revenue += tx.getFinalAmount();
            }
            report.put("transactions", (double) count);
            report.put("revenue", revenue);
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

    public User createFanAccount(String username, String password, String email) {
        session.require(UserRole.STAFF);
        if (username == null || !username.trim().matches("[A-Za-z0-9._-]{3,32}")
                || password == null || password.length() < 8) {
            throw new IllegalArgumentException("Username must be 3-32 valid characters and password at least 8 characters.");
        }
        if (accountRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("That username is already assigned.");
        }
        String id = "FAN-" + UUID.randomUUID().toString();
        User user = new User(id, username.trim(), PasswordHasher.hash(password),
                email == null ? "" : email.trim(), UserRole.FAN.name());
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
