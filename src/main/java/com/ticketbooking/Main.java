package com.ticketbooking;

import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.AccountController;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import com.ticketbooking.view.AccountView;
import com.ticketbooking.view.AuthView;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        FanRepository fanRepository = new FanRepository("data/fans.csv");
        AccountRepository staffRepository = new AccountRepository("data/staff.csv");
        AccountRepository adminRepository = new AccountRepository("data/admins.csv");
        AuthController authController =
                new AuthController(fanRepository, staffRepository, adminRepository);
        Scanner scanner = new Scanner(System.in);
        AccountController accountController = new AccountController(staffRepository,
                adminRepository, fanRepository, new AccountView(scanner), authController);
        AuthView authView = new AuthView(scanner, accountController);
        authView.run(authController);
    }
}