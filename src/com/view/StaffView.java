package com.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.controller.StaffController;
import com.model.BookingTransaction;
import com.model.Match;
import com.model.MatchSeat;
import com.model.Payment;
import com.model.SeatInspectionDetails;
import com.model.ShiftReport;
import com.model.SupportRequest;
import com.model.Ticket;
import com.model.User;
import com.model.enums.PaymentMethod;
import com.model.enums.SeatZone;
import com.model.enums.SupportRequestStatus;

public class StaffView {
    private final StaffController staffController;
    private final Scanner scanner;
    private final SeatMapView seatMapView = new SeatMapView();

    public StaffView(StaffController staffController) {
        this(staffController, new Scanner(System.in));
    }

    public StaffView(StaffController staffController, Scanner scanner) {
        this.staffController = staffController;
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
    }

    public void showLoginScreen() {
        System.out.println("\n=== STAFF LOGIN ===");
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();

        if (staffController.login(username, password)) {
            System.out.println("Login successful. Welcome, " + staffController.getCurrentUser().getUsername());
            showMenu();
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    public void showMenu() {
        while (staffController.isLoggedIn()) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("1. Sell tickets offline");
            System.out.println("2. Confirm payment / refund");
            System.out.println("3. View ticket buyer list");
            System.out.println("4. Shift report");
            System.out.println("5. Support contact");
            System.out.println("6. Create fan account");
            System.out.println("7. Logout");
            System.out.println("8. View real-time seating chart");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice = readInt();
            if (choice == 0) {
                staffController.logout();
                System.out.println("Bye.");
                return;
            }

            switch (choice) {
                case 1:
                    showSellOfflineForm();
                    break;
                case 2:
                    showConfirmForm();
                    break;
                case 3:
                    showBuyerList();
                    break;
                case 4:
                    showShiftReport();
                    break;
                case 5:
                    showSupportForm();
                    break;
                case 6:
                    showCreateFanForm();
                    break;
                case 7:
                    logout();
                    return;
                case 8:
                    showSeatMap();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void render() {
        staffController.showDashboard();
    }

    public void logout() {
        staffController.logout();
        System.out.println("Staff logged out successfully.");
    }

    public void showSellOfflineForm() {
        System.out
                .println("Ticket prices: VIP Area 500.000 | Nearby Area 350.000 | Two wings 250.000 | The Outlying Area 150.000");
        String matchId = chooseMatchId();
        if (matchId == null) {
            return;
        }
        Map<String, Integer> availableCounts = staffController.getAvailableSeatCountsBySection(matchId);
        System.out.println("Available sections:");
        int sectionNumber = 1;
        for (SeatZone zone : SeatZone.values()) {
            System.out.printf("%d. %-16s %,.0f VND (%d seats)%n", sectionNumber++, zone.getDisplayName(),
                    zone.getPrice(), availableCounts.get(zone.getSectionId()));
        }
        System.out.print("Select section (0 to cancel): ");
        int sectionChoice = readInt();
        if (sectionChoice == 0) {
            return;
        }
        if (sectionChoice < 1 || sectionChoice > SeatZone.values().length) {
            System.out.println("Invalid section selection.");
            return;
        }
        SeatZone selectedZone = SeatZone.values()[sectionChoice - 1];
        List<MatchSeat> availableSeats = staffController.getAvailableSeats(matchId, selectedZone.getSectionId());
        if (availableSeats.isEmpty()) {
            System.out.println("This section is sold out for the selected match.");
            return;
        }
        System.out.println("Available seats in " + selectedZone.getDisplayName() + ":");
        seatMapView.renderMatchSeats(availableSeats);
        System.out.print("Seat IDs, comma separated (0 to cancel): ");
        String seatInput = readLine();
        if ("0".equals(seatInput.trim())) {
            return;
        }
        List<String> seatIds = new ArrayList<String>();
        if (seatInput != null && !seatInput.trim().isEmpty()) {
            String[] parts = seatInput.split(",");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    seatIds.add(part.trim());
                }
            }
        }

        System.out.print("Buyer name: ");
        String buyerName = readLine();
        System.out.print("Buyer phone/email: ");
        String buyerContact = readLine();

        List<String> voucherSummaries = staffController.getAvailableVoucherSummaries();
        if (voucherSummaries.isEmpty()) {
            System.out.println("No unused voucher is available.");
        } else {
            System.out.println("Available vouchers:");
            for (String summary : voucherSummaries) {
                System.out.println("  " + summary);
            }
        }
        System.out.print("Voucher code (empty = none): ");
        String voucherCode = readLine();

        try {
            double amountDue = staffController.calculateOfflineSaleTotal(matchId, seatIds, voucherCode, buyerContact);
            System.out.printf("Amount due: %,.0f%n", amountDue);
            PaymentMethod paymentMethod = choosePaymentMethod();
            if (paymentMethod == null) {
                return;
            }
            double amountReceived;
            String paymentReference = "";
            if (paymentMethod == PaymentMethod.CASH) {
                amountReceived = readAmount("Cash received: ");
            } else {
                System.out
                        .println("Complete payment on the external terminal/bank app, then enter its real reference.");
                System.out.print("Payment reference: ");
                paymentReference = readLine();
                amountReceived = amountDue;
            }
            String staffId = staffController.getCurrentUser().getUserId();
            BookingTransaction tx = staffController.sellTicketOffline(staffId, matchId, seatIds,
                    voucherCode, buyerName, buyerContact, paymentMethod.name(), amountReceived, paymentReference);
            printReceipt(tx);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private PaymentMethod choosePaymentMethod() {
        System.out.println("Payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. Bank transfer");
        System.out.println("0. Cancel sale");
        System.out.print("Choose: ");
        switch (readInt()) {
            case 1:
                return PaymentMethod.CASH;
            case 2:
                return PaymentMethod.CARD;
            case 3:
                return PaymentMethod.BANK_TRANSFER;
            default:
                System.out.println("Payment cancelled.");
                return null;
        }
    }

    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double amount = Double.parseDouble(readLine().trim());
                if (Double.isFinite(amount) && amount >= 0) {
                    return amount;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a valid non-negative amount.");
        }
    }

    private void printReceipt(BookingTransaction transaction) {
        Payment payment = staffController.getPaymentForTransaction(transaction.getTransactionId());
        List<Ticket> tickets = staffController.getTicketsForTransaction(transaction.getTransactionId());
        System.out.println("\n========== TICKET RECEIPT ==========");
        System.out.println("Transaction: " + transaction.getTransactionId());
        System.out.println("Match: " + transaction.getMatchId());
        System.out.println("Buyer: " + transaction.getBuyerName());
        System.out.println("Contact: " + transaction.getBuyerContact());
        System.out.println("Seat reservations / tickets:");
        for (Ticket ticket : tickets) {
            System.out.printf("  %-38s Seat %-12s %-9s %,.0f%n", ticket.getTicketId(), ticket.getSeatId(),
                    ticket.getStatus(), ticket.getPrice());
        }
        System.out.printf("Subtotal: %,.0f%n", transaction.getOriginalAmount());
        System.out.printf("Discount: %,.0f%n", transaction.getDiscountAmount());
        if (payment != null) {
            System.out.println("Payment method: " + payment.getMethod());
            System.out.println("Payment status: " + payment.getStatus());
            System.out.printf("Amount due: %,.0f%n", transaction.getFinalAmount());
            if (PaymentMethod.CASH.name().equals(payment.getMethod())) {
                System.out.printf("Cash received: %,.0f%n", payment.getAmountReceived());
                System.out.printf("Change: %,.0f%n", payment.getChangeAmount());
            } else {
                System.out.println("Payment reference: " + payment.getReference());
            }
            if ("PENDING".equals(payment.getStatus())) {
                System.out.println("Not a valid ticket yet. Staff must verify payment before confirmation.");
            } else if ("PAID".equals(payment.getStatus())) {
                System.out.printf("Total paid: %,.0f%n", transaction.getFinalAmount());
            }
        } else {
            System.out.printf("Amount due: %,.0f%n", transaction.getFinalAmount());
        }
        System.out.println("====================================");
    }

    public void showConfirmForm() {
        System.out.println("Pending payment requests:");
        printTransactionQueue(staffController.getPendingPayments());
        System.out.println("Refund requests:");
        printTransactionQueue(staffController.getRefundRequests());
        System.out.println("1. Confirm payment");
        System.out.println("2. Confirm refund");
        System.out.println("0. Back");
        System.out
                .println("Only confirm when the transaction status, external reference and payment amount all match.");
        System.out.print("Choose: ");
        int action = readInt();
        if (action == 0) {
            return;
        }
        System.out.print("Transaction id: ");
        String transactionId = readLine();

        try {
            if (action == 1) {
                staffController.confirmPayment(transactionId);
                System.out.println("Payment confirmed.");
            } else if (action == 2) {
                staffController.confirmRefund(transactionId);
                System.out.println("Refund confirmed.");
            } else {
                System.out.println("Invalid action.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void showRefundForm() {
        showConfirmForm();
    }

    public void showBuyerList() {
        String matchId = chooseMatchId();
        if (matchId == null) {
            return;
        }
        List<BookingTransaction> list = staffController.getTicketBuyerList(matchId);
        printBuyerList(list);
    }

    public void showSeatMap() {
        String matchId = chooseMatchId();
        if (matchId == null) {
            return;
        }
        Match match = staffController.getMatch(matchId);
        boolean inSeatMenu = true;
        while (inSeatMenu) {
            try {
                List<MatchSeat> seats = staffController.getSeatMap(matchId);
                seatMapView.renderDashboard(match, seats);

                System.out.println("\n=== SEAT MAP DASHBOARD ===");
                System.out.println("1. View whole stadium (Color-coded by row)");
                System.out.println("2. View by Stand (Stand Filter)");
                System.out.println("3. Search seat details (Seat Inspection)");
                System.out.println("4. Live Monitor Mode (Auto-refresh every 3s)");
                System.out.println("R. Refresh latest data immediately");
                System.out.println("0. Back to main menu");
                System.out.print("Select action: ");

                String input = readLine().trim();
                if ("0".equals(input)) {
                    inSeatMenu = false;
                } else if ("1".equals(input)) {
                    seatMapView.renderStadiumLayout(seats);
                    pressEnterToContinue();
                } else if ("2".equals(input)) {
                    showStandFilterView(seats);
                } else if ("3".equals(input)) {
                    showSeatInspectionView(matchId);
                } else if ("4".equals(input)) {
                    showLiveMonitor(matchId, match);
                } else if ("r".equalsIgnoreCase(input)) {
                    System.out.println("\n[INFO] Loading latest data from system...");
                } else {
                    System.out.println("Invalid option.");
                }
            } catch (Exception exception) {
                System.out.println("[ERROR] " + exception.getMessage());
                break;
            }
        }
    }

    private void showStandFilterView(List<MatchSeat> seats) {
        System.out.println("\n--- STAND FILTER (STAND FILTER) ---");
        SeatZone[] zones = SeatZone.values();
        for (int i = 0; i < zones.length; i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, zones[i].getDisplayName(), zones[i].getSectionId());
        }
        System.out.println("0. Back to main menu");
        System.out.print("Select Stand you want to view: ");
        int choice = readInt();
        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > zones.length) {
            System.out.println("Invalid option.");
            return;
        }
        SeatZone selectedZone = zones[choice - 1];
        seatMapView.renderStandLayout(seats, selectedZone.getSectionId());
        pressEnterToContinue();
    }

    private void showSeatInspectionView(String matchId) {
        System.out.print("\nEnter seat ID, MatchSeat ID, or seat number (e.g., S-1, S01, MS-1): ");
        String query = readLine().trim();
        if (query.isEmpty() || "0".equals(query)) {
            return;
        }
        try {
            SeatInspectionDetails details = staffController.inspectSeat(matchId, query);
            if (details == null) {
                System.out.println("\n[WARN] No seat found matching the keyword: '" + query + "'");
            } else {
                seatMapView.renderSeatInspection(details);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void showLiveMonitor(String matchId, Match match) {
        System.out.println("\n=======================================================");
        System.out.println("   LIVE MONITOR MODE (AUTO-REFRESH EVERY 3 SECONDS) ");
        System.out.println("=======================================================");
        System.out.println("The system will continuously update seat status and clear expired bookings.");
        System.out.println("Press Enter to stop Live Monitor and return to the menu.\n");

        int maxCycles = 60;
        for (int cycle = 1; cycle <= maxCycles; cycle++) {
            try {
                List<MatchSeat> seats = staffController.getSeatMap(matchId);
                seatMapView.renderDashboard(match, seats);
                System.out.printf(">> [Cycle %d/%d] Monitoring... (Press Enter to stop): %n", cycle, maxCycles);

                boolean stop = false;
                for (int i = 0; i < 15; i++) {
                    Thread.sleep(200);
                    if (System.in.available() > 0) {
                        stop = true;
                        break;
                    }
                }
                if (stop) {
                    readLine();
                    System.out.println("\n[INFO] Live Monitor stopped.");
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
                break;
            }
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        readLine();
    }

    private String chooseMatchId() {
        List<Match> matches = staffController.getMatches();
        if (matches.isEmpty()) {
            System.out.println("No matches are available.");
            return null;
        }
        System.out.printf("%-4s %-12s %s%n", "No.", "Match", "Start time");
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            System.out.printf("%-4d %-12s %s%n", i + 1, match.getMatchId(), match.getStartTime());
        }
        System.out.print("Select match number (0 to cancel): ");
        int choice = readInt();
        if (choice == 0) {
            return null;
        }
        if (choice < 1 || choice > matches.size()) {
            System.out.println("Invalid match selection.");
            return null;
        }
        return matches.get(choice - 1).getMatchId();
    }

    private void printTransactionQueue(List<BookingTransaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("  None");
            return;
        }
        for (BookingTransaction transaction : transactions) {
            Payment payment = staffController.getPaymentForTransaction(transaction.getTransactionId());
            String paymentDetails = payment == null ? "Payment missing"
                    : payment.getMethod() + " / ref " + payment.getReference();
            System.out.printf("  %-38s Buyer: %-22s Amount: %,.0f  %s%n", transaction.getTransactionId(),
                    transaction.getBuyerName(), transaction.getFinalAmount(), paymentDetails);
        }
    }

    public void printBuyerList(List<BookingTransaction> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No buyer found.");
            return;
        }
        System.out.printf("%-38s %-22s %-26s %-22s %s%n", "Transaction", "Buyer", "Phone / Email", "Date", "Amount");
        for (BookingTransaction tx : list) {
            String buyerName = tx.getBuyerName() == null || tx.getBuyerName().trim().isEmpty()
                    ? "Legacy buyer"
                    : tx.getBuyerName();
            String buyerContact = tx.getBuyerContact() == null ? "" : tx.getBuyerContact();
            System.out.printf("%-38s %-22s %-26s %-22s %,.0f%n", tx.getTransactionId(), buyerName,
                    buyerContact, tx.getBookingDate(), tx.getFinalAmount());
        }
    }

    public void showShiftReport() {
        System.out.print("From (yyyy-MM-ddTHH:mm): ");
        String fromStr = readLine();
        System.out.print("To (yyyy-MM-ddTHH:mm): ");
        String toStr = readLine();
        try {
            LocalDateTime from = LocalDateTime.parse(fromStr);
            LocalDateTime to = LocalDateTime.parse(toStr);
            String staffId = staffController.getCurrentUser().getUserId();
            ShiftReport report = staffController.getShiftReport(staffId, from, to);
            printShiftReport(report);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid date format. Use yyyy-MM-ddTHH:mm");
        }
    }

    public void printShiftReport(ShiftReport report) {
        if (report == null) {
            System.out.println("No report data.");
            return;
        }
        System.out.print(report.toFormattedString());
    }

    public void showSupportForm() {
        while (true) {
            System.out.println("\n=== CUSTOMER SUPPORT CENTER ===");
            System.out.println("1. View pending support requests");
            System.out.println("2. View all support requests");
            System.out.println("3. Reply / Resolve support request");
            System.out.println("4. Send direct message to Fan");
            System.out.println("5. Create support request (on behalf of Fan / Demo)");
            System.out.println("0. Back to Staff Menu");
            System.out.print("Choose: ");
            int subChoice = readInt();
            if (subChoice == 0) {
                return;
            }
            switch (subChoice) {
                case 1:
                    viewPendingSupportRequests();
                    break;
                case 2:
                    viewAllSupportRequests();
                    break;
                case 3:
                    replySupportRequest();
                    break;
                case 4:
                    sendDirectMessageToFan();
                    break;
                case 5:
                    createSupportRequestForm();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewPendingSupportRequests() {
        try {
            List<SupportRequest> list = staffController.getPendingSupportRequests();
            System.out.println("\n--- PENDING SUPPORT REQUESTS (" + list.size() + ") ---");
            if (list.isEmpty()) {
                System.out.println("No pending support requests.");
                return;
            }
            for (SupportRequest req : list) {
                System.out.print(req.toFormattedString());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void viewAllSupportRequests() {
        try {
            List<SupportRequest> list = staffController.getAllSupportRequests();
            System.out.println("\n--- ALL SUPPORT REQUESTS (" + list.size() + ") ---");
            if (list.isEmpty()) {
                System.out.println("No support requests found.");
                return;
            }
            for (SupportRequest req : list) {
                System.out.print(req.toFormattedString());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void replySupportRequest() {
        try {
            List<SupportRequest> pending = staffController.getPendingSupportRequests();
            if (pending.isEmpty()) {
                System.out.println("No pending support requests to resolve.");
                return;
            }
            System.out.println("\nAvailable pending requests:");
            for (SupportRequest req : pending) {
                System.out.println(" - [" + req.getRequestId() + "] Fan: " + req.getFanId() + " | Subject: " + req.getSubject());
            }
            System.out.print("Enter Request ID: ");
            String reqId = readLine();
            System.out.print("Decision (1: Resolve / Approve, 2: Reject): ");
            int decision = readInt();
            boolean approved = (decision == 1);
            System.out.print("Reply message to Fan: ");
            String replyMessage = readLine();

            SupportRequest resolved = staffController.resolveSupportRequest(reqId, replyMessage, approved);
            System.out.println("[SUCCESS] Request " + resolved.getRequestId() + " marked as " + resolved.getStatus() + ".");
            System.out.println("Notification sent to Fan: " + resolved.getFanId());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void sendDirectMessageToFan() {
        System.out.print("Fan id: ");
        String fanId = readLine();
        System.out.print("Message: ");
        String message = readLine();
        try {
            staffController.supportContact(fanId, message);
            System.out.println("Support message sent.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void createSupportRequestForm() {
        System.out.print("Fan id: ");
        String fanId = readLine();
        System.out.print("Subject: ");
        String subject = readLine();
        System.out.print("Message / Problem description: ");
        String message = readLine();
        try {
            SupportRequest req = staffController.createSupportRequest(fanId, subject, message);
            System.out.println("[SUCCESS] Support request created: " + req.getRequestId());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void showCreateFanForm() {
        System.out.println("\n=== CREATE FAN ACCOUNT (WALK-IN) ===");
        System.out.print("Full name: ");
        String fullName = readLine();
        System.out.print("Phone number (10 digits): ");
        String phone = readLine();
        System.out.print("Email (to receive e-tickets, optional): ");
        String email = readLine();
        System.out.print("Username (leave blank to auto-generate from phone number): ");
        String username = readLine();
        System.out.print("Initial password (leave blank to auto-generate a secure temporary password): ");
        String password = readLine();

        try {
            StaffController.FanRegistrationResult result = staffController.registerFanAtCounter(
                    fullName, phone, email, username, password);
            User user = result.getUser();

            System.out.println("\n============================================================");
            System.out.println("            FAN REGISTRATION (WALK-IN) SUCCESSFUL       ");
            System.out.println("============================================================");
            System.out.printf("  Fan ID                  : %s%n", user.getUserId());
            System.out.printf("  Full name               : %s%n", user.getFullName());
            System.out.printf("  Phone number            : %s%n", user.getPhoneNumber());
            System.out.printf("  Email                   : %s%n", user.getEmail().isEmpty() ? "(Not updated)" : user.getEmail());
            System.out.printf("  Username                : %s%n", user.getUsername());
            System.out.printf("  Initial password        : %s%n", result.getRawPassword());
            System.out.printf("  Welcome bonus points    : %d points%n", result.getInitialPoints());
            System.out.println("------------------------------------------------------------");
            System.out.println("  * Please change password on first login.");
            System.out.println("  * You can use Fan ID or Phone Number to earn points when purchasing tickets at the counter.");
            System.out.println("============================================================\n");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            String value = scanner.hasNextLine() ? scanner.nextLine() : "";
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }
}
