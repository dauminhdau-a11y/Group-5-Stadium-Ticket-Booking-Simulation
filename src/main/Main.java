package main;

import controller.BookingController;
import model.SyncMechanism;
import repository.SeatsRepository;
import repository.TransactionRepository;
import view.BookingView;
import view.MainView;

public class Main {
    public static void main(String[] args) {
        TransactionRepository transRepo = new TransactionRepository();
        SeatsRepository seatRepo = new SeatsRepository();
        
        BookingController bookingController = new BookingController( SyncMechanism.NO_LOCK,
                seatRepo,
                transRepo);
        BookingView bookingView = new BookingView(bookingController);
        MainView mainView = new MainView(bookingView);

        mainView.displayMenu();
    }
}
