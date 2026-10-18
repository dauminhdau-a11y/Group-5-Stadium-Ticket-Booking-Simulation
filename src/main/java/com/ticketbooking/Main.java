package com.ticketbooking;

import com.ticketbooking.controller.AuthController;
import com.ticketbooking.controller.FanController;
import com.ticketbooking.repository.FanRepository;
import com.ticketbooking.view.AuthView;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        FanRepository fanRepository = new FanRepository("data/fans.csv");
        AuthController authController = new AuthController(fanRepository);
        FanController fanController = new FanController(fanRepository);
        AuthView authView = new AuthView(new Scanner(System.in), fanController);
        authView.run(authController);
    }
}