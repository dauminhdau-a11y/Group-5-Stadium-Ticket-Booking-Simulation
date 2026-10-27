package main;

import controller.BookingController;
import model.SyncMechanism;
import repository.MatchRepository;
import repository.SeatRepository;
import repository.TransactionRepository;
import repository.VoucherRepository;
import view.BookingView;
import view.MainView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TransactionRepository transRepo = new TransactionRepository();
        SeatRepository seatRepo = new SeatRepository();
        VoucherRepository voucherRepo = new VoucherRepository();
        MatchRepository matchRepo = new MatchRepository();

        BookingController bookingController = new BookingController(SyncMechanism.NO_LOCK,
                seatRepo,
                transRepo,
                voucherRepo,
                matchRepo);

        Scanner scanner = new Scanner(System.in);
        BookingView bookingView = new BookingView(bookingController, scanner);
        MainView mainView = new MainView(bookingView, scanner);

        mainView.displayMenu();
    }
}
