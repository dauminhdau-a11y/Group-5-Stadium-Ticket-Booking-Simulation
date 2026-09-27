package com.stadium.view;

import java.util.Scanner;

public class MainView {
    private final Scanner scanner = new Scanner(System.in);
    public void displayMenu() { System.out.println("=== Stadium Ticket Booking ==="); }
    public int getUserInput() { return Integer.parseInt(scanner.nextLine()); }
}
