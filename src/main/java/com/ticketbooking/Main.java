package com.ticketbooking;

import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.FanController;
import com.ticketbooking.repository.AccountRepository;
import com.ticketbooking.repository.FanRepository;
import com.ticketbooking.view.AuthView;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        FanRepository fanRepository = new FanRepository("data/fans.csv");
        AccountRepository staffRepository = new AccountRepository("data/staff.csv");
        AccountRepository adminRepository = new AccountRepository("data/admins.csv");
        AuthController authController =
                new AuthController(fanRepository, staffRepository, adminRepository);
        FanController fanController = new FanController(fanRepository);
        AuthView authView = new AuthView(new Scanner(System.in), fanController);
        authView.run(authController);
    }
}