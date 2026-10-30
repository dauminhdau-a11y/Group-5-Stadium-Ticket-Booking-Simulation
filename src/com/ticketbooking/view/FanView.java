package com.ticketbooking.view;

import com.ticketbooking.controller.AccountController;
import com.ticketbooking.controller.TransactionController;
import com.ticketbooking.controller.VoucherController;
import com.ticketbooking.model.Account;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Notification;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.SupportRequest;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.Voucher;
import com.ticketbooking.model.enums.SupportStatus;
import com.ticketbooking.repository.NotificationRepository;
import com.ticketbooking.repository.SupportRequestRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class FanView {
    private Scanner scanner;
    private SeatMapView seatMapView;
    private NotificationRepository notificationRepository;
    private SupportRequestRepository supportRequestRepository;

    public FanView(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner must not be null.");
        }
        this.scanner = scanner;
        this.seatMapView = new SeatMapView();
        this.notificationRepository = new NotificationRepository("data/notifications.csv");
        this.supportRequestRepository = new SupportRequestRepository("data/support_requests.csv");
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(Fan fan, AccountController accountController,
            TransactionController transactionController, VoucherController voucherController,
            TransactionView transactionView) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== FAN MENU ===");
            System.out.println("Welcome, " + fan.getUsername() + "!");
            System.out.println("1. View profile");
            System.out.println("2. Update profile");
            System.out.println("3. View seat map");
            System.out.println("4. Book tickets");
            System.out.println("5. Check / Verify voucher");
            System.out.println("6. View booking history");
            System.out.println("7. Refund ticket");
            System.out.println("8. Receive notifications");
            System.out.println("9. Support contact");
            System.out.println("10. Logout");
            System.out.print("Choose (1-10): ");

            String input = readLine();
            if (input == null) {
                return;
            }
            String choice = input.trim();
            try {
                switch (choice) {
                    case "1":
                        showProfile(fan, accountController);
                        break;
                    case "2":
                        updateProfile(fan, accountController);
                        break;
                    case "3":
                        viewSeatMap(transactionController);
                        break;
                    case "4":
                        if (transactionView != null) {
                            transactionView.showTransactionForm(fan.getId());
                        }
                        break;
                    case "5":
                        applyVoucher(voucherController);
                        break;
                    case "6":
                        viewBookingHistory(fan.getId(), transactionController);
                        break;
                    case "7":
                        refundTicket(transactionController);
                        break;
                    case "8":
                        receiveNotifications(fan.getId());
                        break;
                    case "9":
                        supportContact(fan.getId());
                        break;
                    case "10":
                        loggedIn = false;
                        System.out.println("You have logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select from 1 to 10.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void showProfile(Fan fan, AccountController accountController) {
        try {
            Account account = accountController.viewProfile(fan.getId());
            if (!(account instanceof Fan)) {
                throw new IllegalArgumentException("Fan account was not found.");
            }
            Fan profile = (Fan) account;
            System.out.println("\n--- Fan Profile ---");
            System.out.println("Fan ID:         " + profile.getId());
            System.out.println("Username:       " + profile.getUsername());
            System.out.println("Email:          " + valueOrDefault(profile.getEmail()));
            System.out.println("Phone:          " + valueOrDefault(profile.getPhone()));
            System.out.println("Date of birth:  " + valueOrDefault(
                    profile.getDateOfBirth() == null ? null : profile.getDateOfBirth().toString()));
            System.out.println("Loyalty points: " + profile.getLoyaltyPoints());
            System.out.println("Membership:     " + profile.getMembershipLevel());
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Unable to load profile: " + e.getMessage());
        }
    }

    public void updateProfile(Fan fan, AccountController accountController) {
        try {
            Account account = accountController.viewProfile(fan.getId());
            if (!(account instanceof Fan)) {
                throw new IllegalArgumentException("Fan account was not found.");
            }
            Fan profile = (Fan) account;
            System.out.println("\n--- Update Profile (leave blank to keep current value) ---");
            System.out.print("Email [" + valueOrDefault(profile.getEmail()) + "]: ");
            String emailInput = readLine();
            System.out.print("Phone [" + valueOrDefault(profile.getPhone()) + "]: ");
            String phoneInput = readLine();
            System.out.print("Date of birth (YYYY-MM-DD) ["
                    + valueOrDefault(profile.getDateOfBirth() == null
                            ? null : profile.getDateOfBirth().toString())
                    + "]: ");
            String dateInput = readLine();

            String email = (emailInput != null && !emailInput.trim().isEmpty())
                    ? emailInput.trim() : profile.getEmail();
            String phone = (phoneInput != null && !phoneInput.trim().isEmpty())
                    ? phoneInput.trim() : profile.getPhone();
            LocalDate dateOfBirth = profile.getDateOfBirth();
            if (dateInput != null && !dateInput.trim().isEmpty()) {
                dateOfBirth = LocalDate.parse(dateInput.trim());
            }

            profile.setEmail(email);
            profile.setPhone(phone);
            profile.setDateOfBirth(dateOfBirth);
            accountController.updateFanAccount(profile);
            System.out.println("Profile updated successfully.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date. Use YYYY-MM-DD.");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Unable to update profile: " + e.getMessage());
        }
    }

    public void viewSeatMap(TransactionController transactionController) throws IOException {
        System.out.print("Enter Match ID to inspect seat map (e.g. M001): ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            return;
        }
        List<Seat> seats = transactionController.getAllSeatsForMatch(matchId.trim());
        seatMapView.displaySeatMap(matchId.trim(), seats);
    }

    public void applyVoucher(VoucherController voucherController) {
        System.out.print("Enter voucher code to check validity (e.g. DISCOUNT10, GIAM20): ");
        String code = readLine();
        if (code == null || code.trim().isEmpty()) {
            return;
        }
        try {
            Voucher voucher = voucherController.getVoucherByCode(code.trim());
            if (voucher == null) {
                System.out.println("Voucher code '" + code.trim() + "' does not exist.");
                return;
            }
            if (voucher.isUsed()) {
                System.out.println("Voucher '" + code.trim() + "' has already been redeemed.");
                return;
            }
            String discountStr = voucher.getDiscountPercent() > 0
                    ? String.format("%.0f%% OFF", voucher.getDiscountPercent())
                    : String.format("%,.0f VND OFF", voucher.getDiscountAmount());
            System.out.println(">> Voucher is VALID! Benefit: " + discountStr);
        } catch (IOException e) {
            System.out.println("Unable to verify voucher: " + e.getMessage());
        }
    }

    public void viewBookingHistory(String fanId, TransactionController transactionController) throws IOException {
        System.out.println("\n=== YOUR BOOKING HISTORY ===");
        List<Transaction> transactions = transactionController.getTransactionsByFan(fanId);
        if (transactions.isEmpty()) {
            System.out.println("You have no booking transactions yet.");
            return;
        }
        System.out.printf("%-14s | %-10s | %-20s | %-12s | %-12s | %-10s%n",
                "Transaction ID", "Match ID", "Seats", "Paid Amount", "Method", "Status");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Transaction txn : transactions) {
            String seatsStr = String.join(", ", txn.getSeatIds());
            System.out.printf("%-14s | %-10s | %-20s | %-12s | %-12s | %-10s%n",
                    txn.getTransactionId(),
                    txn.getMatchId(),
                    seatsStr,
                    String.format("%,.0f VND", txn.getTotalAmount()),
                    txn.getPaymentMethod(),
                    txn.getStatus());
        }
    }

    public void refundTicket(TransactionController transactionController) {
        System.out.print("Enter Transaction ID to refund: ");
        String txnId = readLine();
        if (txnId == null || txnId.trim().isEmpty()) {
            return;
        }
        try {
            boolean success = transactionController.refundTransaction(txnId.trim());
            if (success) {
                System.out.println("Transaction " + txnId.trim() + " refunded successfully. Seats are released.");
            } else {
                System.out.println("Could not refund transaction. Please check Transaction ID or status.");
            }
        } catch (IOException e) {
            System.out.println("Unable to process refund: " + e.getMessage());
        }
    }

    public void receiveNotifications(String fanId) {
        try {
            System.out.println("\n=== NOTIFICATIONS ===");
            List<Notification> list = notificationRepository.findByFanId(fanId);
            if (list.isEmpty()) {
                System.out.println("You have no notifications.");
                return;
            }
            for (Notification n : list) {
                String readStatus = n.isRead() ? "[Read]" : "[NEW]";
                System.out.println(readStatus + " " + n.getCreatedAt() + " - " + n.getMessage());
            }
            notificationRepository.markAllAsRead(fanId);
        } catch (IOException e) {
            System.out.println("Unable to load notifications: " + e.getMessage());
        }
    }

    public void supportContact(String fanId) {
        System.out.println("\n=== SUPPORT CONTACT ===");
        System.out.println("1. Submit new support request");
        System.out.println("2. View my support requests");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        String choice = readLine();
        if ("1".equals(choice)) {
            System.out.print("Subject: ");
            String subject = readLine();
            System.out.print("Content: ");
            String content = readLine();
            try {
                String reqId = "REQ-" + UUID.randomUUID().toString().substring(0, 8);
                SupportRequest req = new SupportRequest(reqId, fanId, subject, content);
                supportRequestRepository.save(req);
                System.out.println("Your support request has been submitted. Support ID: " + reqId);
            } catch (IOException e) {
                System.out.println("Unable to submit support request: " + e.getMessage());
            }
        } else if ("2".equals(choice)) {
            try {
                List<SupportRequest> requests = supportRequestRepository.findByFanId(fanId);
                if (requests.isEmpty()) {
                    System.out.println("You have no support requests.");
                    return;
                }
                for (SupportRequest r : requests) {
                    System.out.println("[" + r.getStatus() + "] " + r.getRequestId() + ": " + r.getSubject());
                    System.out.println("   Content:  " + r.getContent());
                    if (r.getResponse() != null && !r.getResponse().isEmpty()) {
                        System.out.println("   Response: " + r.getResponse());
                    }
                }
            } catch (IOException e) {
                System.out.println("Unable to load support requests: " + e.getMessage());
            }
        }
    }

    public String valueOrDefault(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}