package com.ticketbooking.view;

import com.ticketbooking.controller.TransactionController;
import com.ticketbooking.model.Match;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.Voucher;
import com.ticketbooking.model.enums.PaymentMethod;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SeatType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TransactionView {
    private TransactionController transactionController;
    private Scanner scanner;

    public TransactionView(TransactionController transactionController) {
        this(transactionController, new Scanner(System.in));
    }

    public TransactionView(TransactionController transactionController, Scanner scanner) {
        if (transactionController == null || scanner == null) {
            throw new IllegalArgumentException("Transaction controller and scanner must not be null.");
        }
        this.transactionController = transactionController;
        this.scanner = scanner;
    }

    public TransactionController getTransactionController() {
        return transactionController;
    }

    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run(String fanId) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Ticket transactions ===");
            System.out.println("1. View and filter matches");
            System.out.println("2. View seat map and prices");
            System.out.println("3. Create transaction");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        showMatchListAndFilter();
                        break;
                    case "2":
                        showStandPriceAndSeatMap();
                        break;
                    case "3":
                        showTransactionForm(fanId);
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IOException e) {
                System.out.println("Unable to load transaction data: " + e.getMessage());
            }
        }
    }

    public void showMatchListAndFilter() throws IOException {
        while (true) {
            System.out.println("\n==========================================================================");
            System.out.println("                    MATCH SCHEDULE & TICKETING STATUS                     ");
            System.out.println("==========================================================================");

            List<Match> matches = transactionController.getAllMatches();
            renderMatchTable(matches);

            System.out.println("--------------------------------------------------------------------------");
            System.out.println("SEARCH & FILTER OPTIONS:");
            System.out.println("1. Filter by Match Date (Format: YYYY-MM-DD)");
            System.out.println("2. Filter by Stadium Name");
            System.out.println("3. Filter by Team Name");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option (0-3): ");

            String choice = scanner.nextLine().trim();
            if ("0".equals(choice)) {
                break;
            }

            switch (choice) {
                case "1":
                    System.out.print("Enter date to search (e.g. 2026-11-15): ");
                    String dateQuery = scanner.nextLine().trim();
                    List<Match> dateMatches = transactionController.filterMatches(dateQuery, null, null);
                    System.out.println("\n>> FILTER RESULTS FOR DATE [" + dateQuery + "]:");
                    renderMatchTable(dateMatches);
                    pause();
                    break;
                case "2":
                    System.out.print("Enter stadium name (e.g. Hang Day, My Dinh): ");
                    String stadiumQuery = scanner.nextLine().trim();
                    List<Match> stadiumMatches = transactionController.filterMatches(null, stadiumQuery, null);
                    System.out.println("\n>> FILTER RESULTS FOR STADIUM [" + stadiumQuery + "]:");
                    renderMatchTable(stadiumMatches);
                    pause();
                    break;
                case "3":
                    System.out.print("Enter team name (e.g. Ha Noi, Nam Dinh): ");
                    String teamQuery = scanner.nextLine().trim();
                    List<Match> teamMatches = transactionController.filterMatches(null, null, teamQuery);
                    System.out.println("\n>> FILTER RESULTS FOR TEAM [" + teamQuery + "]:");
                    renderMatchTable(teamMatches);
                    pause();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void renderMatchTable(List<Match> matches) throws IOException {
        if (matches == null || matches.isEmpty()) {
            System.out.println("No matching matches found!");
            return;
        }

        System.out.println("+----------+--------------------------+------------+-------+--------------------+------------+");
        System.out.printf("| %-8s | %-24s | %-10s | %-5s | %-18s | %-10s |%n",
                "MATCH ID", "FIXTURE", "DATE", "TIME", "STADIUM", "STATUS");
        System.out.println("+----------+--------------------------+------------+-------+--------------------+------------+");

        for (Match m : matches) {
            boolean soldOut = transactionController.isMatchSoldOut(m.getMatchId());
            String status = soldOut ? "[SOLD OUT]" : "AVAILABLE";
            System.out.printf("| %-8s | %-24s | %-10s | %-5s | %-18s | %-10s |%n",
                    m.getMatchId(),
                    m.getTitle(),
                    (m.getMatchDate() != null ? m.getMatchDate().toString() : "N/A"),
                    m.getMatchTime(),
                    m.getStadium(),
                    status);
            System.out.println("+----------+--------------------------+------------+-------+--------------------+------------+");
        }
    }

    public void showStandPriceAndSeatMap() throws IOException {
        System.out.println("\n==========================================================================");
        System.out.println("                  STAND PRICING TABLE & VISUAL SEAT MAP                   ");
        System.out.println("==========================================================================");

        renderPricingTable();

        System.out.print("\nEnter Match ID to inspect seat map (e.g. M001, M002): ");
        String matchId = scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("Match ID cannot be empty!");
            return;
        }

        Match match = transactionController.getMatchById(matchId);
        if (match != null) {
            System.out.println(">> Match: " + match.getTitle() + " | Stadium: " + match.getStadium() + " | Date: " + match.getMatchDate());
        }

        List<Seat> allSeats = transactionController.getAllSeatsForMatch(matchId);
        if (allSeats.isEmpty()) {
            System.out.println("No seat data available for match " + matchId);
            return;
        }

        renderSeatMap(allSeats);
        pause();
    }

    public void renderPricingTable() {
        System.out.println("+----------+-----------------------+---------------+--------------------------+");
        System.out.printf("| %-8s | %-21s | %-13s | %-24s |%n", "ZONE", "STAND AREA", "UNIT PRICE", "FEATURES");
        System.out.println("+----------+-----------------------+---------------+--------------------------+");
        for (SeatType type : SeatType.values()) {
            System.out.printf("| %-8s | %-21s | %13s | %-24s |%n",
                    type.name(),
                    type.getStandName(),
                    String.format("%,.0f VND", type.getBasePrice()),
                    type.getDescription());
            System.out.println("+----------+-----------------------+---------------+--------------------------+");
        }
    }

    public void renderSeatMap(List<Seat> seats) {
        System.out.println("\n==========================================================================");
        System.out.println("                         VISUAL STADIUM SEAT MAP                          ");
        System.out.println("==========================================================================");
        System.out.println("                        [ FOOTBALL PITCH / FIELD ]                        ");
        System.out.println("--------------------------------------------------------------------------");

        for (SeatType type : SeatType.values()) {
            List<Seat> group = seats.stream()
                    .filter(s -> s.getSeatType() == type)
                    .collect(Collectors.toList());

            if (!group.isEmpty()) {
                System.out.println(">> [" + type.name() + " - " + type.getStandName() + "] (Price: " + String.format("%,.0f VND", type.getBasePrice()) + "):");
                StringBuilder sb = new StringBuilder("   ");
                for (Seat s : group) {
                    String tag = (s.getStatus() == SeatStatus.AVAILABLE) ? "[O]" : "[X]";
                    sb.append(String.format("%-8s:%s   ", s.getSeatId(), tag));
                }
                System.out.println(sb.toString());
                System.out.println("--------------------------------------------------------------------------");
            }
        }

        long availableCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.AVAILABLE).count();
        long bookedCount = seats.size() - availableCount;

        System.out.println("Legend:     [O] : Available Seat   |   [X] : Booked Seat");
        System.out.println("Statistics: Total: " + seats.size() + " seats | Available: " + availableCount + " | Booked: " + bookedCount);
        System.out.println("==========================================================================");
    }

    public void showTransactionForm(String fanId) throws IOException {
        System.out.println("\n=========================================================================================");
        System.out.println("                          ONLINE TICKET TRANSACTION (FAN)                               ");
        System.out.println("=========================================================================================");

        System.out.print("Enter Match ID for this transaction (e.g. M001): ");
        String matchId = scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("Match ID cannot be empty!");
            return;
        }

        Match match = transactionController.getMatchById(matchId);
        if (match != null) {
            System.out.println(">> Transaction for fixture: " + match.getTitle() + " at " + match.getStadium());
        }

        System.out.print("Select seat IDs (Max 4 tickets/transaction, comma-separated, e.g. VIP-01, B1): ");
        String seatInput = scanner.nextLine().trim();
        if (seatInput.isEmpty()) {
            System.out.println("Seat list cannot be empty!");
            return;
        }

        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));

        List<Seat> validatedSeats;
        try {
            validatedSeats = transactionController.validateAndGetSeats(matchId, seatIds);
        } catch (Exception e) {
            System.out.println("\n[TRANSACTION ERROR]: " + e.getMessage());
            return;
        }

        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.println("SELECTED TICKETS BREAKDOWN:");
        for (Seat s : validatedSeats) {
            System.out.printf(" - Seat: %-8s | Zone: %-10s | %-24s | Price: %s%n",
                    s.getSeatId(),
                    s.getSeatType().name(),
                    s.getSeatType().getStandName(),
                    String.format("%,.0f VND", s.getPrice()));
        }

        double subtotal = transactionController.calculateSubtotal(validatedSeats);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println(">> SUBTOTAL (" + validatedSeats.size() + " tickets): " + String.format("%,.0f VND", subtotal));
        System.out.println("-----------------------------------------------------------------------------------------");

        String appliedVoucherCode = "";
        double discountAmount = 0.0;
        double finalAmount = subtotal;

        System.out.print("Do you have a discount voucher? (y/n): ");
        String hasVoucher = scanner.nextLine().trim();
        if (hasVoucher.equalsIgnoreCase("y") || hasVoucher.equalsIgnoreCase("yes")) {
            while (true) {
                System.out.print("Enter voucher code (e.g. DISCOUNT10, GIAM50K, VIP100K) or type 'skip' to skip: ");
                String inputVoucher = scanner.nextLine().trim();
                if (inputVoucher.equalsIgnoreCase("skip") || inputVoucher.isEmpty()) {
                    break;
                }
                try {
                    Voucher voucher = transactionController.validateAndCheckVoucher(inputVoucher);
                    if (voucher != null) {
                        appliedVoucherCode = voucher.getCode();
                        discountAmount = voucher.calculateDiscount(subtotal);
                        finalAmount = Math.max(0, subtotal - discountAmount);
                        String discountTypeStr = voucher.getDiscountPercent() > 0 ?
                                String.format("%.0f%% OFF", voucher.getDiscountPercent()) :
                                String.format("%,.0f VND OFF", voucher.getDiscountAmount());
                        System.out.println(">> Voucher [" + appliedVoucherCode + "] (" + discountTypeStr + ") applied successfully!");
                        System.out.println(">> Discount: -" + String.format("%,.0f VND", discountAmount));
                        System.out.println(">> Total amount to pay: " + String.format("%,.0f VND", finalAmount));
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("[VOUCHER ERROR]: " + e.getMessage());
                    System.out.print("Would you like to try another voucher? (y/n): ");
                    String retry = scanner.nextLine().trim();
                    if (!retry.equalsIgnoreCase("y") && !retry.equalsIgnoreCase("yes")) {
                        break;
                    }
                }
            }
        }

        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.println("ONLINE PAYMENT GATEWAY (QR CODE):");
        System.out.println("Online transactions require payment via Bank QR Code transfer.");
        System.out.print("Press [Enter] to display QR Code for payment (or type '0' to cancel): ");
        String confirm = scanner.nextLine().trim();
        if ("0".equals(confirm)) {
            System.out.println("\n>> Transaction cancelled.");
            return;
        }

        showQrPaymentScreen(fanId, matchId, finalAmount);

        try {
            System.out.println("\nProcessing and finalizing your transaction...");
            Transaction transaction = transactionController.createTransaction(
                    fanId, matchId, seatIds, appliedVoucherCode, PaymentMethod.QR_CODE);

            printTransactionResult(transaction, subtotal);
        } catch (Exception e) {
            System.out.println("[TRANSACTION ERROR]: " + e.getMessage());
        }
    }

    public void showQrPaymentScreen(String fanId, String matchId, double amount) {
        System.out.println("\n=============================================================");
        System.out.println("             ONLINE QR PAYMENT GATEWAY - SELLER              ");
        System.out.println("=============================================================");
        System.out.println("  +---------------------------------------------------------+");
        System.out.println("  |  [###] [#] [###]   Bank:          MB BANK               |");
        System.out.println("  |  [# #] [ ] [# #]   Account No:    0987654321            |");
        System.out.println("  |  [###] [#] [###]   Account Name:  STADIUM TICKET        |");
        System.out.println("  |  [ ]   [#]   [ ]   Amount:        " + String.format("%-22s", String.format("%,.0f VND", amount)) + "|");
        System.out.println("  |  [###] [ ] [###]   Transfer Note: " + String.format("%-22s", fanId + " " + matchId) + "|");
        System.out.println("  |  [# #] [#] [# #]                                        |");
        System.out.println("  |  [###] [#] [###]   (Scan QR code on your Mobile Banking |");
        System.out.println("  |                    or E-Wallet App to pay)              |");
        System.out.println("  +---------------------------------------------------------+");
        System.out.println("  Payment details have been automatically dispatched to Seller.");
        System.out.println("-------------------------------------------------------------");
        System.out.print(">> Press [Enter] once you have completed the bank transfer: ");
        scanner.nextLine();
        System.out.println(">> Verifying transaction... [Success: Payment confirmed by Seller!]");
    }

    public void printTransactionResult(Transaction transaction, double subtotal) {
        System.out.println("\n==================================================");
        System.out.println("         SUCCESSFUL TRANSACTION RECEIPT           ");
        System.out.println("==================================================");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Fan ID:         " + transaction.getFanId());
        System.out.println("Match ID:       " + transaction.getMatchId());
        if (!transaction.getSeatIds().isEmpty()) {
            System.out.println("Seats:          " + String.join(", ", transaction.getSeatIds()));
        }
        System.out.println("Payment Method: Online Banking (QR Code)");
        System.out.println("Subtotal:       " + String.format("%,.0f VND", subtotal));
        if (transaction.getDiscountAmount() > 0) {
            System.out.println("Voucher:        " + transaction.getVoucherCode() + " (-" + String.format("%,.0f VND", transaction.getDiscountAmount()) + ")");
        }
        System.out.println("Final Paid:     " + String.format("%,.0f VND", transaction.getTotalAmount()));
        System.out.println("Status:         CONFIRMED");
        String formattedDate = transaction.getTransactionDate() != null ?
                transaction.getTransactionDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "N/A";
        System.out.println("Date & Time:    " + formattedDate);
        System.out.println("==================================================\n");
    }

    public void printTransactionResult(Transaction transaction) {
        printTransactionResult(transaction,
                transaction.getTotalAmount() + transaction.getDiscountAmount());
    }

    public void showAvailableSeats() throws IOException {
        showStandPriceAndSeatMap();
    }

    public void pause() {
        System.out.print("\nPress [Enter] to continue...");
        scanner.nextLine();
    }
}
