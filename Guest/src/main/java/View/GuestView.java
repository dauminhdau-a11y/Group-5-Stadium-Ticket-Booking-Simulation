package View;

import Controller.GuestController;
import model.*;
import java.util.*;

public class GuestView {
    private final GuestController controller;
    private final Scanner scanner;
    private Fan currentFan = null; // Quan ly session dang nhap

    public GuestView(GuestController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("||       STADIUM TICKET BOOKING - GUEST MODULE       ||");
            if (currentFan != null) {
                System.out.printf("|| Dang dang nhap: %-33s ||\n", currentFan.getFullName() + " (" + currentFan.getUsername() + ")");
            } else {
                System.out.println("|| Vai tro: KHACH VANG LAI (GUEST)                   ||");
            }
            System.out.println("=======================================================");
            System.out.println("|| 1. Register (Dang ky tai khoan Fan)               ||");
            if (currentFan == null) {
                System.out.println("|| 2. Login (Dang nhap he thong)                     ||");
            } else {
                System.out.println("|| 2. Xem & Cap nhat Profile / Dang xuat             ||");
            }
            System.out.println("|| 3. Search match (Tim kiem tran dau theo tu khoa)  ||");
            System.out.println("|| 4. Filter match (Loc tran dau theo tieu chi)      ||");
            System.out.println("|| 5. View match detail (Xem chi tiet tran dau)      ||");
            System.out.println("|| 0. Thoat                                          ||");
            System.out.println("=======================================================");
            System.out.print(">> Chon chuc nang: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleRegister();
                    break;
                case "2":
                    if (currentFan == null) {
                        handleLogin();
                    } else {
                        handleProfileOrLogout();
                    }
                    break;
                case "3":
                    handleSearchMatch();
                    break;
                case "4":
                    handleFilterMatch();
                    break;
                case "5":
                    handleViewMatchDetail();
                    break;
                case "0":
                    System.out.println("Cam on ban da su dung he thong Stadium Ticket Booking!");
                    return;
                default:
                    System.out.println("[!] Lua chon khong hop le! Vui long chon tu 0 den 5.");
            }
        }
    }

    // --- USE CASE 1: Register ---
    private void handleRegister() {
        System.out.println("\n--- [USE CASE 1: REGISTER] DANG KY TAI KHOAN FAN MOI ---");
        System.out.print("Ten dang nhap (>= 4 ky tu): ");
        String u = scanner.nextLine().trim();
        System.out.print("Mat khau (>= 6 ky tu): ");
        String p = scanner.nextLine().trim();
        System.out.print("Ho va ten: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email (vd: user@gmail.com): ");
        String mail = scanner.nextLine().trim();
        System.out.print("So dien thoai (10 chu so bat dau bang 0, vd: 0912345678): ");
        String phone = scanner.nextLine().trim();

        try {
            Fan f = controller.register(u, p, name, mail, phone);
            System.out.println("[OK] Dang ky thanh cong! FanID cua ban la: " + f.getFanId());
            currentFan = f; // Tu dong dang nhap
            System.out.println("[INFO] Da tu dong dang nhap voi tai khoan: " + f.getFullName());
        } catch (Exception e) {
            System.out.println("[ERROR] Dang ky that bai: " + e.getMessage());
            System.out.println("[!] Vui long kiem tra lai thong tin nhap de dam bao dung quy dinh!");
        }
    }

    // --- USE CASE 2: Login ---
    private void handleLogin() {
        System.out.println("\n--- [USE CASE 2: LOGIN] DANG NHAP HE THONG ---");
        System.out.print("Ten dang nhap: ");
        String u = scanner.nextLine().trim();
        System.out.print("Mat khau: ");
        String p = scanner.nextLine().trim();

        try {
            currentFan = controller.login(u, p);
            System.out.println("[OK] Dang nhap thanh cong! Xin chao " + currentFan.getFullName() + " (" + currentFan.getFanId() + ")");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleProfileOrLogout() {
        System.out.println("\n--- THONG TIN TAI KHOAN HIEN TAI ---");
        System.out.println("Ma Fan:        " + currentFan.getFanId());
        System.out.println("Ten dang nhap: " + currentFan.getUsername());
        System.out.println("Ho va ten:     " + currentFan.getFullName());
        System.out.println("Email:         " + currentFan.getEmail());
        System.out.println("So dien thoai: " + currentFan.getPhone());
        System.out.println("------------------------------------");
        System.out.println("1. Cap nhat thong tin ca nhan (Profile)");
        System.out.println("2. Dang xuat");
        System.out.println("0. Quay lai menu chinh");
        System.out.print(">> Chon: ");
        String sub = scanner.nextLine().trim();
        if ("1".equals(sub)) {
            handleUpdateProfile();
        } else if ("2".equals(sub)) {
            currentFan = null;
            System.out.println("[INFO] Da dang xuat thanh cong!");
        }
    }

    private void handleUpdateProfile() {
        System.out.println("\n--- CAP NHAT THONG TIN CA NHAN ---");
        System.out.println("(De trong neu khong muon thay doi)");
        System.out.print("Ho ten moi: ");
        String name = scanner.nextLine();
        System.out.print("Email moi: ");
        String mail = scanner.nextLine();
        System.out.print("SDT moi: ");
        String phone = scanner.nextLine();
        System.out.print("Mat khau moi: ");
        String pass = scanner.nextLine();

        try {
            currentFan = controller.updateProfile(currentFan.getFanId(), name, mail, phone, pass);
            System.out.println("[OK] Cap nhat thanh cong!");
        } catch (Exception e) {
            System.out.println("[ERROR] Cap nhat that bai: " + e.getMessage());
        }
    }

    // --- USE CASE 3: Search match ---
    private void handleSearchMatch() {
        System.out.println("\n--- [USE CASE 3: SEARCH MATCH] TIM KIEM TRAN DAU ---");
        System.out.print("Nhap tu khoa tim kiem (ten tran, doi bong, san, ma tran, hoac Enter de xem tat ca): ");
        String kw = scanner.nextLine().trim();

        List<Match> results = controller.searchMatches(kw);
        if (results.isEmpty()) {
            System.out.println("[!] Khong tim thay tran dau nao phu hop voi tu khoa: '" + kw + "'");
            return;
        }

        printMatchListTable(results);

        System.out.print("\n>> Ban co muon xem chi tiet (View match detail) tran nao khong? Nhap Ma Tran (hoac Enter de ve menu): ");
        String matchId = scanner.nextLine().trim();
        if (!matchId.isEmpty()) {
            showMatchDetailById(matchId);
        }
    }

    // --- USE CASE 4: Filter match ---
    private void handleFilterMatch() {
        System.out.println("\n--- [USE CASE 4: FILTER MATCH] LOC TRAN DAU THEO TIEU CHI ---");
        System.out.print("1. Nhap ngay thi dau (YYYY-MM-DD, vd: 2026-06-15, Enter de bo qua): ");
        String date = scanner.nextLine().trim();

        System.out.print("2. Nhap ma san van dong (vd: MY_DINH, Enter de bo qua): ");
        String stadium = scanner.nextLine().trim();

        System.out.println("3. Tinh trang ve:");
        System.out.println("   [1] Chi hien tran CON VE (Available)");
        System.out.println("   [2] Chi hien tran HET VE (Sold out)");
        System.out.println("   [3 hoac Enter] Tat ca trang thai");
        System.out.print(">> Chon tinh trang ve: ");
        String stChoice = scanner.nextLine().trim();
        String statusFilter = "all";
        if ("1".equals(stChoice)) statusFilter = "con_ve";
        else if ("2".equals(stChoice)) statusFilter = "het_ve";

        List<Match> results = controller.filterMatches(date, stadium, statusFilter);
        if (results.isEmpty()) {
            System.out.println("[!] Khong co tran dau nao khop voi tieu chi loc da chon!");
            return;
        }

        printMatchListTable(results);

        System.out.print("\n>> Ban co muon xem chi tiet (View match detail) tran nao khong? Nhap Ma Tran (hoac Enter de ve menu): ");
        String matchId = scanner.nextLine().trim();
        if (!matchId.isEmpty()) {
            showMatchDetailById(matchId);
        }
    }

    // --- USE CASE 5: View match detail ---
    private void handleViewMatchDetail() {
        System.out.println("\n--- [USE CASE 5: VIEW MATCH DETAIL] XEM CHI TIET TRAN DAU ---");
        System.out.print("Nhap ma tran dau (vd: M01, M02...): ");
        String matchId = scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[!] Ban chua nhap ma tran dau!");
            return;
        }
        showMatchDetailById(matchId);
    }

    private void showMatchDetailById(String matchId) {
        try {
            Match m = controller.getMatchById(matchId);
            boolean soldOut = controller.isMatchSoldOut(m.getMatchId());
            String status = soldOut ? "[HET VE - SOLD OUT]" : "[CON VE - AVAILABLE]";

            System.out.println("\n==========================================================================");
            System.out.println("                     THONG TIN CHI TIET TRAN DAU                          ");
            System.out.println("==========================================================================");
            System.out.printf(" Ma tran dau    : %s\n", m.getMatchId());
            System.out.printf(" Ten tran dau   : %s\n", m.getMatchName());
            System.out.printf(" Doi thi dau    : %s  VS  %s\n", m.getHomeTeam(), m.getAwayTeam());
            System.out.printf(" Thoi gian      : %s\n", m.getMatchDate());
            System.out.printf(" San van dong   : %s\n", m.getStadiumId());
            System.out.printf(" Tinh trang ve  : %s\n", status);
            System.out.println("--------------------------------------------------------------------------");

            List<Section> sections = controller.getSectionsForMatch(m.getMatchId());
            System.out.println("\n--- DANH SACH KHAN DAI & BANG GIA VE ---");
            if (sections.isEmpty()) {
                System.out.println("[!] Chua co thong tin khan dai cho san van dong nay.");
            } else {
                System.out.println("----------------------------------------------------------------------");
                System.out.printf("%-12s | %-24s | %-14s | %-15s\n", "Ma Khan Dai", "Ten Khan Dai", "San Van Dong", "Gia Ve Goc");
                System.out.println("----------------------------------------------------------------------");
                for (Section s : sections) {
                    System.out.printf("%-12s | %-24s | %-14s | %,15.0f VND\n",
                            s.getSectionId(), s.getSectionName(), s.getStadiumId(), s.getBasePrice());
                }
                System.out.println("----------------------------------------------------------------------");
            }

            // Xem so do cho ngoi
            while (true) {
                System.out.print("\n>> Nhap Ma Khan Dai de xem So Do Cho Ngoi (vd: A_VIP, A_STD...) hoac Enter de quay lai: ");
                String secId = scanner.nextLine().trim();
                if (secId.isEmpty()) break;

                renderSeatMap(m.getMatchId(), secId);
            }

        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void renderSeatMap(String matchId, String secId) {
        List<Seat> seats = controller.getSeatsForSection(matchId, secId);
        if (seats.isEmpty()) {
            System.out.println("[!] Khong tim thay du lieu ghe cho tran " + matchId + " tai khan dai " + secId + "!");
            return;
        }

        long availableCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.available || s.getStatus() == SeatStatus.AVAILABLE).count();
        long lockedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.locked || s.getStatus() == SeatStatus.LOCKED).count();
        long bookedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.booked || s.getStatus() == SeatStatus.BOOKED).count();

        int maxRow = seats.stream().mapToInt(Seat::getRowNumber).max().orElse(0);
        int maxCol = seats.stream().mapToInt(Seat::getSeatNumber).max().orElse(0);

        Map<String, Seat> seatMap = new HashMap<>();
        for (Seat st : seats) {
            seatMap.put(st.getRowNumber() + "_" + st.getSeatNumber(), st);
        }

        System.out.println("\n====================== SO DO CHO NGOI (SEAT MAP) ======================");
        System.out.printf(" Tran: %-10s | Khan dai: %-10s | Ghe trong: %d/%d\n", matchId, secId, availableCount, seats.size());
        System.out.println(" Ky hieu: [O] Con trong (" + availableCount + ")  |  [X] Da dat (" + bookedCount + ")  |  [L] Dang khoa (" + lockedCount + ")\n");
        System.out.print("        ");
        for (int c = 1; c <= maxCol; c++) System.out.printf("C%-3d", c);
        System.out.println("\n------------------------------------------------------------------------");

        for (int r = 1; r <= maxRow; r++) {
            System.out.printf("Hang %-2d | ", r);
            for (int c = 1; c <= maxCol; c++) {
                Seat st = seatMap.get(r + "_" + c);
                if (st == null) {
                    System.out.print(" -  ");
                } else if (st.getStatus() == SeatStatus.available || st.getStatus() == SeatStatus.AVAILABLE) {
                    System.out.print("[O] ");
                } else if (st.getStatus() == SeatStatus.locked || st.getStatus() == SeatStatus.LOCKED) {
                    System.out.print("[L] ");
                } else {
                    System.out.print("[X] ");
                }
            }
            System.out.println();
        }
        System.out.println("========================================================================");
    }

    private void printMatchListTable(List<Match> matches) {
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-28s | %-24s | %-12s | %-12s | %-10s\n",
                "Ma Tran", "Ten Tran Dau", "Cap Dau", "Ngay", "San", "Trang Thai");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        for (Match m : matches) {
            boolean soldOut = controller.isMatchSoldOut(m.getMatchId());
            String status = soldOut ? "[HET VE]" : "[CON VE]";
            String matchUp = m.getHomeTeam() + " vs " + m.getAwayTeam();
            System.out.printf("%-8s | %-28s | %-24s | %-12s | %-12s | %-10s\n",
                    m.getMatchId(), m.getMatchName(), matchUp, m.getMatchDate(), m.getStadiumId(), status);
        }
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }
}
