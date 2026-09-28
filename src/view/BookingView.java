package view;

import controller.BookingController;
import model.BookingTransaction;

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

    public void showBookingForm(){
        System.out.println("\n=== ONLINE TICKET BOOKING SYSTEM ===");
        System.out.print("Enter your Fan ID (e.g., F001): ");
        String fanId = scanner.nextLine().trim();

        System.out.print("Enter match ID (e.g., M001): ");
        String matchId = scanner.nextLine().trim();

        System.out.print("Enter seat IDs to book (separated by comma, e.g., S1, S2): ");
        String seatInput = scanner.nextLine().trim();

        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));

        try{
            System.out.println("Processing transaction...");
            BookingTransaction transaction = bookingController.bookSeats(fanId, matchId, seatIds);

            printBookingResult(transaction);
        }catch (Exception e){
            System.out.println("BOOKING ERROR: " + e.getMessage());
        }
    }
    public void printBookingResult(BookingTransaction transaction){
        System.out.println("\n=== SUCCESSFUL TRANSACTION RECEIPT ===");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Fan ID: " + transaction.getFanId());
        System.out.println("Total amount: " + String.format("%.0f", transaction.getTotalAmount()) + " VND");
        System.out.println("Status: " + transaction.getStatus());
        System.out.println("--------------------------------\n");
    }
}
