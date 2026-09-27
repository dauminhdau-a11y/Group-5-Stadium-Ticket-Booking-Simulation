
package View;

import Controller.GuestController;
import model.*;
import java.util.*;

public class GuestView {
    private final GuestController controller;
    private final Scanner scanner;
    private Fan currentFan = null; // Quản lý session đăng nhập

    public GuestView(GuestController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    // +run(): void đúng theo Class Diagram
    public void run() {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("||         STADIUM TICKET BOOKING - GUEST MODULE           || ");
            if (currentFan != null) {
                System.out.printf("|| Đang dang nhap:  ||", currentFan.getFullName());
            } else {
                System.out.println("  Vai trò: KHACH VANG LAI (GUEST)                         || ");
            }
            System.out.println("===================================");
            System.out.println(" 1. Xem lich thi dau, Tim kiem, Loc (Trang thai het ve)    ||");
            System.out.println(" 2. Xem khan dai, gia ve , So đo trang thai ghe           || ");
            if (currentFan == null) {
                System.out.println("|| 3. Dang ki tai khoan Fan mới                             || ");
                System.out.println("|| 4. Dang nhap he thong                                     ||");
            } else {
                System.out.println("|| 3. Xem va cap nhap ca nhan (Profile)                ||");
                System.out.println("|| 4. Dang xuat tai khoan                                    ||");
            }
            System.out.println("|| 0. Thoat                                                  ||");
            System.out.println("==============================================");
            System.out.print("👉 Chon chuc nang: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleSearchAndFilterMatches();
                    break;
                case "2":
                    handleDisplaySeatMap();
                    break;
                case "3":
                    if (currentFan == null) handleRegister();
                    else handleUpdateProfile();
                    break;
                case "4":
                    if (currentFan == null) handleLogin();
                    else {
                        currentFan = null;
                        System.out.println("🔒 Da đang xuat!");
                    }
                    break;
                case "0":
                    System.out.println("Cam on ban đa su dung he thong!");
                    return;
                default:
                    System.out.println("❌ Lua chon khong hop le!");
            }
        }
    }

    private void handleSearchAndFilterMatches() {
        System.out.println("\n--- TIM KIEM & LOC TRAN ĐAU (Enter de bo qua) ---");
        System.out.print("Nhap ngay (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        System.out.print("Nhap ma san van đong (vd: MY_DINH): ");
        String stadium = scanner.nextLine().trim();
        System.out.print("Nhap ten đoi bong hoac tran đau: ");
        String team = scanner.nextLine().trim();

        List<Match> matches = controller.searchMatches(date, stadium, team);
        if (matches.isEmpty()) {
            System.out.println("⚠️ Khong tim thay tran dau nao phu hop!");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-26s | %-12s | %-12s | %-10s\n", "Mã Trận", "Tên Trận", "Ngày", "Sân", "Trạng Thái");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Match m : matches) {
            boolean soldOut = controller.isMatchSoldOut(m.getMatchId());
            String status = soldOut ? "[HET VE]" : "[CON VE]";
            System.out.printf("%-8s | %-26s | %-12s | %-12s | %-10s\n",
                    m.getMatchId(), m.getMatchName(), m.getMatchDate(), m.getStadiumId(), status);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    private void handleDisplaySeatMap() {
        System.out.println("\n--- DANH SACH KHAN ĐAI & BANG GIA VE ---");
        List<Section> sections = controller.getAllSections();
        System.out.printf("%-10s | %-20s | %-12s | %-15s\n", "Ma Khu", "Ten Khan Đai", "San Van Đong", "Gia Goc (VNĐ)");
        System.out.println("--------------------------------------------------------------------");
        for (Section s : sections) {
            System.out.printf("%-10s | %-20s | %-12s | %,15.0f\n",
                    s.getSectionId(), s.getSectionName(), s.getStadiumId(), s.getBasePrice());
        }

        System.out.print("\nNhap ma tran đau đe xem so đo: ");
        String matchId = scanner.nextLine().trim();
        System.out.print("Nhap ma khan đai (Section ID, vd: A_VIP): ");
        String secId = scanner.nextLine().trim();

        List<Seat> seats = controller.getSeatsForSection(matchId, secId);
        if (seats.isEmpty()) {
            System.out.println("⚠️ Khong tim thay du lieu ghe cho khu vuc nay!");
            return;
        }

        int maxRow = seats.stream().mapToInt(Seat::getRowNumber).max().orElse(0);
        int maxCol = seats.stream().mapToInt(Seat::getSeatNumber).max().orElse(0);

        Map<String, Seat> seatMap = new HashMap<>();
        for (Seat st : seats) {
            seatMap.put(st.getRowNumber() + "_" + st.getSeatNumber(), st);
        }

        System.out.println("\n============== SO ĐO GHE KHU VUC " + secId + " ==============");
        System.out.println("Ky hieu: [O] Con trong  |  [X] Da dat  |  [L] Đang khóa\n");
        System.out.print("       ");
        for (int c = 1; c <= maxCol; c++) System.out.printf("C%-3d", c);
        System.out.println("\n--------------------------------------------------------------");

        for (int r = 1; r <= maxRow; r++) {
            System.out.printf("Row %-2d| ", r);
            for (int c = 1; c <= maxCol; c++) {
                Seat st = seatMap.get(r + "_" + c);
                if (st == null) System.out.print(" -  ");
                else if (st.getStatus() == SeatStatus.available) System.out.print("[O] ");
                else if (st.getStatus() == SeatStatus.locked) System.out.print("[L] ");
                else System.out.print("[X] ");
            }
            System.out.println();
        }
        System.out.println("==============================================================");
    }

    private void handleRegister() {
        System.out.println("\n--- DANG KY TAI KHOAN MOI ---");
        System.out.print("ten dang nhap (>=4 ky tu): ");
        String u = scanner.nextLine().trim();
        System.out.print("Mat khau (>=6 ky tu): ");
        String p = scanner.nextLine().trim();
        System.out.print("Ho va ten: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String mail = scanner.nextLine().trim();
        System.out.print("So đien thoai (10 chu so): ");
        String phone = scanner.nextLine().trim();

        try {
            Fan f = controller.register(u, p, name, mail, phone);
            System.out.println("✅ Đang ky thanh cong tai khoan: " + f.getUsername());
        } catch (Exception e) {
            System.out.println("❌ Đăng ky that bai: " + e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.println("\n--- ĐANG NHAP HE THONG ---");
        System.out.print("Ten đang nhap: ");
        String u = scanner.nextLine().trim();
        System.out.print("Mật khẩu: ");
        String p = scanner.nextLine().trim();

        try {
            currentFan = controller.login(u, p);
            System.out.println("✅ Dang nhap thanh cong, xin chao, " + currentFan.getFullName());
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void handleUpdateProfile() {
        System.out.println("\n--- THONG TIN CA NHAN (PROFILE) ---");
        System.out.println("Code Fan:        " + currentFan.getFanId());
        System.out.println("Ten đang nhap: " + currentFan.getUsername());
        System.out.println("Ho ten:        " + currentFan.getFullName());
        System.out.println("Email:         " + currentFan.getEmail());
        System.out.println("So dien thoai: " + currentFan.getPhone());

        System.out.print("\nBan muon cap nhap thong tin khong? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) return;

        System.out.println("(De trong neu khong muon thay doi)");
        System.out.print("Ho ten moi: ");
        String name = scanner.nextLine();
        System.out.print("Email moi: ");
        String mail = scanner.nextLine();
        System.out.print("SĐT moi: ");
        String phone = scanner.nextLine();
        System.out.print("Mat khau moi: ");
        String pass = scanner.nextLine();

        try {
            currentFan = controller.updateProfile(currentFan.getFanId(), name, mail, phone, pass);
            System.out.println("✅ Cap nhap thanh cong!");
        } catch (Exception e) {
            System.out.println("❌ Cap nhap that bai: " + e.getMessage());
        }
    }
}
