package main;

import controller.BookingController;
import model.SyncMechanism;
import repository.SeatRepository;
import repository.TransactionRepository;
import view.BookingView;
import view.MainView;

public class Main {
    public static void main(String[] args) {
        TransactionRepository transRepo = new TransactionRepository();
        SeatRepository seatRepo = new SeatRepository();

        BookingController bookingController = new BookingController(SyncMechanism.NO_LOCK,
                seatRepo,
                transRepo);
        BookingView bookingView = new BookingView(bookingController);
        MainView mainView = new MainView(bookingView);

        mainView.displayMenu();
    }
}
