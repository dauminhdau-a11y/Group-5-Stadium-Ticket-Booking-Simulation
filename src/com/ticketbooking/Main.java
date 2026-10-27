package com.ticketbooking;

import com.ticketbooking.controller.AccountController;
import com.ticketbooking.controller.AdminController;
import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.MatchController;
import com.ticketbooking.controller.ReportController;
import com.ticketbooking.controller.SeatController;
import com.ticketbooking.controller.SectionController;
import com.ticketbooking.controller.SimulatorController;
import com.ticketbooking.controller.StadiumController;
import com.ticketbooking.controller.StaffController;
import com.ticketbooking.controller.TicketController;
import com.ticketbooking.controller.TransactionController;
import com.ticketbooking.controller.VoucherController;
import com.ticketbooking.model.enums.SyncMechanism;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import com.ticketbooking.repository.MatchRepository;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.SectionRepository;
import com.ticketbooking.repository.StadiumRepository;
import com.ticketbooking.repository.SupportRequestRepository;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.TransactionRepository;
import com.ticketbooking.repository.VoucherRepository;
import com.ticketbooking.view.AccountView;
import com.ticketbooking.view.AuthView;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        AccountView accountView = new AccountView(scanner);
        AccountController accountController = new AccountController(staffRepo, adminRepo, fanRepo, accountView, authController);

        MatchController matchController = new MatchController(matchRepo);
        StadiumController stadiumController = new StadiumController(stadiumRepo);
        SectionController sectionController = new SectionController(sectionRepo);
        SeatController seatController = new SeatController(seatRepo);
        TicketController ticketController = new TicketController(ticketRepo);
        VoucherController voucherController = new VoucherController(voucherRepo);
        TransactionController transactionController = new TransactionController(
        SyncMechanism.SYNCHRONIZED, seatRepo, transactionRepo, voucherRepo, matchRepo);
        StaffController staffController = new StaffController(seatRepo, ticketRepo, transactionRepo, matchRepo, supportRepo);
        SimulatorController simulatorController = new SimulatorController(10, SyncMechanism.SYNCHRONIZED);
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
                stadiumController,
                sectionController,
                seatController,
                ticketController,
                voucherController,
                transactionController,
                staffController,
                adminController,
                simulatorController,
                reportController);

        authView.run();
    }
}