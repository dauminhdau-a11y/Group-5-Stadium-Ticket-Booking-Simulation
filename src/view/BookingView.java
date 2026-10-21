package view;

import controller.BookingController;
import model.BookingTransaction;
import model.Seat;

import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class BookingView {
    private BookingController bookingController;
    private Scanner scanner;

    public BookingView(BookingController bookingController) {
        this.bookingController = bookingController;
        this.scanner = new Scanner(System.in);
    }

    public void showBookingForm() {
        System.out.println("\n=== ONLINE TICKET BOOKING SYSTEM ===");
        System.out.print("Enter your Fan ID (e.g., F001): ");
        String fanId = scanner.nextLine().trim();

        System.out.print("Enter match ID (e.g., M001): ");
        String matchId = scanner.nextLine().trim();

        System.out.print("Enter seat IDs to book (separated by comma, e.g., S1, S2): ");
        String seatInput = scanner.nextLine().trim();

        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));

        try {
            System.out.println("Processing transaction...");
            BookingTransaction transaction = bookingController.bookSeats(fanId, matchId, seatIds);

            printBookingResult(transaction);
        } catch (Exception e) {
            System.out.println("BOOKING ERROR: " + e.getMessage());
        }
    }

    public void showAvailableSeats() {
        System.out.println("\n=== AVAILABLE SEATS LOOKUP ===");
        System.out.println("Enter match ID to view seats (e.g., M0001): ");
        String matchId = scanner.nextLine().trim();

        if (matchId.isEmpty()) {
            System.out.println("Match ID cannot be empty!");
            return;
        }
        List<Seat> availableSeats = bookingController.getAvailableSeats(matchId);

        if (availableSeats == null || availableSeats.isEmpty()) {
            System.out.println("No available seats found for match " + matchId);
        } else {
            System.out.println(
                    "\n=== AVAILABLE SEATS FOR MATCH " + matchId + " (" + availableSeats.size() + " seats) ---");
            System.out.printf("%-12s | %-12s | %-15s%n", "Seat ID", "Match ID", "Status");
            System.out.println("--------------------------------");
            for (Seat seat : availableSeats) {
                System.out.printf("%-12s | %-12s | %-15s%n", seat.getSeatId(), seat.getMatchId(), seat.getStatus());
            }
            System.out.println("--------------------------------\n");
        }
    }

    public void printBookingResult(BookingTransaction transaction) {
        System.out.println("\n=== SUCCESSFUL TRANSACTION RECEIPT ===");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Fan ID: " + transaction.getFanId());
        if (transaction.getSeatIds() != null && !transaction.getSeatIds().isEmpty()) {
            System.out.println("Booked Seats: " + String.join(",", transaction.getSeatIds()));
        }
        System.out.println("Total amount: " + String.format("%.0f", transaction.getTotalAmount()) + " VND");
        System.out.println("Status: " + transaction.getStatus());
        System.out.println("--------------------------------\n");
    }
}
