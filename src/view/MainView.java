package view;

import java.util.Scanner;
import com.view.SimulatorView;
public class MainView {
    private Scanner scanner;
    private BookingView bookingView;

    public MainView(BookingView bookingView) {
        this(bookingView, new Scanner(System.in));
    }

    public MainView(BookingView bookingView, Scanner scanner) {
        this.scanner = scanner;
        this.bookingView = bookingView;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("==========================================================================");
            System.out.println("              STADIUM TICKET BOOKING SYSTEM - SIMULATION                  ");
            System.out.println("==========================================================================");
            System.out.println("1. View Match Schedule & Filter (Date, Stadium, Team, Sold Out)");
            System.out.println("2. View Stands, Pricing Table & Visual Seat Map");
            System.out.println("3. Book Tickets Online (With Voucher & QR Code Payment)");
            System.out.println("4. Run Simulation Tool");
            System.out.println("0. Exit Program");
            System.out.print("Select an option (0-4): ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    bookingView.showMatchListAndFilter();
                    break;
                case 2:
                    bookingView.showStandPriceAndSeatMap();
                    break;
                case 3:
                    bookingView.showBookingForm();
                    break;
                case 4:
                    SimulatorView simulatorView =
            new SimulatorView(scanner, new com.controller.SimulatorController());
    simulatorView.showSimulator();
    break;
                case 0:
                    System.out.println("\nThank you for using the Stadium Ticket Booking System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }

    public int getUserChoice() {
        try {
            if (!scanner.hasNextLine()) {
                return 0;
            }
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
