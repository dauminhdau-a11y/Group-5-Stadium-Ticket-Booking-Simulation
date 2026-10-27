package com.ticketbooking.view;

import com.ticketbooking.controller.AdminController;
import com.ticketbooking.model.Admin;
import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Match;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Section;
import com.ticketbooking.model.SimulationResult;
import com.ticketbooking.model.Stadium;
import com.ticketbooking.model.Staff;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.Voucher;
import com.ticketbooking.model.enums.MatchStatus;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SeatType;
import com.ticketbooking.model.enums.SectionType;
import com.ticketbooking.model.enums.SyncMechanism;
import com.ticketbooking.model.enums.TicketStatus;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AdminView {
    private AdminController adminController;
    private Scanner scanner;
    private SimulatorView simulatorView;

    public AdminView(AdminController adminController, Scanner scanner) {
        if (adminController == null || scanner == null) {
            throw new IllegalArgumentException("Admin controller and scanner must not be null.");
        }
        this.adminController = adminController;
        this.scanner = scanner;
        this.simulatorView = new SimulatorView(adminController.getSimulatorController());
    }

    public AdminController getAdminController() {
        return adminController;
    }

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(Admin admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== ADMIN MAIN MENU ===");
            System.out.println("Welcome, Administrator " + admin.getUsername() + "!");
            System.out.println("1. Manage account");
            System.out.println("2. Manage match");
            System.out.println("3. Manage stadium");
            System.out.println("4. Manage section");
            System.out.println("5. Manage seat");
            System.out.println("6. Manage ticket");
            System.out.println("7. Manage voucher");
            System.out.println("8. Simulation & data");
            System.out.println("9. Logout");
            System.out.print("Choose (1-9): ");

            String input = readLine();
            if (input == null) {
                return;
            }
            String choice = input.trim();
            try {
                switch (choice) {
                    case "1":
                        manageAccount();
                        break;
                    case "2":
                        manageMatch();
                        break;
                    case "3":
                        manageStadium();
                        break;
                    case "4":
                        manageSection();
                        break;
                    case "5":
                        manageSeat();
                        break;
                    case "6":
                        manageTicket();
                        break;
                    case "7":
                        manageVoucher();
                        break;
                    case "8":
                        manageSimulationAndData();
                        break;
                    case "9":
                        loggedIn = false;
                        System.out.println("Admin logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose from 1 to 9.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    // ==========================================
    // 1. MANAGE ACCOUNT
    // ==========================================
    public void manageAccount() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Account ---");
            System.out.println("1. Create staff account");
            System.out.println("2. Remove staff account");
            System.out.println("3. Create fan account");
            System.out.println("4. Update fan account");
            System.out.println("5. Search fan account");
            System.out.println("6. Remove fan account");
            System.out.println("7. Lock fan account");
            System.out.println("0. Back");
            System.out.print("Choose (0-7): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Staff username: ");
                    String sUser = readLine();
                    System.out.print("Staff password: ");
                    String sPass = readLine();
                    System.out.print("Staff email: ");
                    String sEmail = readLine();
                    Staff staff = new Staff();
                    staff.setUsername(sUser != null ? sUser.trim() : "");
                    staff.setPasswordHash(sPass != null ? sPass.trim() : "");
                    staff.setEmail(sEmail != null ? sEmail.trim() : "");
                    try {
                        adminController.getAccountController().createStaffAccount(staff);
                        System.out.println("Staff account created.");
                    } catch (Exception e) {
                        System.out.println("[ERROR]: " + e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print("Staff Account ID to remove: ");
                    String remStaffId = readLine();
                    if (remStaffId != null) {
                        boolean ok = adminController.getAccountController().removeAccount(remStaffId.trim());
                        System.out.println(ok ? "Staff account removed." : "Account not found.");
                    }
                    break;
                case "3":
                    System.out.print("Fan username: ");
                    String fUser = readLine();
                    System.out.print("Fan password: ");
                    String fPass = readLine();
                    System.out.print("Fan email: ");
                    String fEmail = readLine();
                    Fan fan = new Fan();
                    fan.setUsername(fUser != null ? fUser.trim() : "");
                    fan.setPasswordHash(fPass != null ? fPass.trim() : "");
                    fan.setEmail(fEmail != null ? fEmail.trim() : "");
                    try {
                        adminController.getAccountController().createFanAccount(fan);
                        System.out.println("Fan account created.");
                    } catch (Exception e) {
                        System.out.println("[ERROR]: " + e.getMessage());
                    }
                    break;
                case "4":
                    adminController.getAccountController().getAccountView().updateFanProfile(adminController.getAccountController());
                    break;
                case "5":
                    System.out.print("Search keyword: ");
                    String kw = readLine();
                    List<Fan> fans = adminController.getAccountController().searchFanAccount(kw);
                    adminController.getAccountController().getAccountView().showAccountList(fans);
                    break;
                case "6":
                    System.out.print("Fan Account ID to remove: ");
                    String remFanId = readLine();
                    if (remFanId != null) {
                        boolean ok = adminController.getAccountController().removeAccount(remFanId.trim());
                        System.out.println(ok ? "Fan account removed." : "Account not found.");
                    }
                    break;
                case "7":
                    System.out.print("Fan Account ID to lock: ");
                    String lockId = readLine();
                    if (lockId != null) {
                        try {
                            adminController.getAccountController().lockFanAccount(lockId.trim());
                            System.out.println("Fan account locked.");
                        } catch (Exception e) {
                            System.out.println("[ERROR]: " + e.getMessage());
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 2. MANAGE MATCH
    // ==========================================
    public void manageMatch() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Match ---");
            System.out.println("1. Create match");
            System.out.println("2. Update match detail");
            System.out.println("3. Update match schedule");
            System.out.println("4. Search match (Admin)");
            System.out.println("5. Remove match");
            System.out.println("0. Back");
            System.out.print("Choose (0-5): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Match ID (e.g. M005): ");
                    String mId = readLine();
                    System.out.print("Home Team: ");
                    String hTeam = readLine();
                    System.out.print("Away Team: ");
                    String aTeam = readLine();
                    System.out.print("Date (YYYY-MM-DD): ");
                    String dStr = readLine();
                    System.out.print("Time (HH:mm): ");
                    String tStr = readLine();
                    System.out.print("Stadium: ");
                    String std = readLine();
                    try {
                        LocalDate mDate = LocalDate.parse(dStr.trim());
                        Match match = new Match(mId.trim(), hTeam.trim(), aTeam.trim(), mDate, tStr.trim(), std.trim());
                        boolean ok = adminController.getMatchController().createMatch(match);
                        System.out.println(ok ? "Match created successfully." : "Failed to create match (ID exists).");
                    } catch (Exception e) {
                        System.out.println("[ERROR]: " + e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print("Match ID to update: ");
                    String upId = readLine();
                    System.out.print("New Home Team (leave blank to skip): ");
                    String nh = readLine();
                    System.out.print("New Away Team (leave blank to skip): ");
                    String na = readLine();
                    System.out.print("New Stadium (leave blank to skip): ");
                    String ns = readLine();
                    boolean okDetail = adminController.getMatchController().updateMatchDetail(upId.trim(), nh, na, ns);
                    System.out.println(okDetail ? "Match details updated." : "Match not found.");
                    break;
                case "3":
                    System.out.print("Match ID to reschedule: ");
                    String scId = readLine();
                    System.out.print("New Date (YYYY-MM-DD): ");
                    String nd = readLine();
                    System.out.print("New Time (HH:mm): ");
                    String nt = readLine();
                    try {
                        LocalDate newD = (nd != null && !nd.trim().isEmpty()) ? LocalDate.parse(nd.trim()) : null;
                        boolean okSc = adminController.getMatchController().updateMatchSchedule(scId.trim(), newD, nt);
                        System.out.println(okSc ? "Match schedule updated." : "Match not found.");
                    } catch (Exception e) {
                        System.out.println("[ERROR]: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.print("Search match keyword: ");
                    String q = readLine();
                    List<Match> matches = adminController.getMatchController().searchMatches(q);
                    System.out.println("Found " + matches.size() + " matches:");
                    for (Match m : matches) {
                        System.out.println(" - [" + m.getMatchId() + "] " + m.getTitle() + " | " + m.getMatchDate() + " " + m.getMatchTime() + " | " + m.getStadium());
                    }
                    break;
                case "5":
                    System.out.print("Match ID to remove: ");
                    String rmId = readLine();
                    boolean okRm = adminController.getMatchController().removeMatch(rmId.trim());
                    System.out.println(okRm ? "Match removed." : "Match not found.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 3. MANAGE STADIUM
    // ==========================================
    public void manageStadium() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Stadium ---");
            System.out.println("1. Create stadium");
            System.out.println("2. Update stadium");
            System.out.println("3. Remove stadium");
            System.out.println("0. Back");
            System.out.print("Choose (0-3): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Stadium ID (e.g. STD_005): ");
                    String sId = readLine();
                    System.out.print("Stadium Name: ");
                    String sName = readLine();
                    System.out.print("Location: ");
                    String sLoc = readLine();
                    System.out.print("Capacity: ");
                    int cap = Integer.parseInt(readLine().trim());
                    Stadium stadium = new Stadium(sId.trim(), sName.trim(), sLoc.trim(), cap);
                    boolean ok = adminController.getStadiumController().createStadium(stadium);
                    System.out.println(ok ? "Stadium created successfully." : "Stadium ID exists.");
                    break;
                case "2":
                    System.out.print("Stadium ID to update: ");
                    String upId = readLine();
                    System.out.print("New Name: ");
                    String nn = readLine();
                    System.out.print("New Location: ");
                    String nl = readLine();
                    System.out.print("New Capacity: ");
                    int nc = Integer.parseInt(readLine().trim());
                    boolean okUp = adminController.getStadiumController().updateStadium(upId.trim(), nn, nl, nc);
                    System.out.println(okUp ? "Stadium updated." : "Stadium not found.");
                    break;
                case "3":
                    System.out.print("Stadium ID to remove: ");
                    String rmId = readLine();
                    boolean okRm = adminController.getStadiumController().removeStadium(rmId.trim());
                    System.out.println(okRm ? "Stadium removed." : "Stadium not found.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 4. MANAGE SECTION
    // ==========================================
    public void manageSection() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Section ---");
            System.out.println("1. Create section");
            System.out.println("2. Update section");
            System.out.println("3. Remove section");
            System.out.println("0. Back");
            System.out.print("Choose (0-3): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Section ID (e.g. SEC_005): ");
                    String secId = readLine();
                    System.out.print("Stadium ID: ");
                    String stdId = readLine();
                    System.out.print("Section Name: ");
                    String sName = readLine();
                    System.out.print("Type (VIP/STANDARD/ECONOMY/STANDING): ");
                    SectionType type = SectionType.valueOf(readLine().trim().toUpperCase());
                    System.out.print("Capacity: ");
                    int cap = Integer.parseInt(readLine().trim());
                    System.out.print("Base Price: ");
                    double price = Double.parseDouble(readLine().trim());
                    Section section = new Section(secId.trim(), stdId.trim(), sName.trim(), type, cap, price);
                    boolean ok = adminController.getSectionController().createSection(section);
                    System.out.println(ok ? "Section created successfully." : "Section ID exists.");
                    break;
                case "2":
                    System.out.print("Section ID to update: ");
                    String upId = readLine();
                    System.out.print("New Name: ");
                    String nn = readLine();
                    System.out.print("New Type (VIP/STANDARD/ECONOMY/STANDING): ");
                    SectionType nt = SectionType.valueOf(readLine().trim().toUpperCase());
                    System.out.print("New Capacity: ");
                    int nc = Integer.parseInt(readLine().trim());
                    System.out.print("New Price: ");
                    double np = Double.parseDouble(readLine().trim());
                    boolean okUp = adminController.getSectionController().updateSection(upId.trim(), nn, nt, nc, np);
                    System.out.println(okUp ? "Section updated." : "Section not found.");
                    break;
                case "3":
                    System.out.print("Section ID to remove: ");
                    String rmId = readLine();
                    boolean okRm = adminController.getSectionController().removeSection(rmId.trim());
                    System.out.println(okRm ? "Section removed." : "Section not found.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 5. MANAGE SEAT
    // ==========================================
    public void manageSeat() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Seat ---");
            System.out.println("1. Create seat");
            System.out.println("2. Update seat");
            System.out.println("3. Remove seat");
            System.out.println("4. Search seat");
            System.out.println("0. Back");
            System.out.print("Choose (0-4): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Seat ID (e.g. VIP-999): ");
                    String sId = readLine();
                    System.out.print("Match ID: ");
                    String mId = readLine();
                    System.out.print("Seat Type (VVIP/VIP/STANDARD/ECONOMY/STANDING): ");
                    SeatType sType = SeatType.valueOf(readLine().trim().toUpperCase());
                    System.out.print("Price: ");
                    double price = Double.parseDouble(readLine().trim());
                    Seat seat = new Seat(sId.trim(), mId.trim(), SeatStatus.AVAILABLE, sType, price, 1);
                    boolean ok = adminController.getSeatController().createSeat(seat);
                    System.out.println(ok ? "Seat created." : "Seat ID exists.");
                    break;
                case "2":
                    System.out.print("Seat ID to update: ");
                    String upId = readLine();
                    System.out.print("New Status (AVAILABLE/BOOKED/RESERVED/LOCKED): ");
                    SeatStatus ns = SeatStatus.valueOf(readLine().trim().toUpperCase());
                    System.out.print("New Type (VVIP/VIP/STANDARD/ECONOMY/STANDING): ");
                    SeatType nt = SeatType.valueOf(readLine().trim().toUpperCase());
                    System.out.print("New Price: ");
                    double np = Double.parseDouble(readLine().trim());
                    boolean okUp = adminController.getSeatController().updateSeat(upId.trim(), ns, nt, np);
                    System.out.println(okUp ? "Seat updated." : "Seat not found.");
                    break;
                case "3":
                    System.out.print("Seat ID to remove: ");
                    String rmId = readLine();
                    boolean okRm = adminController.getSeatController().removeSeat(rmId.trim());
                    System.out.println(okRm ? "Seat removed." : "Seat not found.");
                    break;
                case "4":
                    System.out.print("Search seat keyword: ");
                    String q = readLine();
                    List<Seat> seats = adminController.getSeatController().searchSeats(q);
                    System.out.println("Found " + seats.size() + " seats:");
                    int count = 0;
                    for (Seat s : seats) {
                        System.out.println(" - " + s.getSeatId() + " | Match: " + s.getMatchId() + " | " + s.getSeatType() + " | " + s.getStatus() + " | " + String.format("%,.0f VND", s.getPrice()));
                        count++;
                        if (count >= 20) {
                            System.out.println("... (" + (seats.size() - count) + " more seats)");
                            break;
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 6. MANAGE TICKET
    // ==========================================
    public void manageTicket() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Ticket ---");
            System.out.println("1. Create ticket");
            System.out.println("2. Update ticket price");
            System.out.println("3. Remove ticket");
            System.out.println("0. Back");
            System.out.print("Choose (0-3): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Ticket ID (e.g. TCK-001): ");
                    String tId = readLine();
                    System.out.print("Match ID: ");
                    String mId = readLine();
                    System.out.print("Seat ID: ");
                    String sId = readLine();
                    System.out.print("Price: ");
                    double price = Double.parseDouble(readLine().trim());
                    Ticket ticket = new Ticket(tId.trim(), mId.trim(), sId.trim(), price, TicketStatus.AVAILABLE);
                    boolean ok = adminController.getTicketController().createTicket(ticket);
                    System.out.println(ok ? "Ticket created." : "Ticket ID exists.");
                    break;
                case "2":
                    System.out.print("Ticket ID to update: ");
                    String upId = readLine();
                    System.out.print("New Price: ");
                    double np = Double.parseDouble(readLine().trim());
                    boolean okUp = adminController.getTicketController().updateTicketPrice(upId.trim(), np);
                    System.out.println(okUp ? "Ticket price updated." : "Ticket not found.");
                    break;
                case "3":
                    System.out.print("Ticket ID to remove: ");
                    String rmId = readLine();
                    boolean okRm = adminController.getTicketController().removeTicket(rmId.trim());
                    System.out.println(okRm ? "Ticket removed." : "Ticket not found.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 7. MANAGE VOUCHER
    // ==========================================
    public void manageVoucher() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Voucher ---");
            System.out.println("1. Create voucher");
            System.out.println("2. Update voucher");
            System.out.println("3. Remove voucher");
            System.out.println("0. Back");
            System.out.print("Choose (0-3): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Voucher code (e.g. PROMO50): ");
                    String code = readLine();
                    System.out.print("Discount percent (0-100, 0 if fixed amount): ");
                    double dPct = Double.parseDouble(readLine().trim());
                    System.out.print("Discount amount (0 if percent): ");
                    double dAmt = Double.parseDouble(readLine().trim());
                    Voucher voucher = new Voucher(code.trim(), dPct, dAmt, false);
                    boolean ok = adminController.getVoucherController().createVoucher(voucher);
                    System.out.println(ok ? "Voucher created." : "Voucher code exists.");
                    break;
                case "2":
                    System.out.print("Voucher code to update: ");
                    String upCode = readLine();
                    System.out.print("New discount percent: ");
                    double np = Double.parseDouble(readLine().trim());
                    System.out.print("New discount amount: ");
                    double na = Double.parseDouble(readLine().trim());
                    System.out.print("Is used? (true/false): ");
                    boolean nu = Boolean.parseBoolean(readLine().trim());
                    boolean okUp = adminController.getVoucherController().updateVoucher(upCode.trim(), np, na, nu);
                    System.out.println(okUp ? "Voucher updated." : "Voucher not found.");
                    break;
                case "3":
                    System.out.print("Voucher code to remove: ");
                    String rmCode = readLine();
                    boolean okRm = adminController.getVoucherController().removeVoucher(rmCode.trim());
                    System.out.println(okRm ? "Voucher removed." : "Voucher not found.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 8. SIMULATION & DATA
    // ==========================================
    public void manageSimulationAndData() throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Simulation & Data ---");
            System.out.println("1. Generate CSV data (>= 10,000 rows)");
            System.out.println("2. Set number of threads");
            System.out.println("3. Choose sync mechanism");
            System.out.println("4. Run simulation");
            System.out.println("5. View comparison result");
            System.out.println("6. Save result to CSV");
            System.out.println("7. View transactions");
            System.out.println("8. View revenue");
            System.out.println("0. Back");
            System.out.print("Choose (0-8): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            switch (choice.trim()) {
                case "1":
                    System.out.print("Enter number of rows to generate (min 10000): ");
                    int rows = Integer.parseInt(readLine().trim());
                    System.out.print("Match ID for seats (default M001): ");
                    String mId = readLine();
                    if (mId == null || mId.trim().isEmpty()) {
                        mId = "M001";
                    }
                    int genCount = adminController.getSimulatorController().generateData(rows, mId.trim());
                    System.out.println("Successfully generated " + genCount + " seats into data/seats.csv!");
                    break;
                case "2":
                    System.out.print("Enter number of threads (current: "
                            + adminController.getSimulatorController().getThreadCount() + "): ");
                    int threads = Integer.parseInt(readLine().trim());
                    adminController.getSimulatorController().setThreadCount(threads);
                    System.out.println("Threads set to " + threads);
                    break;
                case "3":
                    System.out.println("Choose synchronization mechanism:");
                    System.out.println("1. NO_LOCK (No synchronization)");
                    System.out.println("2. SYNCHRONIZED (Java synchronized block)");
                    System.out.println("3. OPTIMISTIC (Version-based optimistic locking)");
                    System.out.println("4. FILE_LOCK (File-level locking)");
                    System.out.print("Select (1-4): ");
                    String smChoice = readLine();
                    SyncMechanism mechanism;
                    if ("2".equals(smChoice)) {
                        mechanism = SyncMechanism.SYNCHRONIZED;
                    } else if ("3".equals(smChoice)) {
                        mechanism = SyncMechanism.OPTIMISTIC;
                    } else if ("4".equals(smChoice)) {
                        mechanism = SyncMechanism.FILE_LOCK;
                    } else {
                        mechanism = SyncMechanism.NO_LOCK;
                    }
                    adminController.getSimulatorController().setSelectedMechanism(mechanism);
                    System.out.println("Selected sync mechanism: " + mechanism);
                    break;
                case "4":
                    System.out.print("Match ID to simulate (default M001): ");
                    String simMatch = readLine();
                    if (simMatch == null || simMatch.trim().isEmpty()) {
                        simMatch = "M001";
                    }
                    System.out.print("Total concurrent booking requests: ");
                    int requests = Integer.parseInt(readLine().trim());
                    System.out.println("\nRunning simulation with "
                            + adminController.getSimulatorController().getThreadCount() + " threads using "
                            + adminController.getSimulatorController().getSelectedMechanism() + "...");
                    SimulationResult res = adminController.getSimulatorController().runSimulation(simMatch.trim(), requests);
                    System.out.println(">> Simulation Complete!");
                    System.out.println("Execution Time:   " + res.getExecutionTimeMs() + " ms");
                    System.out.println("Successful:       " + res.getSuccessfulRequests());
                    System.out.println("Failed/Conflicts: " + res.getFailedRequests());
                    break;
                case "5":
                    List<SimulationResult> list = adminController.getSimulatorController().getComparisonResults();
                    simulatorView.displayComparisonTable(list);
                    break;
                case "6":
                    adminController.getSimulatorController().saveResultsToCsv();
                    System.out.println("Simulation results saved to data/simulation_results.csv.");
                    break;
                case "7":
                    List<Transaction> txns = adminController.getSimulatorController().getTransactions();
                    System.out.println("\nTotal Transactions: " + txns.size());
                    int tCount = 0;
                    for (Transaction t : txns) {
                        System.out.println(" - " + t.getTransactionId() + " | Fan: " + t.getFanId() + " | Match: " + t.getMatchId() + " | Amount: " + String.format("%,.0f VND", t.getTotalAmount()) + " | " + t.getStatus());
                        tCount++;
                        if (tCount >= 25) {
                            System.out.println("... (" + (txns.size() - tCount) + " more transactions)");
                            break;
                        }
                    }
                    break;
                case "8":
                    double revenue = adminController.getSimulatorController().calculateRevenue();
                    System.out.printf("%nTotal Recorded Revenue: %,.0f VND%n", revenue);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}