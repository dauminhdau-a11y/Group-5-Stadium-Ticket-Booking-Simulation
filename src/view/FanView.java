package view;

import controller.AccountController;
import controller.MatchController;
import controller.NotificationController;
import controller.TransactionController;
import controller.VoucherController;
import model.Account;
import model.Fan;
import model.Seat;
import model.SupportRequest;
import model.Ticket;
import model.Transaction;
import model.Voucher;
import model.enums.TransactionStatus;
import repository.SupportRequestRepository;
import repository.NotificationRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class FanView {
    private Scanner scanner;
    private SeatMapView seatMapView;
    private MatchController matchController;
    private MatchView matchView;
    private NotificationView notificationView;
    private SupportRequestRepository supportRequestRepository;

    public FanView(Scanner scanner) {
        this(scanner, new MatchController(),
                new NotificationView(new NotificationController(new NotificationRepository()), scanner));
    }

    public FanView(Scanner scanner, MatchController matchController) {
        this(scanner, matchController,
                new NotificationView(new NotificationController(new NotificationRepository()), scanner));
    }

    public FanView(Scanner scanner, MatchController matchController, NotificationView notificationView) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner must not be null.");
        }
        if (matchController == null) {
            throw new IllegalArgumentException("Match controller must not be null.");
        }
        if (notificationView == null) {
            throw new IllegalArgumentException("Notification view must not be null.");
        }
        this.scanner = scanner;
        this.seatMapView = new SeatMapView();
        this.matchController = matchController;
        this.matchView = new MatchView(matchController, scanner);
        this.notificationView = notificationView;
        this.supportRequestRepository = new SupportRequestRepository("data/support_requests.csv");
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
        this.matchView.setScanner(scanner);
        this.notificationView.setScanner(scanner);
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
            System.out.println("3. Search match");
            System.out.println("4. Filter matches");
            System.out.println("5. View seat map");
            System.out.println("6. Book tickets");
            System.out.println("7. Check voucher");
            System.out.println("8. View booking history");
            System.out.println("9. Notifications");
            System.out.println("10. Refund ticket");
            System.out.println("11. Support contact");
            System.out.println("12. Logout");
            System.out.print("Choose (1-12): ");

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
                        searchMatch();
                        break;
                    case "4":
                        filterMatches();
                        break;
                    case "5":
                        viewSeatMap(transactionController);
                        break;
                    case "6":
                        if (transactionView != null) {
                            transactionView.showTransactionForm(fan.getId());
                        }
                        break;
                    case "7":
                        checkVouchers(voucherController);
                        break;
                    case "8":
                        viewBookingHistory(fan.getId(), transactionController);
                        break;
                    case "9":
                        notificationView.showForFan(fan.getId());
                        break;
                    case "10":
                        refundTicket(fan.getId(), transactionController);
                        break;
                    case "11":
                        supportContact(fan.getId());
                        break;
                    case "12":
                        loggedIn = false;
                        System.out.println("You have logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select from 1 to 12.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void searchMatch() throws IOException {
        System.out.print("\nEnter keyword to search match (team, stadium, ID): ");
        String keyword = readLine();
        matchView.displayMatches(matchController.searchMatches(keyword));
    }

    public void filterMatches() throws IOException {
        matchView.filterMatches();
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
        seatMapView.displaySeatMap(matchId.trim(), seats, false);

        System.out.print("Do you want to view all seats in full detail (without truncation)? (y/n, or 'all'): ");
        String opt = readLine();
        if (opt != null && (opt.trim().equalsIgnoreCase("y") || opt.trim().equalsIgnoreCase("yes") || opt.trim().equalsIgnoreCase("all"))) {
            seatMapView.displaySeatMap(matchId.trim(), seats, true);
        }
    }

    public void checkVouchers(VoucherController voucherController) {
        try {
            List<Voucher> vouchers = voucherController.getAllVouchers();
            if (vouchers.isEmpty()) {
                System.out.println("There are no vouchers available.");
                return;
            }

            System.out.println("\n--- AVAILABLE VOUCHERS AND CONDITIONS ---");
            System.out.println("One voucher can be applied per transaction. Vouchers may be reused in later transactions.");
            for (Voucher voucher : vouchers) {
                String discountStr = voucher.getDiscountPercent() > 0
                        ? String.format("%.0f%% OFF", voucher.getDiscountPercent())
                        : String.format("%,.0f VND OFF", voucher.getDiscountAmount());
                System.out.printf("%-16s | %-16s | Minimum order: %,.0f VND%n",
                        voucher.getCode(), discountStr, voucher.getMinimumOrderAmount());
            }
        } catch (IOException e) {
            System.out.println("Unable to load vouchers: " + e.getMessage());
        }
    }

    public void viewBookingHistory(String fanId, TransactionController transactionController) throws IOException {
        List<Transaction> rawList = transactionController.getTransactionsByFan(fanId);
        if (rawList == null || rawList.isEmpty()) {
            System.out.println("\n=== YOUR BOOKING HISTORY ===");
            System.out.println("You have no booking transactions yet.");
            return;
        }

        List<Transaction> transactions = new ArrayList<>(rawList);
        int currentSort = 1;

        while (true) {
            sortTransactions(transactions, currentSort);

            int maxSeatsLen = 16;
            for (Transaction txn : transactions) {
                if (txn.getSeatIds() != null) {
                    String s = String.join(", ", txn.getSeatIds());
                    if (s.length() > maxSeatsLen) {
                        maxSeatsLen = s.length();
                    }
                }
            }
            int seatsColWidth = Math.max(16, maxSeatsLen);

            System.out.println("\n============================= YOUR BOOKING HISTORY =============================");
            String sortName = getSortName(currentSort);
            System.out.println("Sorted by: " + sortName);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String headerFormat = "%-3s | %-14s | %-16s | %-8s | %-" + seatsColWidth + "s | %15s | %-12s | %-11s%n";
            String rowFormat    = "%-3s | %-14s | %-16s | %-8s | %-" + seatsColWidth + "s | %15s | %-12s | %-11s%n";

            int totalLineWidth = 80 + seatsColWidth;
            StringBuilder sepBuilder = new StringBuilder();
            for (int i = 0; i < totalLineWidth; i++) {
                sepBuilder.append("-");
            }
            String separator = sepBuilder.toString();

            System.out.println(separator);
            System.out.printf(headerFormat, "#", "Transaction ID", "Booking Date", "Match", "Seats", "Paid Amount", "Method", "Status");
            System.out.println(separator);

            int idx = 1;
            double totalSuccessfulPaid = 0.0;
            int confirmedCount = 0;
            int pendingCount = 0;
            int refundedCount = 0;

            for (Transaction txn : transactions) {
                String seatsStr = txn.getSeatIds() != null ? String.join(", ", txn.getSeatIds()) : "";
                String dateStr = txn.getTransactionDate() != null ? txn.getTransactionDate().format(dtf) : "N/A";
                String amountStr = String.format("%,.0f VND", txn.getTotalAmount());
                String methodStr = txn.getPaymentMethod() != null ? txn.getPaymentMethod().name() : "N/A";
                String statusStr = txn.getStatus() != null ? txn.getStatus().name() : "N/A";

                System.out.printf(rowFormat,
                        String.valueOf(idx++),
                        txn.getTransactionId(),
                        dateStr,
                        txn.getMatchId(),
                        seatsStr,
                        amountStr,
                        methodStr,
                        statusStr);

                if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                    totalSuccessfulPaid += txn.getTotalAmount();
                    confirmedCount++;
                } else if (txn.getStatus() == TransactionStatus.PENDING) {
                    pendingCount++;
                } else if (txn.getStatus() == TransactionStatus.REFUNDED) {
                    refundedCount++;
                }
            }

            System.out.println(separator);
            System.out.printf("Summary: %d transaction(s) | Total Successful Paid: %,.0f VND%n",
                    transactions.size(), totalSuccessfulPaid);
            System.out.printf("Status Breakdown: %d CONFIRMED, %d PENDING, %d REFUNDED%n",
                    confirmedCount, pendingCount, refundedCount);
            System.out.println(separator);

            System.out.println("Actions: [Enter] Back to menu | Type 'sort' to change order | Type [Txn ID] or [#] to view details");
            System.out.print("Your choice: ");
            String action = readLine();
            if (action == null || action.trim().isEmpty()) {
                return;
            }
            String actTrim = action.trim();
            if (actTrim.equalsIgnoreCase("sort")) {
                System.out.println("\n--- CHOOSE SORT ORDER ---");
                System.out.println("1. Newest booking first (Default)");
                System.out.println("2. Oldest booking first");
                System.out.println("3. Highest amount first");
                System.out.println("4. Lowest amount first");
                System.out.println("5. Priority status (PENDING -> CONFIRMED -> REFUNDED)");
                System.out.print("Choose option (1-5, or [Enter] to cancel): ");
                String sortOpt = readLine();
                if (sortOpt != null && !sortOpt.trim().isEmpty()) {
                    try {
                        int opt = Integer.parseInt(sortOpt.trim());
                        if (opt >= 1 && opt <= 5) {
                            currentSort = opt;
                        } else {
                            System.out.println("Invalid option. Retaining current sort order.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number. Retaining current sort order.");
                    }
                }
            } else {
                Transaction found = null;
                try {
                    int num = Integer.parseInt(actTrim);
                    if (num >= 1 && num <= transactions.size()) {
                        found = transactions.get(num - 1);
                    }
                } catch (NumberFormatException ignored) {}

                if (found == null) {
                    for (Transaction txn : transactions) {
                        if (txn.getTransactionId() != null &&
                                txn.getTransactionId().equalsIgnoreCase(actTrim)) {
                            found = txn;
                            break;
                        }
                    }
                }

                if (found != null) {
                    showTransactionDetails(found);
                } else {
                    System.out.println("Transaction '" + actTrim + "' not found. Press [Enter] to continue...");
                    readLine();
                }
            }
        }
    }

    public void sortTransactions(List<Transaction> transactions, int sortType) {
        switch (sortType) {
            case 2:
                transactions.sort((a, b) -> {
                    if (a.getTransactionDate() == null && b.getTransactionDate() == null) return 0;
                    if (a.getTransactionDate() == null) return 1;
                    if (b.getTransactionDate() == null) return -1;
                    return a.getTransactionDate().compareTo(b.getTransactionDate());
                });
                break;
            case 3:
                transactions.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));
                break;
            case 4:
                transactions.sort((a, b) -> Double.compare(a.getTotalAmount(), b.getTotalAmount()));
                break;
            case 5:
                transactions.sort((a, b) -> {
                    int pA = getStatusPriority(a.getStatus());
                    int pB = getStatusPriority(b.getStatus());
                    if (pA != pB) return Integer.compare(pA, pB);
                    if (a.getTransactionDate() == null || b.getTransactionDate() == null) return 0;
                    return b.getTransactionDate().compareTo(a.getTransactionDate());
                });
                break;
            case 1:
            default:
                transactions.sort((a, b) -> {
                    if (a.getTransactionDate() == null && b.getTransactionDate() == null) return 0;
                    if (a.getTransactionDate() == null) return 1;
                    if (b.getTransactionDate() == null) return -1;
                    return b.getTransactionDate().compareTo(a.getTransactionDate());
                });
                break;
        }
    }

    public String getSortName(int sortType) {
        switch (sortType) {
            case 2: return "Oldest booking first";
            case 3: return "Highest amount first";
            case 4: return "Lowest amount first";
            case 5: return "Status priority (PENDING -> CONFIRMED -> REFUNDED)";
            case 1:
            default: return "Newest booking first";
        }
    }

    public int getStatusPriority(TransactionStatus status) {
        if (status == null) return 99;
        switch (status) {
            case PENDING: return 1;
            case CONFIRMED:
            case SUCCESS: return 2;
            case REFUNDED: return 3;
            case CANCELLED: return 4;
            default: return 5;
        }
    }

    public void showTransactionDetails(Transaction txn) {
        System.out.println("\n==================================================");
        System.out.println("             BOOKING RECEIPT DETAILS              ");
        System.out.println("==================================================");
        System.out.println("Transaction ID: " + txn.getTransactionId());
        String dateFormatted = txn.getTransactionDate() != null
                ? txn.getTransactionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : "N/A";
        System.out.println("Booking Date:   " + dateFormatted);
        System.out.println("Fan ID:         " + txn.getFanId());
        System.out.println("Match ID:       " + txn.getMatchId());
        List<String> seats = txn.getSeatIds() != null ? txn.getSeatIds() : new ArrayList<>();
        System.out.println("Seats Booked:   " + (seats.isEmpty() ? "None" : String.join(", ", seats))
                + " (" + seats.size() + " seat" + (seats.size() > 1 ? "s" : "") + ")");
        System.out.println("Payment Method: " + (txn.getPaymentMethod() != null ? txn.getPaymentMethod().name() : "N/A"));
        if (txn.getVoucherCode() != null && !txn.getVoucherCode().trim().isEmpty()) {
            System.out.println("Voucher Used:   " + txn.getVoucherCode() + " (-" + String.format("%,.0f VND", txn.getDiscountAmount()) + ")");
        }
        System.out.println("Paid Amount:    " + String.format("%,.0f VND", txn.getTotalAmount()));
        if (txn.getRefundedAmount() > 0) {
            System.out.println("Refunded:       " + String.format("%,.0f VND", txn.getRefundedAmount()));
        }
        System.out.println("Status:         " + (txn.getStatus() != null ? txn.getStatus().name() : "N/A"));
        System.out.println("==================================================");
        System.out.print("Press [Enter] to return to history list...");
        readLine();
    }

    public void refundTicket(String fanId, TransactionController transactionController) {
        System.out.println("\n=== TICKET REFUND POLICY ===");
        System.out.println("- Each eligible ticket is refunded at 50% of its paid price.");
        System.out.println("- Any voucher discount is allocated proportionally before calculating the 50% refund.");
        System.out.println("- Refund the ticket at least 12 hours before the match starts.");
        System.out.println("- The deadline is the scheduled kick-off time minus 12 hours.");
        System.out.println("- Requests after the deadline, including after kick-off, are rejected.");
        System.out.println("- Eligible refunds are processed immediately; staff approval is not required.");
        try {
            List<TransactionController.RefundableTicket> refundableTickets =
                    transactionController.getRefundableTicketsByFan(fanId);
            if (refundableTickets.isEmpty()) {
                System.out.println("You have no tickets currently eligible for a refund.");
                return;
            }

            System.out.printf("%-14s | %-10s | %-14s | %-14s | %-16s | %-14s%n",
                    "TICKET ID", "MATCH", "SEAT", "TICKET PRICE", "VOUCHER SHARE", "REFUND 50%");
            System.out.println("----------------------------------------------------------------------------------------------");
            for (TransactionController.RefundableTicket refundable : refundableTickets) {
                Ticket ticket = refundable.getTicket();
                System.out.printf("%-14s | %-10s | %-14s | %,.0f VND | %,.0f VND | %,.0f VND%n",
                        ticket.getTicketId(), ticket.getMatchId(), ticket.getSeatId(), ticket.getPrice(),
                        refundable.getVoucherDiscount(), refundable.getRefundAmount());
            }

            System.out.print("Enter Ticket ID to refund (or 0 to cancel): ");
            String ticketId = readLine();
            if (ticketId == null || ticketId.trim().isEmpty() || "0".equals(ticketId.trim())) {
                return;
            }
            TransactionController.RefundResult result =
                    transactionController.refundTicket(fanId, ticketId.trim());
            if (result.isSuccessful()) {
                System.out.println("Ticket " + ticketId.trim() + " has been refunded.");
                System.out.println(result.getMessage());
                System.out.println("Refund amount: "
                        + String.format("%,.0f VND", result.getRefundAmount()));
            } else {
                System.out.println("Refund could not be processed: " + result.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Unable to load or process refund: " + e.getMessage());
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