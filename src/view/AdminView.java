package view;

import controller.AdminController;
import model.Account;
import model.Admin;
import model.Fan;
import model.Match;
import model.Seat;
import model.Section;
import model.SimulationResult;
import model.Stadium;
import model.Staff;
import model.Ticket;
import model.Transaction;
import model.Voucher;
import model.enums.Role;
import model.enums.SeatStatus;
import model.enums.Stand;
import model.enums.SyncMechanism;
import model.enums.TicketStatus;
import java.io.IOException;
import java.time.LocalDate;
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
            System.out.println("8. Simulation");
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
            System.out.println("1. View all staff accounts");
            System.out.println("2. Create staff account");
            System.out.println("3. Remove staff account");
            System.out.println("4. View all fan accounts");
            System.out.println("5. Create fan account");
            System.out.println("6. Update fan account");
            System.out.println("7. Search fan account");
            System.out.println("8. Remove fan account");
            System.out.println("9. Lock fan account");
            System.out.println("10. Unlock fan account");
            System.out.println("0. Back");
            System.out.print("Choose (0-10): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        List<Account> staffList = adminController.getAccountController().findByRole(Role.STAFF);
                        System.out.println("\n--- Staff Accounts (" + staffList.size() + ") ---");
                        System.out.printf("%-14s | %-16s | %-24s | %-10s%n", "ACCOUNT ID", "USERNAME", "EMAIL", "STATUS");
                        System.out.println("-------------------------------------------------------------------------");
                        for (Account a : staffList) {
                            System.out.printf("%-14s | %-16s | %-24s | %-10s%n",
                                    a.getId(), a.getUsername(), a.getEmail() != null ? a.getEmail() : "-",
                                    a.isLocked() ? "LOCKED" : "ACTIVE");
                        }
                        break;
                    case "2":
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
                        adminController.getAccountController().createStaffAccount(staff);
                        System.out.println(">> Staff account created successfully.");
                        break;
                    case "3":
                        System.out.print("Staff Account ID to remove: ");
                        String remStaffId = readLine();
                        if (remStaffId != null && !remStaffId.trim().isEmpty()) {
                            boolean ok = adminController.getAccountController().removeAccount(remStaffId.trim());
                            System.out.println(ok ? ">> Staff account removed." : "[ERROR]: Account not found.");
                        }
                        break;
                    case "4":
                        List<Account> fanList = adminController.getAccountController().findByRole(Role.FAN);
                        System.out.println("\n--- Fan Accounts (" + fanList.size() + ") ---");
                        System.out.printf("%-14s | %-16s | %-24s | %-10s%n", "ACCOUNT ID", "USERNAME", "EMAIL", "STATUS");
                        System.out.println("-------------------------------------------------------------------------");
                        for (Account a : fanList) {
                            System.out.printf("%-14s | %-16s | %-24s | %-10s%n",
                                    a.getId(), a.getUsername(), a.getEmail() != null ? a.getEmail() : "-",
                                    a.isLocked() ? "LOCKED" : "ACTIVE");
                        }
                        break;
                    case "5":
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
                        adminController.getAccountController().createFanAccount(fan);
                        System.out.println(">> Fan account created successfully.");
                        break;
                    case "6":
                        adminController.getAccountController().getAccountView().updateFanProfile(adminController.getAccountController());
                        break;
                    case "7":
                        System.out.print("Search keyword: ");
                        String kw = readLine();
                        List<Fan> fans = adminController.getAccountController().searchFanAccount(kw);
                        adminController.getAccountController().getAccountView().showAccountList(fans);
                        break;
                    case "8":
                        System.out.print("Fan Account ID to remove: ");
                        String remFanId = readLine();
                        if (remFanId != null && !remFanId.trim().isEmpty()) {
                            boolean ok = adminController.getAccountController().removeAccount(remFanId.trim());
                            System.out.println(ok ? ">> Fan account removed." : "[ERROR]: Account not found.");
                        }
                        break;
                    case "9":
                        System.out.print("Fan Account ID to lock: ");
                        String lockId = readLine();
                        if (lockId != null && !lockId.trim().isEmpty()) {
                            adminController.getAccountController().lockFanAccount(lockId.trim());
                            System.out.println(">> Fan account locked.");
                        }
                        break;
                    case "10":
                        System.out.print("Fan Account ID to unlock: ");
                        String unlockId = readLine();
                        if (unlockId != null && !unlockId.trim().isEmpty()) {
                            adminController.getAccountController().unlockFanAccount(unlockId.trim());
                            System.out.println(">> Fan account unlocked.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
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
            System.out.println("1. View all matches");
            System.out.println("2. Create match");
            System.out.println("3. Update match detail");
            System.out.println("4. Update match schedule");
            System.out.println("5. Search match");
            System.out.println("6. Remove match");
            System.out.println("0. Back");
            System.out.print("Choose (0-6): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        List<Match> allMatches = adminController.getMatchController().getAllMatches();
                        System.out.println("\n--- All Matches (" + allMatches.size() + ") ---");
                        System.out.printf("%-10s | %-32s | %-12s | %-8s | %-20s%n", "MATCH ID", "MATCH TITLE", "DATE", "TIME", "STADIUM");
                        System.out.println("-----------------------------------------------------------------------------------------");
                        for (Match m : allMatches) {
                            System.out.printf("%-10s | %-32s | %-12s | %-8s | %-20s%n",
                                    m.getMatchId(), m.getTitle(),
                                    m.getMatchDate() != null ? m.getMatchDate().toString() : "N/A",
                                    m.getMatchTime() != null ? m.getMatchTime() : "N/A",
                                    m.getStadium());
                        }
                        break;
                    case "2":
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
                        LocalDate mDate = LocalDate.parse(dStr != null ? dStr.trim() : "");
                        Match match = new Match(mId != null ? mId.trim() : "", hTeam != null ? hTeam.trim() : "",
                                aTeam != null ? aTeam.trim() : "", mDate, tStr != null ? tStr.trim() : "", std != null ? std.trim() : "");
                        boolean ok = adminController.getMatchController().createMatch(match);
                        System.out.println(ok ? ">> Match created successfully." : "[ERROR]: Failed to create match (ID exists).");
                        break;
                    case "3":
                        System.out.print("Match ID to update: ");
                        String upId = readLine();
                        System.out.print("New Home Team (leave blank to skip): ");
                        String nh = readLine();
                        System.out.print("New Away Team (leave blank to skip): ");
                        String na = readLine();
                        System.out.print("New Stadium (leave blank to skip): ");
                        String ns = readLine();
                        boolean okDetail = adminController.getMatchController().updateMatchDetail(upId != null ? upId.trim() : "", nh, na, ns);
                        System.out.println(okDetail ? ">> Match details updated." : "[ERROR]: Match not found.");
                        break;
                    case "4":
                        System.out.print("Match ID to reschedule: ");
                        String scId = readLine();
                        System.out.print("New Date (YYYY-MM-DD): ");
                        String nd = readLine();
                        System.out.print("New Time (HH:mm): ");
                        String nt = readLine();
                        LocalDate newD = (nd != null && !nd.trim().isEmpty()) ? LocalDate.parse(nd.trim()) : null;
                        boolean okSc = adminController.getMatchController().updateMatchSchedule(scId != null ? scId.trim() : "", newD, nt);
                        System.out.println(okSc ? ">> Match schedule updated." : "[ERROR]: Match not found.");
                        break;
                    case "5":
                        System.out.print("Search match keyword: ");
                        String q = readLine();
                        List<Match> matches = adminController.getMatchController().searchMatches(q);
                        System.out.println("Found " + matches.size() + " matches:");
                        for (Match m : matches) {
                            System.out.println(" - [" + m.getMatchId() + "] " + m.getTitle() + " | " + m.getMatchDate() + " " + m.getMatchTime() + " | " + m.getStadium());
                        }
                        break;
                    case "6":
                        System.out.print("Match ID to remove: ");
                        String rmId = readLine();
                        boolean okRm = adminController.getMatchController().removeMatch(rmId != null ? rmId.trim() : "");
                        System.out.println(okRm ? ">> Match removed." : "[ERROR]: Match not found.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
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
            System.out.println("1. View all stadiums");
            System.out.println("2. Create stadium (Auto-provisions 4 Stands A, B, C, D)");
            System.out.println("3. Update stadium");
            System.out.println("4. Remove stadium");
            System.out.println("0. Back");
            System.out.print("Choose (0-4): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        List<Stadium> stadiums = adminController.getStadiumController().getAllStadiums();
                        System.out.println("\n--- Stadiums (" + stadiums.size() + ") ---");
                        System.out.printf("%-12s | %-28s | %-20s | %-10s%n", "STADIUM ID", "NAME", "LOCATION", "CAPACITY");
                        System.out.println("--------------------------------------------------------------------------------");
                        for (Stadium s : stadiums) {
                            System.out.printf("%-12s | %-28s | %-20s | %,d%n",
                                    s.getStadiumId(), s.getName(), s.getLocation(), s.getCapacity());
                        }
                        break;
                    case "2":
                        System.out.print("Stadium ID (e.g. STD_002): ");
                        String sId = readLine();
                        System.out.print("Stadium Name: ");
                        String sName = readLine();
                        System.out.print("Location: ");
                        String sLoc = readLine();
                        System.out.print("Capacity (e.g. 40000): ");
                        String capStr = readLine();
                        int cap = (capStr != null && !capStr.trim().isEmpty()) ? Integer.parseInt(capStr.trim()) : 40000;
                        Stadium stadium = new Stadium(sId != null ? sId.trim() : "", sName != null ? sName.trim() : "", sLoc != null ? sLoc.trim() : "", cap);
                        boolean ok = adminController.getStadiumController().createStadium(stadium);
                        if (ok) {
                            System.out.println(">> Stadium '" + sId + "' created successfully!");
                            System.out.println(">> Auto-generated 4 Stands (A: South, B: North, C: West, D: East) in sections database!");
                        } else {
                            System.out.println("[ERROR]: Stadium ID already exists or invalid.");
                        }
                        break;
                    case "3":
                        System.out.print("Stadium ID to update: ");
                        String upId = readLine();
                        System.out.print("New Name: ");
                        String nn = readLine();
                        System.out.print("New Location: ");
                        String nl = readLine();
                        System.out.print("New Capacity: ");
                        String ncStr = readLine();
                        int nc = (ncStr != null && !ncStr.trim().isEmpty()) ? Integer.parseInt(ncStr.trim()) : 0;
                        boolean okUp = adminController.getStadiumController().updateStadium(upId != null ? upId.trim() : "", nn, nl, nc);
                        System.out.println(okUp ? ">> Stadium updated." : "[ERROR]: Stadium not found.");
                        break;
                    case "4":
                        System.out.print("Stadium ID to remove: ");
                        String rmId = readLine();
                        boolean okRm = adminController.getStadiumController().removeStadium(rmId != null ? rmId.trim() : "");
                        System.out.println(okRm ? ">> Stadium and associated sections removed." : "[ERROR]: Stadium not found.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
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
            System.out.println("1. View all sections");
            System.out.println("2. View sections by stadium");
            System.out.println("3. Create section");
            System.out.println("4. Update section");
            System.out.println("5. Remove section");
            System.out.println("0. Back");
            System.out.print("Choose (0-5): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        List<Section> allSections = adminController.getSectionController().getAllSections();
                        printSectionTable(allSections);
                        break;
                    case "2":
                        System.out.print("Enter Stadium ID (e.g. STD_001): ");
                        String stdQuery = readLine();
                        List<Section> stdSections = adminController.getSectionController().getSectionsByStadium(stdQuery != null ? stdQuery.trim() : "");
                        printSectionTable(stdSections);
                        break;
                    case "3":
                        System.out.print("Section ID (e.g. SEC_STD_001_A): ");
                        String secId = readLine();
                        System.out.print("Stadium ID: ");
                        String stdId = readLine();
                        System.out.print("Section Name: ");
                        String sName = readLine();
                        System.out.print("Stand (A/B/C/D): ");
                        String standStr = readLine();
                        Stand stand = (standStr != null && !standStr.trim().isEmpty())
                                ? Stand.valueOf(standStr.trim().toUpperCase()) : Stand.A;
                        System.out.print("Capacity: ");
                        String capStr = readLine();
                        int cap = (capStr != null && !capStr.trim().isEmpty()) ? Integer.parseInt(capStr.trim()) : 0;
                        System.out.print("Base Price: ");
                        String prStr = readLine();
                        double price = (prStr != null && !prStr.trim().isEmpty()) ? Double.parseDouble(prStr.trim()) : 0.0;
                        Section section = new Section(secId != null ? secId.trim() : "", stdId != null ? stdId.trim() : "", sName != null ? sName.trim() : "", stand, cap, price);
                        boolean ok = adminController.getSectionController().createSection(section);
                        System.out.println(ok ? ">> Section created successfully." : "[ERROR]: Section ID already exists.");
                        break;
                    case "4":
                        System.out.print("Section ID to update: ");
                        String upId = readLine();
                        System.out.print("New Name: ");
                        String nn = readLine();
                        System.out.print("New Stand (A/B/C/D): ");
                        String nsStr = readLine();
                        Stand newStand = (nsStr != null && !nsStr.trim().isEmpty())
                                ? Stand.valueOf(nsStr.trim().toUpperCase()) : Stand.A;
                        System.out.print("New Capacity: ");
                        String ncStr = readLine();
                        int nc = (ncStr != null && !ncStr.trim().isEmpty()) ? Integer.parseInt(ncStr.trim()) : 0;
                        System.out.print("New Price: ");
                        String npStr = readLine();
                        double np = (npStr != null && !npStr.trim().isEmpty()) ? Double.parseDouble(npStr.trim()) : 0.0;
                        boolean okUp = adminController.getSectionController().updateSection(upId != null ? upId.trim() : "", nn, newStand, nc, np);
                        System.out.println(okUp ? ">> Section updated." : "[ERROR]: Section not found.");
                        break;
                    case "5":
                        System.out.print("Section ID to remove: ");
                        String rmId = readLine();
                        boolean okRm = adminController.getSectionController().removeSection(rmId != null ? rmId.trim() : "");
                        System.out.println(okRm ? ">> Section removed." : "[ERROR]: Section not found.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void printSectionTable(List<Section> sections) {
        System.out.println("\n--- Sections (" + sections.size() + ") ---");
        System.out.printf("%-18s | %-12s | %-20s | %-6s | %-10s | %-14s%n",
                "SECTION ID", "STADIUM ID", "NAME", "STAND", "CAPACITY", "BASE PRICE");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (Section s : sections) {
            System.out.printf("%-18s | %-12s | %-20s | %-6s | %,10d | %,.0f VND%n",
                    s.getSectionId(), s.getStadiumId(), s.getName(),
                    s.getStand() != null ? s.getStand().name() : "-",
                    s.getCapacity(), s.getPrice());
        }
    }

    // ==========================================
    // 5. MANAGE SEAT
    // ==========================================
    public void manageSeat() throws IOException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Seat ---");
            System.out.println("1. View seats by match");
            System.out.println("2. Create single seat");
            System.out.println("3. Batch create seats by section (Hàng loạt)");
            System.out.println("4. Update single seat status");
            System.out.println("5. Batch update seat status by section (Hàng loạt)");
            System.out.println("6. Remove single seat");
            System.out.println("7. Batch remove seats by section (Hàng loạt)");
            System.out.println("8. Search seats");
            System.out.println("0. Back");
            System.out.print("Choose (0-8): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        System.out.print("Enter Match ID (e.g. M001): ");
                        String matchQuery = readLine();
                        List<Seat> mSeats = adminController.getSeatController().getSeatsByMatch(matchQuery != null ? matchQuery.trim() : "");
                        paginateSeatList(mSeats);
                        break;
                    case "2":
                        System.out.print("Seat ID (e.g. SEAT_99999): ");
                        String sId = readLine();
                        System.out.print("Match ID: ");
                        String mId = readLine();
                        System.out.print("Section ID: ");
                        String sectionId = readLine();
                        System.out.print("Row label (e.g. R1): ");
                        String rowLabel = readLine();
                        System.out.print("Seat number: ");
                        String sNumStr = readLine();
                        int seatNumber = (sNumStr != null && !sNumStr.trim().isEmpty()) ? Integer.parseInt(sNumStr.trim()) : 1;
                        Seat seat = new Seat(sId != null ? sId.trim() : "", mId != null ? mId.trim() : "", sectionId != null ? sectionId.trim() : "",
                                rowLabel != null ? rowLabel.trim() : "", seatNumber, SeatStatus.AVAILABLE, 1);
                        boolean ok = adminController.getSeatController().createSeat(seat);
                        System.out.println(ok ? ">> Seat created successfully." : "[ERROR]: Seat ID exists.");
                        break;
                    case "3":
                        System.out.println("\n--- Batch Create Seats by Section ---");
                        System.out.print("Match ID: ");
                        String bMatchId = readLine();
                        System.out.print("Section ID (e.g. SEC_STD_001_A): ");
                        String bSecId = readLine();
                        System.out.print("Stand (A/B/C/D): ");
                        String bStandStr = readLine();
                        Stand bStand = (bStandStr != null && !bStandStr.trim().isEmpty())
                                ? Stand.valueOf(bStandStr.trim().toUpperCase()) : Stand.A;
                        System.out.print("Quantity of seats to generate (e.g. 50): ");
                        String qtyStr = readLine();
                        int quantity = (qtyStr != null && !qtyStr.trim().isEmpty()) ? Integer.parseInt(qtyStr.trim()) : 0;
                        System.out.print("Default Price per seat in VND (e.g. 500000): ");
                        String prcStr = readLine();
                        double bPrice = (prcStr != null && !prcStr.trim().isEmpty()) ? Double.parseDouble(prcStr.trim()) : 500000.0;
                        int createdCount = adminController.getSeatController().createSeatsBatch(bMatchId, bSecId, bStand, quantity, bPrice);
                        System.out.println(">> [SUCCESS]: Batch generated and saved " + createdCount + " seats for Match " + bMatchId + " in Section " + bSecId + "!");
                        break;
                    case "4":
                        System.out.print("Seat ID to update: ");
                        String upId = readLine();
                        System.out.print("New Status (AVAILABLE/BOOKED/RESERVED/LOCKED): ");
                        String nsStr = readLine();
                        SeatStatus ns = (nsStr != null && !nsStr.trim().isEmpty())
                                ? SeatStatus.valueOf(nsStr.trim().toUpperCase()) : SeatStatus.AVAILABLE;
                        boolean okUp = adminController.getSeatController().updateSeat(upId != null ? upId.trim() : "", ns);
                        System.out.println(okUp ? ">> Seat status updated." : "[ERROR]: Seat not found.");
                        break;
                    case "5":
                        System.out.println("\n--- Batch Update Seat Status by Section ---");
                        System.out.print("Match ID: ");
                        String buMatch = readLine();
                        System.out.print("Section ID: ");
                        String buSec = readLine();
                        System.out.print("New Status (AVAILABLE/BOOKED/RESERVED/LOCKED): ");
                        String buStatStr = readLine();
                        SeatStatus buStatus = (buStatStr != null && !buStatStr.trim().isEmpty())
                                ? SeatStatus.valueOf(buStatStr.trim().toUpperCase()) : SeatStatus.AVAILABLE;
                        int updatedCount = adminController.getSeatController().updateSeatsStatusBySection(buMatch, buSec, buStatus);
                        System.out.println(">> [SUCCESS]: Batch updated " + updatedCount + " seats in Section " + buSec + " to status " + buStatus + "!");
                        break;
                    case "6":
                        System.out.print("Seat ID to remove: ");
                        String rmId = readLine();
                        boolean okRm = adminController.getSeatController().removeSeat(rmId != null ? rmId.trim() : "");
                        System.out.println(okRm ? ">> Seat removed." : "[ERROR]: Seat not found.");
                        break;
                    case "7":
                        System.out.println("\n--- Batch Remove Seats by Section ---");
                        System.out.print("Match ID: ");
                        String brMatch = readLine();
                        System.out.print("Section ID: ");
                        String brSec = readLine();
                        System.out.print("Are you sure you want to delete all seats in this section? (y/n): ");
                        String confirmRm = readLine();
                        if (confirmRm != null && (confirmRm.trim().equalsIgnoreCase("y") || confirmRm.trim().equalsIgnoreCase("yes"))) {
                            int deletedCount = adminController.getSeatController().removeSeatsBySection(brMatch, brSec);
                            System.out.println(">> [SUCCESS]: Batch deleted " + deletedCount + " seats from Match " + brMatch + ", Section " + brSec + "!");
                        } else {
                            System.out.println("Batch delete cancelled.");
                        }
                        break;
                    case "8":
                        System.out.print("Search seat keyword: ");
                        String q = readLine();
                        List<Seat> seats = adminController.getSeatController().searchSeats(q);
                        paginateSeatList(seats);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void paginateSeatList(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats found.");
            return;
        }
        System.out.println("Found " + seats.size() + " seat(s):");
        int pageSize = 25;
        int cur = 0;
        int total = seats.size();
        System.out.printf("%-12s | %-10s | %-12s | %-10s | %-12s%n",
                "SEAT ID", "MATCH ID", "SECTION", "STAND", "STATUS");
        System.out.println("-------------------------------------------------------------");
        while (cur < total) {
            int end = Math.min(cur + pageSize, total);
            for (int i = cur; i < end; i++) {
                Seat s = seats.get(i);
                System.out.printf("%-12s | %-10s | %-12s | %-10s | %-12s%n",
                        s.getSeatId(),
                        s.getMatchId(),
                        s.getSectionId() != null ? s.getSectionId() : "N/A",
                        s.getStand() != null ? s.getStand().name() : "N/A",
                        s.getStatus() != null ? s.getStatus().name() : "N/A");
            }
            cur = end;
            System.out.println("-------------------------------------------------------------");
            System.out.println("Showing " + cur + "/" + total + " seats.");
            if (cur < total) {
                System.out.print("Do you want to view more seats? (y/n, or 'all' to show all remaining): ");
                String ans = readLine();
                if (ans == null || ans.trim().equalsIgnoreCase("n") || ans.trim().equalsIgnoreCase("no")) {
                    break;
                } else if (ans.trim().equalsIgnoreCase("all") || ans.trim().equalsIgnoreCase("a")) {
                    pageSize = total - cur;
                } else if (!ans.trim().equalsIgnoreCase("y") && !ans.trim().equalsIgnoreCase("yes")) {
                    break;
                }
                System.out.println("-------------------------------------------------------------");
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
            System.out.println("1. View tickets by match");
            System.out.println("2. Create single ticket");
            System.out.println("3. Batch generate tickets for match/section (Hàng loạt)");
            System.out.println("4. Update single ticket price");
            System.out.println("5. Batch update ticket prices by section (Hàng loạt)");
            System.out.println("6. Remove single ticket");
            System.out.println("7. Batch remove tickets by section (Hàng loạt)");
            System.out.println("0. Back");
            System.out.print("Choose (0-7): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        System.out.print("Enter Match ID (e.g. M001): ");
                        String tMatch = readLine();
                        List<Ticket> tickets = adminController.getTicketController().getTicketsByMatch(tMatch != null ? tMatch.trim() : "");
                        paginateTicketList(tickets);
                        break;
                    case "2":
                        System.out.print("Ticket ID (e.g. TCK-001): ");
                        String tId = readLine();
                        System.out.print("Match ID: ");
                        String mId = readLine();
                        System.out.print("Seat ID: ");
                        String sId = readLine();
                        System.out.print("Price: ");
                        String pStr = readLine();
                        double price = (pStr != null && !pStr.trim().isEmpty()) ? Double.parseDouble(pStr.trim()) : 0.0;
                        Ticket ticket = new Ticket(tId != null ? tId.trim() : "", mId != null ? mId.trim() : "", sId != null ? sId.trim() : "", price, TicketStatus.AVAILABLE);
                        boolean ok = adminController.getTicketController().createTicket(ticket);
                        System.out.println(ok ? ">> Ticket created successfully." : "[ERROR]: Ticket ID exists.");
                        break;
                    case "3":
                        System.out.println("\n--- Batch Generate Tickets for Match / Section ---");
                        System.out.print("Match ID: ");
                        String bgMatch = readLine();
                        System.out.print("Section ID (leave blank to generate for all sections in match): ");
                        String bgSec = readLine();
                        System.out.print("Override Price in VND (or 0 to use seat default price): ");
                        String bgPrStr = readLine();
                        double bgPrice = (bgPrStr != null && !bgPrStr.trim().isEmpty()) ? Double.parseDouble(bgPrStr.trim()) : 0.0;
                        int genCount = adminController.getTicketController().generateTicketsForMatch(
                                bgMatch, bgSec, bgPrice, adminController.getSeatController());
                        System.out.println(">> [SUCCESS]: Generated " + genCount + " new tickets for Match " + bgMatch + "!");
                        break;
                    case "4":
                        System.out.print("Ticket ID to update: ");
                        String upId = readLine();
                        System.out.print("New Price: ");
                        String npStr = readLine();
                        double np = (npStr != null && !npStr.trim().isEmpty()) ? Double.parseDouble(npStr.trim()) : 0.0;
                        boolean okUp = adminController.getTicketController().updateTicketPrice(upId != null ? upId.trim() : "", np);
                        System.out.println(okUp ? ">> Ticket price updated." : "[ERROR]: Ticket not found.");
                        break;
                    case "5":
                        System.out.println("\n--- Batch Update Ticket Prices by Section ---");
                        System.out.print("Match ID: ");
                        String bupMatch = readLine();
                        System.out.print("Section ID: ");
                        String bupSec = readLine();
                        System.out.print("New Price for all tickets in this section: ");
                        String bupPrStr = readLine();
                        double bupPrice = (bupPrStr != null && !bupPrStr.trim().isEmpty()) ? Double.parseDouble(bupPrStr.trim()) : 0.0;
                        int upCount = adminController.getTicketController().updateTicketPricesBySection(
                                bupMatch, bupSec, bupPrice, adminController.getSeatController());
                        System.out.println(">> [SUCCESS]: Updated prices for " + upCount + " tickets in Section " + bupSec + "!");
                        break;
                    case "6":
                        System.out.print("Ticket ID to remove: ");
                        String rmId = readLine();
                        boolean okRm = adminController.getTicketController().removeTicket(rmId != null ? rmId.trim() : "");
                        System.out.println(okRm ? ">> Ticket removed." : "[ERROR]: Ticket not found.");
                        break;
                    case "7":
                        System.out.println("\n--- Batch Remove Tickets by Section ---");
                        System.out.print("Match ID: ");
                        String brmMatch = readLine();
                        System.out.print("Section ID: ");
                        String brmSec = readLine();
                        System.out.print("Are you sure you want to delete all tickets for this section? (y/n): ");
                        String confirmTckRm = readLine();
                        if (confirmTckRm != null && (confirmTckRm.trim().equalsIgnoreCase("y") || confirmTckRm.trim().equalsIgnoreCase("yes"))) {
                            int delTckCount = adminController.getTicketController().removeTicketsBySection(
                                    brmMatch, brmSec, adminController.getSeatController());
                            System.out.println(">> [SUCCESS]: Batch deleted " + delTckCount + " tickets from Match " + brmMatch + ", Section " + brmSec + "!");
                        } else {
                            System.out.println("Batch ticket deletion cancelled.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public void paginateTicketList(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        System.out.println("Found " + tickets.size() + " ticket(s):");
        int pageSize = 25;
        int cur = 0;
        int total = tickets.size();
        System.out.printf("%-16s | %-10s | %-12s | %-14s | %-12s%n",
                "TICKET ID", "MATCH ID", "SEAT ID", "PRICE", "STATUS");
        System.out.println("-------------------------------------------------------------------------");
        while (cur < total) {
            int end = Math.min(cur + pageSize, total);
            for (int i = cur; i < end; i++) {
                Ticket t = tickets.get(i);
                System.out.printf("%-16s | %-10s | %-12s | %,10.0f VND | %-12s%n",
                        t.getTicketId(),
                        t.getMatchId(),
                        t.getSeatId() != null ? t.getSeatId() : "N/A",
                        t.getPrice(),
                        t.getStatus() != null ? t.getStatus().name() : "N/A");
            }
            cur = end;
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Showing " + cur + "/" + total + " tickets.");
            if (cur < total) {
                System.out.print("Do you want to view more tickets? (y/n, or 'all' to show all remaining): ");
                String ans = readLine();
                if (ans == null || ans.trim().equalsIgnoreCase("n") || ans.trim().equalsIgnoreCase("no")) {
                    break;
                } else if (ans.trim().equalsIgnoreCase("all") || ans.trim().equalsIgnoreCase("a")) {
                    pageSize = total - cur;
                } else if (!ans.trim().equalsIgnoreCase("y") && !ans.trim().equalsIgnoreCase("yes")) {
                    break;
                }
                System.out.println("-------------------------------------------------------------------------");
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
            System.out.println("1. View all vouchers");
            System.out.println("2. Create voucher");
            System.out.println("3. Update voucher");
            System.out.println("4. Remove voucher");
            System.out.println("0. Back");
            System.out.print("Choose (0-4): ");
            String choice = readLine();
            if (choice == null || "0".equals(choice.trim())) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        List<Voucher> vouchers = adminController.getVoucherController().getAllVouchers();
                        System.out.println("\n--- Vouchers (" + vouchers.size() + ") ---");
                        System.out.printf("%-16s | %-16s | %-18s | %-10s%n", "CODE", "DISCOUNT", "MIN ORDER", "STATUS");
                        System.out.println("----------------------------------------------------------------------");
                        for (Voucher v : vouchers) {
                            String dStr = v.getDiscountPercent() > 0
                                    ? String.format("%.0f%% OFF", v.getDiscountPercent())
                                    : String.format("%,.0f VND OFF", v.getDiscountAmount());
                            System.out.printf("%-16s | %-16s | %,14.0f VND | %-10s%n",
                                    v.getCode(), dStr, v.getMinimumOrderAmount(), v.isUsed() ? "USED" : "AVAILABLE");
                        }
                        break;
                    case "2":
                        System.out.print("Voucher code (e.g. PROMO50): ");
                        String code = readLine();
                        System.out.print("Discount percent (0-100, 0 if fixed amount): ");
                        String dPctStr = readLine();
                        double dPct = (dPctStr != null && !dPctStr.trim().isEmpty()) ? Double.parseDouble(dPctStr.trim()) : 0.0;
                        System.out.print("Discount amount (0 if percent): ");
                        String dAmtStr = readLine();
                        double dAmt = (dAmtStr != null && !dAmtStr.trim().isEmpty()) ? Double.parseDouble(dAmtStr.trim()) : 0.0;
                        System.out.print("Minimum order amount in VND (blank for 200000): ");
                        String minimumInput = readLine();
                        double minimumOrderAmount = minimumInput == null || minimumInput.trim().isEmpty()
                                ? 200000.0 : Double.parseDouble(minimumInput.trim());
                        Voucher voucher = new Voucher(code != null ? code.trim() : "", dPct, dAmt, minimumOrderAmount, false);
                        boolean ok = adminController.getVoucherController().createVoucher(voucher);
                        System.out.println(ok ? ">> Voucher created successfully." : "[ERROR]: Voucher code exists.");
                        break;
                    case "3":
                        System.out.print("Voucher code to update: ");
                        String upCode = readLine();
                        System.out.print("New discount percent: ");
                        String npStr = readLine();
                        double np = (npStr != null && !npStr.trim().isEmpty()) ? Double.parseDouble(npStr.trim()) : 0.0;
                        System.out.print("New discount amount: ");
                        String naStr = readLine();
                        double na = (naStr != null && !naStr.trim().isEmpty()) ? Double.parseDouble(naStr.trim()) : 0.0;
                        System.out.print("New minimum order amount in VND: ");
                        String moaStr = readLine();
                        double newMinimumOrderAmount = (moaStr != null && !moaStr.trim().isEmpty()) ? Double.parseDouble(moaStr.trim()) : 200000.0;
                        boolean okUp = adminController.getVoucherController().updateVoucher(
                                upCode != null ? upCode.trim() : "", np, na, newMinimumOrderAmount);
                        System.out.println(okUp ? ">> Voucher updated." : "[ERROR]: Voucher not found.");
                        break;
                    case "4":
                        System.out.print("Voucher code to remove: ");
                        String rmCode = readLine();
                        boolean okRm = adminController.getVoucherController().removeVoucher(rmCode != null ? rmCode.trim() : "");
                        System.out.println(okRm ? ">> Voucher removed." : "[ERROR]: Voucher not found.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
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
            try {
                switch (choice.trim()) {
                    case "1":
                        System.out.print("Enter number of rows to generate (min 10000): ");
                        String rStr = readLine();
                        if (rStr == null || rStr.trim().isEmpty()) {
                            System.out.println("Operation cancelled.");
                            break;
                        }
                        int rows = Integer.parseInt(rStr.trim());
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
                        String tStr = readLine();
                        if (tStr == null || tStr.trim().isEmpty()) {
                            break;
                        }
                        int threads = Integer.parseInt(tStr.trim());
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
                        String reqStr = readLine();
                        int requests = (reqStr != null && !reqStr.trim().isEmpty()) ? Integer.parseInt(reqStr.trim()) : 50;
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
                        if (txns == null || txns.isEmpty()) {
                            System.out.println("No transactions found.");
                            break;
                        }
                        System.out.println("\nTotal Transactions: " + txns.size());
                        int pageSize = 25;
                        int cur = 0;
                        int totalT = txns.size();
                        System.out.printf("%-16s | %-12s | %-10s | %15s | %-12s%n",
                                "TRANSACTION ID", "FAN ID", "MATCH", "AMOUNT", "STATUS");
                        System.out.println("-------------------------------------------------------------------------");
                        while (cur < totalT) {
                            int end = Math.min(cur + pageSize, totalT);
                            for (int i = cur; i < end; i++) {
                                Transaction t = txns.get(i);
                                System.out.printf("%-16s | %-12s | %-10s | %15s | %-12s%n",
                                        t.getTransactionId(),
                                        t.getFanId() != null ? t.getFanId() : "N/A",
                                        t.getMatchId() != null ? t.getMatchId() : "N/A",
                                        String.format("%,.0f VND", t.getTotalAmount()),
                                        t.getStatus() != null ? t.getStatus().name() : "N/A");
                            }
                            cur = end;
                            System.out.println("-------------------------------------------------------------------------");
                            System.out.println("Showing " + cur + "/" + totalT + " transactions.");
                            if (cur < totalT) {
                                System.out.print("Do you want to view more? (y/n, or 'all' to show all remaining): ");
                                String ans = readLine();
                                if (ans == null || ans.trim().equalsIgnoreCase("n") || ans.trim().equalsIgnoreCase("no")) {
                                    break;
                                } else if (ans.trim().equalsIgnoreCase("all") || ans.trim().equalsIgnoreCase("a")) {
                                    pageSize = totalT - cur;
                                } else if (!ans.trim().equalsIgnoreCase("y") && !ans.trim().equalsIgnoreCase("yes")) {
                                    break;
                                }
                                System.out.println("-------------------------------------------------------------------------");
                            }
                        }
                        break;
                    case "8":
                        double revenue = adminController.getSimulatorController().calculateRevenue();
                        System.out.printf("%nTotal Recorded Revenue: %,.0f VND%n", revenue);
                        break;
                    default:
                        System.out.println("Invalid choice. Please select from 0 to 8.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
