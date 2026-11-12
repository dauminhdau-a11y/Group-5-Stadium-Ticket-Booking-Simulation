package controller;

import model.enums.SyncMechanism;
import repository.AccountRepository;
import repository.FanRepository;
import repository.MatchRepository;
import repository.SeatRepository;
import repository.SectionRepository;
import repository.StadiumRepository;
import repository.SupportRequestRepository;
import repository.TicketRepository;
import repository.TransactionRepository;
import repository.VoucherRepository;
import view.AccountView;
import view.AuthView;
import java.util.Scanner;

public class ApplicationController {
    public void run() {
        Scanner scanner = new Scanner(System.in);

        FanRepository fanRepo = new FanRepository("data/fans.csv");
        AccountRepository staffRepo = new AccountRepository("data/staff.csv");
        AccountRepository adminRepo = new AccountRepository("data/admins.csv");
        StadiumRepository stadiumRepo = new StadiumRepository("data/stadiums.csv");
        SectionRepository sectionRepo = new SectionRepository("data/sections.csv");
        SeatRepository seatRepo = new SeatRepository("data/seats.csv", sectionRepo);
        MatchRepository matchRepo = new MatchRepository("data/matches.csv");
        TicketRepository ticketRepo = new TicketRepository("data/tickets.csv");
        VoucherRepository voucherRepo = new VoucherRepository("data/vouchers.csv");
        TransactionRepository transactionRepo = new TransactionRepository("data/transactions.csv");
        SupportRequestRepository supportRepo = new SupportRequestRepository("data/support_requests.csv");

        AuthController authController = new AuthController(fanRepo, staffRepo, adminRepo);
        AccountController accountController = new AccountController(
                staffRepo,
                adminRepo,
                fanRepo,
                new AccountView(scanner),
                authController);

        MatchController matchController = new MatchController(matchRepo);
        StadiumController stadiumController = new StadiumController(stadiumRepo);
        SectionController sectionController = new SectionController(sectionRepo);
        SeatController seatController = new SeatController(seatRepo);
        TicketController ticketController = new TicketController(ticketRepo);
        VoucherController voucherController = new VoucherController(voucherRepo);
        TransactionController transactionController = new TransactionController(
                SyncMechanism.SYNCHRONIZED, seatRepo, transactionRepo, voucherRepo, matchRepo);
        StaffController staffController = new StaffController(
                seatRepo, ticketRepo, transactionRepo, matchRepo, supportRepo);
        SimulatorController simulatorController = new SimulatorController(
                10, SyncMechanism.SYNCHRONIZED);
        ReportController reportController = new ReportController(transactionRepo);

        AdminController adminController = new AdminController(
                accountController,
                matchController,
                stadiumController,
                sectionController,
                seatController,
                ticketController,
                voucherController,
                simulatorController,
                reportController);

        AuthView authView = new AuthView(
                scanner,
                authController,
                accountController,
                matchController,
                voucherController,
                transactionController,
                staffController,
                adminController);
        authView.run();
    }
}