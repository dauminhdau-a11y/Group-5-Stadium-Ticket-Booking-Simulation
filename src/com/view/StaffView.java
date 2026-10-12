package com.view;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.controller.StaffController;
import com.model.BookingTransaction;
import com.model.MatchSeat;

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
        System.out.print("Match id: ");
        String matchId = readLine();
        System.out.print("Seats (comma separated, example: S-1,S-2): ");
        String seatInput = readLine();
        List<String> seatIds = new ArrayList<String>();
        if (seatInput != null && !seatInput.trim().isEmpty()) {
            String[] parts = seatInput.split(",");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    seatIds.add(part.trim());
                }
            }
        }

        System.out.print("Voucher code (empty = none): ");
        String voucherCode = readLine();

        try {
            String staffId = staffController.getCurrentUser().getUserId();
            BookingTransaction tx = staffController.sellTicketOffline(staffId, matchId, seatIds, voucherCode);
            System.out.println("Sold successfully. Transaction: " + tx.getTransactionId() + ", final amount: " + tx.getFinalAmount());
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void showConfirmForm() {
        System.out.println("Pending payment requests:");
        printTransactionQueue(staffController.getPendingPayments());
        System.out.println("Refund requests:");
        printTransactionQueue(staffController.getRefundRequests());
        System.out.println("1. Confirm payment");
        System.out.println("2. Confirm refund");
        System.out.println("0. Back");
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
        System.out.print("Match id: ");
        String matchId = readLine();
        List<BookingTransaction> list = staffController.getTicketBuyerList(matchId);
        printBuyerList(list);
    }

    public void showSeatMap() {
        System.out.print("Match id: ");
        String matchId = readLine();
        try {
            List<MatchSeat> seats = staffController.getSeatMap(matchId);
            seatMapView.renderMatchSeats(seats);
        } catch (Exception exception) {
            System.out.println("[ERROR] " + exception.getMessage());
        }
    }

    private void printTransactionQueue(List<BookingTransaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("  None");
            return;
        }
        for (BookingTransaction transaction : transactions) {
            System.out.printf("  %-12s Buyer: %-12s Amount: %,.0f%n", transaction.getTransactionId(),
                    transaction.getFanId(), transaction.getFinalAmount());
        }
    }

    public void printBuyerList(List<BookingTransaction> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No buyer found.");
            return;
        }
        System.out.printf("%-12s %-12s %-22s %s%n", "Transaction", "Fan", "Date", "Amount");
        for (BookingTransaction tx : list) {
            System.out.printf("%-12s %-12s %-22s %,.0f%n", tx.getTransactionId(), tx.getFanId(), tx.getBookingDate(), tx.getFinalAmount());
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
            Map<String, Double> report = staffController.getShiftReport(staffId, from, to);
            printShiftReport(report);
        } catch (Exception e) {
            System.out.println("[ERROR] Invalid date format. Use yyyy-MM-ddTHH:mm");
        }
    }

    public void printShiftReport(Map<String, Double> report) {
        if (report == null || report.isEmpty()) {
            System.out.println("No report data.");
            return;
        }
        System.out.println("Transactions: " + report.getOrDefault("transactions", 0.0).intValue());
        System.out.println("Revenue: " + String.format("%,.0f", report.getOrDefault("revenue", 0.0)));
    }

    public void showSupportForm() {
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

    public void showCreateFanForm() {
        System.out.print("Username: ");
        String username = readLine();
        System.out.print("Password: ");
        String password = readLine();
        System.out.print("Email: ");
        String email = readLine();
        try {
            com.model.User user = staffController.createFanAccount(username, password, email);
            System.out.println("Fan account created: " + user.getUserId());
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
