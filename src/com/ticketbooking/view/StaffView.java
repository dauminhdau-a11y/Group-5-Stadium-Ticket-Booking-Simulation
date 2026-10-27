package com.ticketbooking.view;

import com.ticketbooking.controller.StaffController;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Staff;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.enums.PaymentMethod;
import java.io.IOException;
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
            System.out.println("3. Confirm reservation payment");
            System.out.println("4. Confirm ticket refund");
            System.out.println("5. View real-time seating chart");
            System.out.println("6. Staff report");
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
                        confirmTicketRefund();
                        break;
                    case "5":
                        viewRealTimeSeatingChart();
                        break;
                    case "6":
                        staffReport();
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
        System.out.print("Enter Match ID to view fee list (or leave blank for all): ");
        String matchId = readLine();
        if (matchId != null && matchId.trim().isEmpty()) {
            matchId = null;
        }
        List<Seat> seats = staffController.getTicketFeeList(matchId);
        if (seats.isEmpty()) {
            System.out.println("No seats / ticket fees found.");
            return;
        }
        System.out.println("\n-------------------------------------------------------------");
        System.out.printf("%-12s | %-10s | %-14s | %-14s | %-10s%n",
                "Seat ID", "Match ID", "Type", "Price", "Status");
        System.out.println("-------------------------------------------------------------");
        int count = 0;
        for (Seat s : seats) {
            System.out.printf("%-12s | %-10s | %-14s | %-14s | %-10s%n",
                    s.getSeatId(),
                    s.getMatchId(),
                    s.getSeatType() != null ? s.getSeatType().name() : "STANDARD",
                    String.format("%,.0f VND", s.getPrice()),
                    s.getStatus());
            count++;
            if (count >= 50) {
                System.out.println("... (" + (seats.size() - count) + " more records)");
                break;
            }
        }
        System.out.println("-------------------------------------------------------------");
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
        System.out.print("Seat IDs (comma-separated, e.g. VVIP-1, VIP-2): ");
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
        System.out.print("Enter Transaction ID to confirm payment: ");
        String txnId = readLine();
        if (txnId == null || txnId.trim().isEmpty()) {
            return;
        }
        boolean ok = staffController.confirmReservationPayment(txnId.trim());
        if (ok) {
            System.out.println("Reservation payment for transaction " + txnId.trim() + " confirmed.");
        } else {
            System.out.println("Transaction ID not found.");
        }
    }

    public void confirmTicketRefund() throws IOException {
        System.out.print("Enter Ticket ID to confirm refund: ");
        String ticketId = readLine();
        if (ticketId == null || ticketId.trim().isEmpty()) {
            return;
        }
        boolean ok = staffController.confirmTicketRefund(ticketId.trim());
        if (ok) {
            System.out.println("Ticket " + ticketId.trim() + " refund confirmed and seat released.");
        } else {
            System.out.println("Ticket not found or already refunded.");
        }
    }

    public void viewRealTimeSeatingChart() throws IOException {
        System.out.print("Enter Match ID to inspect seating chart: ");
        String matchId = readLine();
        if (matchId == null || matchId.trim().isEmpty()) {
            return;
        }
        List<Seat> seats = staffController.getRealTimeSeatingChart(matchId.trim());
        seatMapView.displaySeatMap(matchId.trim(), seats);
    }

    public void staffReport() throws IOException {
        System.out.println();
        System.out.println(staffController.generateStaffReport());
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}