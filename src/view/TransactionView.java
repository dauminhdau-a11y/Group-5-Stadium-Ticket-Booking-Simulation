package view;

import controller.TransactionController;
import model.Match;
import model.Seat;
import model.Transaction;
import model.Voucher;
import model.enums.PaymentMethod;
import model.enums.Stand;
import model.enums.TransactionStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TransactionView {
    private TransactionController transactionController;
    private Scanner scanner;
    private SeatMapView seatMapView;

    public TransactionView(TransactionController transactionController) {
        this(transactionController, new Scanner(System.in));
    }

    public TransactionView(TransactionController transactionController, Scanner scanner) {
        if (transactionController == null || scanner == null) {
            throw new IllegalArgumentException("Transaction controller and scanner must not be null.");
        }
        this.transactionController = transactionController;
        this.scanner = scanner;
        this.seatMapView = new SeatMapView();
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

        renderSeatMap(matchId, allSeats);
        System.out.print("Do you want to view all seats in full detail (without truncation)? (y/n, or 'all'): ");
        String opt = scanner.nextLine().trim();
        if (opt.equalsIgnoreCase("y") || opt.equalsIgnoreCase("yes") || opt.equalsIgnoreCase("all")) {
            seatMapView.displaySeatMap(matchId, allSeats, true);
        }
        pause();
    }

    public void renderPricingTable() {
        System.out.println("+----------+-----------------------+---------------+--------------------------+");
        System.out.printf("| %-8s | %-21s | %-13s | %-24s |%n", "STAND", "STAND AREA", "UNIT PRICE", "FEATURES");
        System.out.println("+----------+-----------------------+---------------+--------------------------+");
        for (Stand stand : Stand.values()) {
            String position;
            switch (stand) {
                case A: position = "South"; break;
                case B: position = "North"; break;
                case C: position = "West"; break;
                default: position = "East"; break;
            }
            System.out.printf("| %-8s | %-21s | %13s | %-24s |%n",
                    stand.name(), "Stand " + stand.name() + " (" + position + ")",
                    "Per section", "All seats in this stand");
            System.out.println("+----------+-----------------------+---------------+--------------------------+");
        }
    }

    public void renderSeatMap(String matchId, List<Seat> seats) {
        seatMapView.displaySeatMap(matchId, seats);
    }

    public void renderSeatMap(List<Seat> seats) {
        String matchId = (seats != null && !seats.isEmpty() && seats.get(0).getMatchId() != null)
                ? seats.get(0).getMatchId()
                : "MATCH";
        renderSeatMap(matchId, seats);
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

        System.out.print("Select seat IDs (Max 4 tickets/transaction, comma-separated, e.g. SEAT_00001, SEAT_00002): ");
        String seatInput = scanner.nextLine().trim();
        if (seatInput.isEmpty()) {
            System.out.println("Seat list cannot be empty!");
            return;
        }

        List<String> seatIds = Arrays.asList(seatInput.split("\\s*,\\s*"));

        List<Seat> validatedSeats;
        try {
            validatedSeats = transactionController.holdSeatsForPayment(fanId, matchId, seatIds);
        } catch (Exception e) {
            System.out.println("\n[TRANSACTION ERROR]: " + e.getMessage());
            return;
        }

        System.out.println("\n>> [HOLD SUCCESSFUL]: The system has RESERVED " + validatedSeats.size()
                + " seat(s) for 10 MINUTES (until "
                + java.time.LocalTime.now().plusMinutes(10).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
                + ")!");
        System.out.println(">> Notice: During this time, other customers cannot select these seats (marked as LOCKED).");

        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.println("SELECTED TICKETS BREAKDOWN (HOLDING FOR 10 MINUTES):");
        for (Seat s : validatedSeats) {
            System.out.printf(" - Seat: %-8s | Stand: %-10s | %-24s | Price: %s%n",
                    s.getSeatId(),
                    s.getStand() != null ? "Stand " + s.getStand().name() : "N/A",
                    s.getStand() != null ? "Section " + s.getSectionId() : "",
                    String.format("%,.0f VND", transactionController.getTicketPrice(s)));
        }

        double subtotal = transactionController.calculateSubtotal(validatedSeats);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println(">> SUBTOTAL (" + validatedSeats.size() + " tickets): " + String.format("%,.0f VND", subtotal));
        System.out.println("-----------------------------------------------------------------------------------------");

        String appliedVoucherCode = "";
        double discountAmount = 0.0;
        double finalAmount = subtotal;

        List<Voucher> applicableVouchers = transactionController.getApplicableVouchers(fanId, subtotal);
        if (applicableVouchers.isEmpty()) {
            System.out.println("No voucher is applicable to this order (or you have already used eligible vouchers).");
        } else {
            System.out.println("\nVouchers applicable to this order (choose at most one, each voucher can only be used once per account):");
            for (Voucher voucher : applicableVouchers) {
                String discountType = voucher.getDiscountPercent() > 0
                        ? String.format("%.0f%% OFF", voucher.getDiscountPercent())
                        : String.format("%,.0f VND OFF", voucher.getDiscountAmount());
                System.out.printf(" - %-16s | %-16s | Minimum order: %,.0f VND%n",
                        voucher.getCode(), discountType, voucher.getMinimumOrderAmount());
            }

            System.out.print("Would you like to apply a voucher? (y/n): ");
            String hasVoucher = scanner.nextLine().trim();
            if (hasVoucher.equalsIgnoreCase("y") || hasVoucher.equalsIgnoreCase("yes")) {
                while (true) {
                    System.out.print("Enter one voucher code or type 'skip' to skip: ");
                    String inputVoucher = scanner.nextLine().trim();
                    if (inputVoucher.equalsIgnoreCase("skip") || inputVoucher.isEmpty()) {
                        break;
                    }
                    try {
                        Voucher voucher = transactionController.validateAndCheckVoucher(fanId, inputVoucher, subtotal);
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
        }

        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.println("SELECT PAYMENT METHOD (HYBRID PAYMENT SYSTEM):");
        System.out.println("  1. Bank Transfer (VietQR)         - Manual transfer via Bank App (Staff verifies & confirms)");
        System.out.println("  2. Instant Gateway (Credit/Debit) - Auto-confirmed immediately by Payment Gateway");
        System.out.println("  0. Cancel and unlock seats");
        System.out.print("Choose payment method (0-2): ");
        String methodChoice = scanner.nextLine().trim();

        if ("0".equals(methodChoice)) {
            transactionController.cancelHoldSeats(fanId, seatIds);
            System.out.println("\n>> Transaction cancelled. Selected seats have been released back to AVAILABLE.");
            return;
        }

        PaymentMethod selectedMethod = PaymentMethod.QR_CODE;
        boolean autoConfirm = false;

        if ("1".equals(methodChoice)) {
            selectedMethod = PaymentMethod.QR_CODE;
            int qrResult = showQrPaymentScreen(fanId, matchId, finalAmount);
            if (qrResult == 0) {
                transactionController.cancelHoldSeats(fanId, seatIds);
                System.out.println("\n>> Payment cancelled at QR gateway. Selected seats have been unlocked and returned to AVAILABLE.");
                return;
            } else if (qrResult == 2) {
                autoConfirm = true;
            } else {
                autoConfirm = false;
            }
        } else if ("2".equals(methodChoice)) {
            selectedMethod = PaymentMethod.CREDIT_CARD;
            boolean cardPaid = showInstantGatewayPaymentScreen(fanId, matchId, finalAmount);
            if (!cardPaid) {
                transactionController.cancelHoldSeats(fanId, seatIds);
                System.out.println("\n>> Payment cancelled at Instant Gateway. Selected seats have been unlocked and returned to AVAILABLE.");
                return;
            }
            autoConfirm = true;
        } else {
            System.out.println("[ERROR]: Invalid payment method choice. Transaction cancelled.");
            transactionController.cancelHoldSeats(fanId, seatIds);
            return;
        }

        try {
            System.out.println("\nProcessing and finalizing your transaction...");
            Transaction transaction = transactionController.createTransaction(
                    fanId, matchId, seatIds, appliedVoucherCode, selectedMethod, autoConfirm);

            printTransactionResult(transaction, subtotal);
        } catch (Exception e) {
            System.out.println("[TRANSACTION ERROR]: " + e.getMessage());
            transactionController.cancelHoldSeats(fanId, seatIds);
        }
    }

    public int showQrPaymentScreen(String fanId, String matchId, double amount) {
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
        System.out.println("  [Tip]: Type 'auto' to simulate instant Bank IPN/Webhook auto-approval.");
        System.out.println("-------------------------------------------------------------");
        System.out.print(">> Press [Enter] once completed (or 'auto' for auto-IPN, '0' to cancel): ");
        String confirm = scanner.nextLine().trim();
        if ("0".equals(confirm)) {
            return 0;
        }
        if ("auto".equalsIgnoreCase(confirm)) {
            System.out.println(">> [WEBHOOK IPN]: Bank transfer verified automatically by Webhook gateway!");
            return 2;
        }
        System.out.println(">> Payment submitted. The booking is waiting for staff confirmation.");
        return 1;
    }

    public boolean showInstantGatewayPaymentScreen(String fanId, String matchId, double amount) {
        System.out.println("\n=============================================================");
        System.out.println("        INSTANT PAYMENT GATEWAY (CREDIT CARD / E-WALLET)     ");
        System.out.println("=============================================================");
        System.out.println("  Match ID:     " + matchId);
        System.out.println("  Fan ID:       " + fanId);
        System.out.println("  Total Amount: " + String.format("%,.0f VND", amount));
        System.out.println("-------------------------------------------------------------");
        System.out.println("  Simulating Instant Payment Gateway (Visa / Master / E-Wallet)...");
        System.out.print(">> Enter Cardholder / Account Name (or '0' to cancel): ");
        String cardHolder = scanner.nextLine().trim();
        if ("0".equals(cardHolder) || cardHolder.isEmpty()) {
            return false;
        }
        System.out.print(">> Enter Card / Account Number (e.g. 4111222233334444): ");
        String cardNum = scanner.nextLine().trim();
        if ("0".equals(cardNum) || cardNum.isEmpty()) {
            return false;
        }
        System.out.println(">> Contacting Payment Gateway for authorization...");
        System.out.println(">> [SUCCESS]: Payment authorized and captured instantly!");
        return true;
    }

    public void printTransactionResult(Transaction transaction, double subtotal) {
        System.out.println("\n==================================================");
        System.out.println("                BOOKING RECEIPT                   ");
        System.out.println("==================================================");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Fan ID:         " + transaction.getFanId());
        System.out.println("Match ID:       " + transaction.getMatchId());
        if (!transaction.getSeatIds().isEmpty()) {
            System.out.println("Seats:          " + String.join(", ", transaction.getSeatIds()));
        }
        System.out.println("Payment Method: " + (transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().name() : "N/A"));
        System.out.println("Subtotal:       " + String.format("%,.0f VND", subtotal));
        if (transaction.getDiscountAmount() > 0) {
            System.out.println("Voucher:        " + transaction.getVoucherCode() + " (-" + String.format("%,.0f VND", transaction.getDiscountAmount()) + ")");
        }
        System.out.println("Final Paid:     " + String.format("%,.0f VND", transaction.getTotalAmount()));
        if (transaction.getStatus() == TransactionStatus.CONFIRMED || transaction.getStatus() == TransactionStatus.SUCCESS) {
            System.out.println("Status:         CONFIRMED (TICKETS ISSUED & READY)");
            System.out.println(">> Your booking has been confirmed automatically!");
            System.out.println(">> Confirmation notification dispatched to your account.");
        } else {
            System.out.println("Status:         PENDING STAFF CONFIRMATION");
            System.out.println(">> Seats are reserved while staff verifies the payment transfer.");
        }
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
