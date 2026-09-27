package main;

import controller.BookingController;
import model.SyncMechanism;
import repository.TransactionRepository;
import view.BookingView;
import view.MainView;

public class Main {
    public static void main(String[] args) {
        TransactionRepository transRepo = new TransactionRepository();

        BookingController bookingController = new BookingController(SyncMechanism.SYNCHRONIZED, transRepo.getSeatRepository(), transRepo.getTransactionRepository(), transRepo.getBookingRepository());
        BookingView bookingView = new BookingView(bookingController);
        MainView mainView = new MainView(bookingView);

        mainView.displayMenu();
    }
}
