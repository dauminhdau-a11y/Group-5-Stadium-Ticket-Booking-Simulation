package view;

import java.util.Scanner;

public class MainView {
    private Scanner scanner;
    private BookingView bookingView;

    public MainView(BookingView bookingView) {
        this.scanner = new Scanner(System.in);
        this.bookingView = bookingView;
    }
    public void displayMenu(){
        while(true){
            System.out.println("=====================================");
            System.out.println("    STADIUM TICKET BOOKING SYSTEM    ");
            System.out.println("=====================================");
            System.out.println("1. View available seats");
            System.out.println("2. Book tickets online");
            System.out.println("3. Run simulation tool");
            System.out.println("0. Exit program");
            System.out.print("Select an option (0-3): ");
            
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.println("Feature under development...");
                    break;
                case 2:
                    bookingView.showBookingForm();
                    break;
                case 3:
                    System.out.println("Feature under development...");
                    break;
                case 0:
                    System.out.println("Thank you for using the system. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
