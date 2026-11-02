package com.ticketbooking.view;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SeatType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatMapView {
    // ANSI Color Escape Codes for rich terminal rendering
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    public SeatMapView() {
    }

    public void displaySeatMap(String matchId, List<Seat> seats) {
        int totalWidth = 98;
        String lineSeparator = repeatStr("=", totalWidth);

        System.out.println("\n" + lineSeparator);
        System.out.println(centerVisible(BOLD + "🏟️   VISUAL STADIUM SEAT MAP - SO DO CHO NGOI SAN VAN DONG   🏟️" + RESET, totalWidth));
        System.out.println(centerVisible("[ TRAN DAU / MATCH: " + matchId + " | BO CUC 4 KHAN DAI BAO QUANH SAN DAU ]", totalWidth));
        System.out.println(lineSeparator);

        if (seats == null || seats.isEmpty()) {
            System.out.println(centerVisible("No seats available to display for match " + matchId, totalWidth));
            System.out.println(lineSeparator + "\n");
            return;
        }

        // 1. Phân loại ghế theo khán đài
        List<Seat> standB = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.STANDARD)
                .collect(Collectors.toList());

        List<Seat> standC = seats.stream()
                .filter(s -> s.getSeatId().startsWith("C"))
                .collect(Collectors.toList());

        List<Seat> standD = seats.stream()
                .filter(s -> s.getSeatId().startsWith("D") || (s.getSeatType() == SeatType.ECONOMY && !s.getSeatId().startsWith("C")))
                .collect(Collectors.toList());

        List<Seat> standA_VVIP = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.VVIP)
                .collect(Collectors.toList());

        List<Seat> standA_VIP = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.VIP)
                .collect(Collectors.toList());

        // 2. KHÁN ĐÀI BẮC (NORTH STAND - STAND B - STANDARD)
        System.out.println("\n" + centerVisible(BOLD + "▲  KHAN DAI BAC (NORTH STAND) - STAND B (STANDARD)  ▲" + RESET, totalWidth));
        System.out.println(centerVisible("Goc nhin toan canh san van dong | Don gia: 500,000 VND", totalWidth));
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");
        renderSeatGrid(standB, 5, totalWidth);
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");

        // 3. KHU VỰC TRUNG TÂM: KHÁN ĐÀI C (TÂY) + SÂN BÓNG ĐÁ (PITCH) + KHÁN ĐÀI D (ĐÔNG)
        System.out.println();
        int leftColWidth = 24;
        int centerColWidth = 46;
        int rightColWidth = 24;

        String headerC = BOLD + "KHAN DAI C (TAY/WEST)" + RESET;
        String subC = "Sau Cau Mon 1 - 250k";
        String headerPitch = BOLD + "⚽  SAN BONG DA (PITCH FIELD)  ⚽" + RESET;
        String subPitch = "Vach giua san & 2 Khung thanh";
        String headerD = BOLD + "KHAN DAI D (DONG/EAST)" + RESET;
        String subD = "Sau Cau Mon 2 - 250k";

        System.out.println(
                centerVisible(headerC, leftColWidth) + " | " +
                centerVisible(headerPitch, centerColWidth) + " | " +
                centerVisible(headerD, rightColWidth)
        );
        System.out.println(
                centerVisible(subC, leftColWidth) + " | " +
                centerVisible(subPitch, centerColWidth) + " | " +
                centerVisible(subD, rightColWidth)
        );
        System.out.println(
                repeatStr("-", leftColWidth) + "-+-" +
                repeatStr("-", centerColWidth) + "-+-" +
                repeatStr("-", rightColWidth)
        );

        // Mô hình sân bóng đá nhìn từ trên cao (Pitch ASCII Art)
        String[] pitchRows = new String[]{
                "+------+------------------------------+------+",
                "| |--| |              |               | |--| |",
                "| |G1| |              |               | |G2| |",
                "| |--| |            ( o )             | |--| |",
                "| Goal |          Center Circle       | Goal |",
                "|  1   |              |               |  2   |",
                "+------+------------------------------+------+"
        };

        List<String> cLines = formatSeatsForSideColumn(standC, 2);
        List<String> dLines = formatSeatsForSideColumn(standD, 2);

        int maxRows = Math.max(pitchRows.length, Math.max(cLines.size(), dLines.size()));

        for (int r = 0; r < maxRows; r++) {
            String cText = r < cLines.size() ? cLines.get(r) : "";
            String dText = r < dLines.size() ? dLines.get(r) : "";
            String pitchArt = r < pitchRows.length ? pitchRows[r] : "";

            System.out.println(
                    centerVisible(cText, leftColWidth) + " | " +
                    centerVisible(pitchArt, centerColWidth) + " | " +
                    centerVisible(dText, rightColWidth)
            );
        }

        // 4. KHÁN ĐÀI NAM (SOUTH STAND - STAND A - VIP & VVIP)
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");
        System.out.println(centerVisible(BOLD + "▼  KHAN DAI NAM (SOUTH STAND) - STAND A (VIP & VVIP)  ▼" + RESET, totalWidth));
        System.out.println(centerVisible("Khan dai chinh & Phong cho danh du | Don gia: 1,000,000 - 1,800,000 VND", totalWidth));
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");

        System.out.println("  👑 " + BOLD + "KHU VUC VVIP LOUNGE (Phong VIP thuong hang - 1,800,000 VND):" + RESET);
        renderSeatGrid(standA_VVIP, 5, totalWidth);
        System.out.println();

        System.out.println("  ⭐ " + BOLD + "KHU VUC VIP CENTER (Khan dai VIP trung tam - 1,000,000 VND):" + RESET);
        renderSeatGrid(standA_VIP, 5, totalWidth);
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");

        // 5. CHÚ THÍCH TRẠNG THÁI & THỐNG KÊ
        long availableCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.AVAILABLE).count();
        long lockedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.LOCKED).count();
        long bookedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.BOOKED).count();

        System.out.println("\n📌 CHU THICH TRANG THAI GHE (SEAT STATUS LEGEND):");
        System.out.println("   " + formatSeatBadge("[O]", GREEN) + "  : CON TRONG (AVAILABLE)  -  Ghe trong, san sang de dat ve");
        System.out.println("   " + formatSeatBadge("[L]", YELLOW) + "  : DANG GIU (LOCKED)      -  Dang trong giao dich giu cho (10 phut)");
        System.out.println("   " + formatSeatBadge("[X]", RED) + "  : DA DAT (BOOKED)        -  Ghe da co chu, khong the chon");

        System.out.printf("\n📊 Thong ke (%s): Tong so: %d ghe | %sCon trong: %d%s | %sDang giu: %d%s | %sDa dat: %d%s%n",
                matchId, seats.size(),
                GREEN + BOLD, availableCount, RESET,
                YELLOW + BOLD, lockedCount, RESET,
                RED + BOLD, bookedCount, RESET);

        // 6. DANH SÁCH GHẾ CÒN TRỐNG ĐỂ LỰA CHỌN NHANH (QUICK-PICK)
        System.out.println("\n" + lineSeparator);
        System.out.println("  🎯 DANH SACH GHE CON TRONG THEO TUNG KHAN DAI (DE LUA CHON NHANH):");
        printAvailableStandSeats("Khan dai C (Tay - Economy | 250,000 VND)", standC);
        printAvailableStandSeats("Khan dai D (Dong - Economy | 250,000 VND)", standD);
        printAvailableStandSeats("Khan dai B (Bac - Standard | 500,000 VND)", standB);
        List<Seat> standA = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.VIP || s.getSeatType() == SeatType.VVIP)
                .collect(Collectors.toList());
        printAvailableStandSeats("Khan dai A (Nam - VIP/VVIP | 1,000,000 - 1,800,000 VND)", standA);
        System.out.println("  👉 HUONG DAN: Quy khach ghi nho ma ghe con trong [O] o tren de nhap vao muc dat ve.");
        System.out.println(lineSeparator + "\n");
    }

    private List<String> formatSeatsForSideColumn(List<Seat> sideSeats, int seatsPerLine) {
        List<String> lines = new ArrayList<>();
        if (sideSeats == null || sideSeats.isEmpty()) {
            lines.add("(Khong co ghe)");
            return lines;
        }

        StringBuilder currentLine = new StringBuilder();
        int countInLine = 0;
        for (Seat s : sideSeats) {
            String seatStr = formatSingleSeat(s);
            if (countInLine > 0) {
                currentLine.append(" ");
            }
            currentLine.append(seatStr);
            countInLine++;
            if (countInLine == seatsPerLine) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                countInLine = 0;
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private void printAvailableStandSeats(String standTitle, List<Seat> standSeats) {
        List<String> avail = standSeats.stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .map(Seat::getSeatId)
                .collect(Collectors.toList());
        if (avail.isEmpty()) {
            System.out.println("   • " + standTitle + ": (Het ve / Sold out)");
        } else {
            System.out.println("   • " + standTitle + ": " + GREEN + BOLD + String.join(", ", avail) + RESET);
        }
    }

    private void renderSeatGrid(List<Seat> seats, int itemsPerRow, int totalWidth) {
        if (seats == null || seats.isEmpty()) {
            System.out.println(centerVisible("(Khong co ghe trong khu vuc nay)", totalWidth));
            return;
        }

        int count = 0;
        int rowNumber = 1;
        int maxDisplay = 30;
        StringBuilder rowSb = new StringBuilder();

        for (Seat s : seats) {
            rowSb.append(formatSingleSeat(s)).append("  ");
            count++;

            if (count % itemsPerRow == 0) {
                String rowPrefix = String.format("Hang %d: ", rowNumber++);
                System.out.println(centerVisible(rowPrefix + rowSb.toString().trim(), totalWidth));
                rowSb = new StringBuilder();
            }

            if (count >= maxDisplay) {
                if (rowSb.length() > 0) {
                    String rowPrefix = String.format("Hang %d: ", rowNumber);
                    System.out.println(centerVisible(rowPrefix + rowSb.toString().trim(), totalWidth));
                }
                int remaining = seats.size() - count;
                if (remaining > 0) {
                    System.out.println(centerVisible("... [Con " + remaining + " ghe khac trong khu vuc nay]", totalWidth));
                }
                return;
            }
        }

        if (rowSb.length() > 0) {
            String rowPrefix = String.format("Hang %d: ", rowNumber);
            System.out.println(centerVisible(rowPrefix + rowSb.toString().trim(), totalWidth));
        }
    }

    private String formatSingleSeat(Seat s) {
        String statusText;
        String color;
        if (s.getStatus() == SeatStatus.AVAILABLE) {
            statusText = "[O]";
            color = GREEN;
        } else if (s.getStatus() == SeatStatus.LOCKED) {
            statusText = "[L]";
            color = YELLOW;
        } else {
            statusText = "[X]";
            color = RED;
        }
        return color + String.format("[%s:%s]", s.getSeatId(), statusText) + RESET;
    }

    private String formatSeatBadge(String tag, String color) {
        return color + BOLD + "[ SEAT : " + tag + " ]" + RESET;
    }

    public static String repeatStr(String s, int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String stripAnsi(String text) {
        if (text == null) return "";
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public static String centerVisible(String text, int targetWidth) {
        if (text == null) text = "";
        int visLen = stripAnsi(text).length();
        int totalPad = targetWidth - visLen;
        if (totalPad <= 0) return text;
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return repeatStr(" ", leftPad) + text + repeatStr(" ", rightPad);
    }
}
