package com.stadium;
import com.stadium.view.SimulatorView;

public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("   STADIUM TICKET BOOKING SIMULATION SYSTEM");
        System.out.println("===============================================");
        SimulatorView simulatorView = new SimulatorView();
        simulatorView.showSimulator();
    }
}

