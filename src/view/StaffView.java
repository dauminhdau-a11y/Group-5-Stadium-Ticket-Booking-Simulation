package view;

import controller.StaffController;
import model.Seat;
import model.Staff;
import model.SupportRequest;
import model.Transaction;
import model.enums.PaymentMethod;
import model.enums.SupportStatus;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class StaffView {
    private StaffController staffController;
    private Scanner scanner;
    private SeatMapView seatMapView;

    public StaffView(StaffController staffController, Scanner scanner) {
        if (staffController == null || scanner == null) {
            throw new IllegalArgumentException("Staff controller and scanner must not be null.");
        }
        this.staffController = staffController;
        this.scanner = scanner;
        this.seatMapView = new SeatMapView();
    }

    public StaffController getStaffController() {
        return staffController;
    }

    public void setStaffController(StaffController staffController) {
        this.staffController = staffController;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(Staff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("Welcome, " + staff.getUsername() + "!");
            System.out.println("1. View ticket fee list");
            System.out.println("2. Sell tickets offline");
            System.out.println("3. Approve booking payment");
            System.out.println("4. View seat map");
            System.out.println("5. Shift report");
            System.out.println("6. Respond to fan support requests");
            System.out.println("7. Logout");
            System.out.print("Choose (1-7): ");

            String input = readLine();
            if (input == null) {
                return;
            }
            String choice = input.trim();
            try {
                switch (choice) {
                    case "1":
                        viewTicketFeeList();
                        break;
                    case "2":
                        sellTicketsOffline();
                        break;
                    case "3":
                        confirmReservationPayment();
                        break;
                    case "4":
                        viewRealTimeSeatingChart();
                        break;
                    case "5":
                        staffReport();
                        break;
                    case "6":
                        handleSupportRequests();
                        break;
                    case "7":
                        loggedIn = false;
                        System.out.println("Staff logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose 1-7.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void viewTicketFeeList() throws IOException {
        System.out.print("Enter Match ID to view fee list: ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            System.out.println("[ERROR]: Match ID cannot be empty. Please enter a valid Match ID.");
            return;
        }
        matchId = matchId.trim();
        List<Seat> seats = staffController.getTicketFeeList(matchId);
        if (seats.isEmpty()) {
            System.out.println("No seats / ticket fees found for Match ID: " + matchId);
            return;
        }

        int pageSize = 50;
        int total = seats.size();
        int current = 0;

        System.out.println("\n-------------------------------------------------------------");
        System.out.printf("%-12s | %-10s | %-14s | %-14s | %-10s%n",
                "Seat ID", "Match ID", "Stand", "Price", "Status");
        System.out.println("-------------------------------------------------------------");

        while (current < total) {
            int end = Math.min(current + pageSize, total);
            for (int i = current; i < end; i++) {
                Seat s = seats.get(i);
                System.out.printf("%-12s | %-10s | %-14s | %-14s | %-10s%n",
                        s.getSeatId(),
                        s.getMatchId(),
                        s.getStand() != null ? s.getStand().name() : "N/A",
                        String.format("%,.0f VND", staffController.getTicketPrice(s)),
                        s.getStatus());
            }
            current = end;
            System.out.println("-------------------------------------------------------------");
            System.out.println("Showing " + current + "/" + total + " records.");

            if (current < total) {
                System.out.print("Do you want to view more records? (y/n, or 'all' to show all remaining): ");
                String choice = readLine();
                if (choice == null || choice.trim().isEmpty()
                        || choice.trim().equalsIgnoreCase("n")
                        || choice.trim().equalsIgnoreCase("no")) {
                    break;
                } else if (choice.trim().equalsIgnoreCase("all") || choice.trim().equalsIgnoreCase("a")) {
                    pageSize = total - current;
                } else if (!choice.trim().equalsIgnoreCase("y") && !choice.trim().equalsIgnoreCase("yes")) {
                    System.out.println("Invalid choice. Stopping view.");
                    break;
                }
                System.out.println("-------------------------------------------------------------");
            }
        }
    }

    public void sellTicketsOffline() throws IOException {
        System.out.println("\n--- SELL TICKETS OFFLINE (COUNTER) ---");
        System.out.print("Customer Name / Phone: ");
        String customer = readLine();
        if (customer == null || customer.trim().isEmpty()) {
            System.out.println("Customer identifier is required.");
            return;
        }
        System.out.print("Match ID: ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            return;
        }
        System.out.print("Seat IDs (comma-separated, e.g. SEAT_00001, SEAT_00002): ");
        String seatInput = readLine();
        if (seatInput == null || seatInput.trim().isEmpty()) {
            return;
        }
        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));
        System.out.println("Payment Method: 1. CASH   2. CREDIT_CARD");
        System.out.print("Choose payment method (1 or 2): ");
        String pChoice = readLine();
        PaymentMethod method = "2".equals(pChoice) ? PaymentMethod.CREDIT_CARD : PaymentMethod.CASH;

        try {
            Transaction txn = staffController.sellTicketsOffline(customer.trim(), matchId.trim(), seatIds, method);
            System.out.println("\n>> Offline Ticket Sale SUCCESS!");
            System.out.println("Transaction ID: " + txn.getTransactionId());
            System.out.println("Seats:          " + String.join(", ", txn.getSeatIds()));
            System.out.println("Total Amount:   " + String.format("%,.0f VND", txn.getTotalAmount()));
            System.out.println("Payment Method: " + txn.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    public void confirmReservationPayment() throws IOException {
        System.out.println("\n=== APPROVE BOOKING PAYMENT ===");
        List<Transaction> pending = staffController.getPendingApprovalTransactions();
        if (pending.isEmpty()) {
            System.out.println("There are no pending booking transactions awaiting confirmation.");
            return;
        }

        int maxSeatsLen = 16;
        for (Transaction txn : pending) {
            if (txn.getSeatIds() != null) {
                String s = String.join(", ", txn.getSeatIds());
                if (s.length() > maxSeatsLen) {
                    maxSeatsLen = s.length();
                }
            }
        }
        int seatsColWidth = Math.max(16, maxSeatsLen);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String headerFmt = "%-3s | %-14s | %-16s | %-10s | %-8s | %-" + seatsColWidth + "s | %15s | %-10s%n";
        String rowFmt = "%-3s | %-14s | %-16s | %-10s | %-8s | %-" + seatsColWidth + "s | %15s | %-10s%n";

        int totalLineWidth = 78 + seatsColWidth;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalLineWidth; i++) {
            sb.append("-");
        }
        String sep = sb.toString();

        System.out.println("Total pending transactions: " + pending.size());
        System.out.println(sep);
        System.out.printf(headerFmt, "#", "Transaction ID", "Booking Date", "Fan ID", "Match", "Seats", "Paid Amount",
                "Method");
        System.out.println(sep);

        int idx = 1;
        for (Transaction txn : pending) {
            String dateStr = txn.getTransactionDate() != null ? txn.getTransactionDate().format(dtf) : "N/A";
            String seatsStr = txn.getSeatIds() != null ? String.join(", ", txn.getSeatIds()) : "";
            String amountStr = String.format("%,.0f VND", txn.getTotalAmount());
            String methodStr = txn.getPaymentMethod() != null ? txn.getPaymentMethod().name() : "N/A";

            System.out.printf(rowFmt,
                    String.valueOf(idx++),
                    txn.getTransactionId(),
                    dateStr,
                    txn.getFanId(),
                    txn.getMatchId(),
                    seatsStr,
                    amountStr,
                    methodStr);
        }
        System.out.println(sep);

        System.out.print("Enter Transaction ID or [#] to confirm (or 'all' for all, '0' to cancel): ");
        String input = readLine();
        if (input == null || input.trim().isEmpty() || "0".equals(input.trim())) {
            System.out.println("Approval cancelled.");
            return;
        }

        String inputTrim = input.trim();
        if ("all".equalsIgnoreCase(inputTrim)) {
            int successCount = 0;
            for (Transaction txn : pending) {
                if (staffController.confirmReservationPayment(txn.getTransactionId())) {
                    successCount++;
                }
            }
            System.out.println(
                    ">> [SUCCESS]: Approved " + successCount + " / " + pending.size() + " pending transaction(s).");
            return;
        }

        String targetTxnId = inputTrim;
        try {
            int num = Integer.parseInt(inputTrim);
            if (num >= 1 && num <= pending.size()) {
                targetTxnId = pending.get(num - 1).getTransactionId();
            }
        } catch (NumberFormatException ignored) {
        }

        boolean ok = staffController.confirmReservationPayment(targetTxnId);
        if (ok) {
            System.out.println(">> [SUCCESS]: Reservation payment for transaction " + targetTxnId + " confirmed.");
            System.out.println(">> Tickets have been issued (Status: SOLD) and notification sent to Fan.");
        } else {
            System.out.println(
                    ">> [ERROR]: Transaction '" + targetTxnId + "' not found or not awaiting staff confirmation.");
        }
    }

    public void viewRealTimeSeatingChart() throws IOException {
        System.out.print("Enter Match ID to inspect seating chart: ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            return;
        }
        List<Seat> seats = staffController.getRealTimeSeatingChart(matchId.trim());
        seatMapView.displaySeatMap(matchId.trim(), seats, false);

        System.out.print("Do you want to view all seats in full detail (without truncation)? (y/n, or 'all'): ");
        String opt = readLine();
        if (opt != null && (opt.trim().equalsIgnoreCase("y") || opt.trim().equalsIgnoreCase("yes")
                || opt.trim().equalsIgnoreCase("all"))) {
            seatMapView.displaySeatMap(matchId.trim(), seats, true);
        }
    }

    public void staffReport() throws IOException {
        System.out.println();
        System.out.println(staffController.generateStaffReport());
    }

    public void handleSupportRequests() throws IOException {
        System.out.println("\n=== FAN SUPPORT REQUESTS ===");
        List<SupportRequest> requests = staffController.getAllSupportRequests();
        if (requests.isEmpty()) {
            System.out.println("No support requests found.");
            return;
        }

        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-14s | %-10s | %-10s | %-19s | %-35s%n",
                "REQUEST ID", "FAN ID", "STATUS", "CREATED AT", "SUBJECT");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        for (SupportRequest r : requests) {
            String dateStr = r.getCreatedAt() != null
                    ? r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : "N/A";
            String subj = r.getSubject() != null ? r.getSubject() : "";
            if (subj.length() > 35) {
                subj = subj.substring(0, 32) + "...";
            }
            System.out.printf("%-14s | %-10s | %-10s | %-19s | %-35s%n",
                    r.getRequestId(),
                    r.getFanId(),
                    r.getStatus(),
                    dateStr,
                    subj);
        }
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");

        System.out.print("Enter Request ID to view & respond (or press Enter to back): ");
        String reqId = readLine();
        if (reqId == null || reqId.trim().isEmpty()) {
            return;
        }

        SupportRequest target = staffController.getSupportRequestById(reqId.trim());
        if (target == null) {
            System.out.println("[ERROR]: Support request '" + reqId.trim() + "' not found.");
            return;
        }

        System.out.println("\n--- Support Request Details ---");
        System.out.println("Request ID: " + target.getRequestId());
        System.out.println("Fan ID:     " + target.getFanId());
        System.out.println("Created At: " + (target.getCreatedAt() != null
                ? target.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "N/A"));
        System.out.println("Status:     " + target.getStatus());
        System.out.println("Subject:    " + target.getSubject());
        System.out.println("Content:    " + target.getContent());
        if (target.getResponse() != null && !target.getResponse().isEmpty()) {
            System.out.println("Previous Response: " + target.getResponse());
        }
        System.out.println("--------------------------------");

        System.out.print("Enter your response message (or press Enter to cancel): ");
        String responseMsg = readLine();
        if (responseMsg == null || responseMsg.trim().isEmpty()) {
            System.out.println("Response cancelled.");
            return;
        }

        System.out.print("Update status [1: RESOLVED (default), 2: CLOSED, 3: PENDING]: ");
        String statusChoice = readLine();
        SupportStatus newStatus = SupportStatus.RESOLVED;
        if (statusChoice != null) {
            String sc = statusChoice.trim();
            if ("2".equals(sc) || "CLOSED".equalsIgnoreCase(sc)) {
                newStatus = SupportStatus.CLOSED;
            } else if ("3".equals(sc) || "PENDING".equalsIgnoreCase(sc)) {
                newStatus = SupportStatus.PENDING;
            }
        }

        boolean ok = staffController.respondToSupportRequest(target.getRequestId(), responseMsg.trim(), newStatus);
        if (ok) {
            System.out.println(">> Response submitted successfully! Request marked as " + newStatus + ".");
        } else {
            System.out.println("[ERROR]: Unable to save response.");
        }
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}