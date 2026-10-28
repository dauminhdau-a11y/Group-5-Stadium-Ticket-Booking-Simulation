package com.ticketbooking.view;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SeatType;
import java.util.List;
import java.util.stream.Collectors;

public class SeatMapView {
    public SeatMapView() {
    }

    public void displaySeatMap(String matchId, List<Seat> seats) {
        System.out.println("\n=========================================================================================================");
        System.out.println("                                      VISUAL STADIUM SEAT MAP                                            ");
        System.out.printf("                             [ MATCH: %-10s | 4-STAND SURROUND LAYOUT ]                                 %n", matchId);
        System.out.println("=========================================================================================================");

        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats available to display for match " + matchId);
            return;
        }

        // 1. Phía Bắc (Trên cùng): Khán đài B (Standard)
        List<Seat> standB = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.STANDARD)
                .collect(Collectors.toList());
        System.out.println("\n                                        NORTH / PHÍA BẮC                                                 ");
        System.out.println("                       [ KHÁN ĐÀI B (STAND B - STANDARD) | Giá: 500,000 VND ]                            ");
        System.out.println("+-------------------------------------------------------------------------------------------------------+");
        renderSeatRow(standB, 8);
        System.out.println("+-------------------------------------------------------------------------------------------------------+");

        // 2. Hai bên cánh: Khán đài C (Phía Tây) và Khán đài D (Phía Đông) bao quanh sân bóng ở giữa
        List<Seat> standC = seats.stream()
                .filter(s -> s.getSeatId().startsWith("C"))
                .collect(Collectors.toList());
        List<Seat> standD = seats.stream()
                .filter(s -> s.getSeatId().startsWith("D") || (s.getSeatType() == SeatType.ECONOMY && !s.getSeatId().startsWith("C")))
                .collect(Collectors.toList());

        System.out.println("\n  KHÁN ĐÀI C (TÂY - ECONOMY)      |                     SÂN THI ĐẤU                     |    KHÁN ĐÀI D (ĐÔNG - ECONOMY)   ");
        System.out.println("   (Sau cầu môn 1 - 250k)         |                  [ FOOTBALL PITCH ]                 |     (Sau cầu môn 2 - 250k)       ");
        System.out.println("----------------------------------+-----------------------------------------------------+----------------------------------");

        int maxSideRows = Math.max(4, Math.max((standC.size() + 1) / 2, (standD.size() + 1) / 2));
        for (int row = 0; row < maxSideRows; row++) {
            String cText = formatSeatSlice(standC, row * 2, 2);
            String dText = formatSeatSlice(standD, row * 2, 2);

            if (row == 0) {
                System.out.printf("  %-32s|       +-----------------------------------------+   |  %-32s%n", cText, dText);
            } else if (row == 1) {
                System.out.printf("  %-32s|  [G1] |  [GOAL 1]           (o)        [GOAL 2] | [G2]|  %-32s%n", cText, dText);
            } else if (row == 2) {
                System.out.printf("  %-32s|       |   Cầu môn 1      Vòng tròn    Cầu môn 2 |   |  %-32s%n", cText, dText);
            } else if (row == 3) {
                System.out.printf("  %-32s|       +-----------------------------------------+   |  %-32s%n", cText, dText);
            } else {
                System.out.printf("  %-32s|                                                     |  %-32s%n", cText, dText);
            }
        }

        // 3. Phía Nam (Dưới cùng): Khán đài A (VIP Lounge & VIP Zone)
        List<Seat> standA_VVIP = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.VVIP)
                .collect(Collectors.toList());
        List<Seat> standA_VIP = seats.stream()
                .filter(s -> s.getSeatType() == SeatType.VIP)
                .collect(Collectors.toList());

        System.out.println("+-------------------------------------------------------------------------------------------------------+");
        System.out.println("                 [ KHÁN ĐÀI A (STAND A - VVIP & VIP LOUNGE) | Giá: 1,000,000 - 1,800,000 VND ]          ");
        System.out.println("                                        SOUTH / PHÍA NAM (VIP)                                           ");
        System.out.println("+-------------------------------------------------------------------------------------------------------+");
        System.out.print("  >> [VVIP Lounge]: ");
        for (Seat s : standA_VVIP) {
            System.out.print(formatSeatTag(s) + "   ");
        }
        System.out.println("\n  >> [VIP Center]:  ");
        renderSeatRow(standA_VIP, 7);
        System.out.println("+-------------------------------------------------------------------------------------------------------+");

        long availableCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.AVAILABLE).count();
        long lockedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.LOCKED).count();
        long bookedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.BOOKED).count();

        System.out.println("\nLegend:     [O] : AVAILABLE (Còn trống)   |   [L] : LOCKED (Đang giữ chỗ 10p)   |   [X] : BOOKED (Đã bán)");
        System.out.printf("Statistics: Total: %d seats | Available: %d | Locked (Holding): %d | Booked: %d%n",
                seats.size(), availableCount, lockedCount, bookedCount);
        System.out.println("=========================================================================================================\n");
    }

    private void renderSeatRow(List<Seat> seats, int itemsPerRow) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("   (No seats)");
            return;
        }
        int count = 0;
        int maxDisplay = 32;
        StringBuilder sb = new StringBuilder("   ");
        for (Seat s : seats) {
            sb.append(formatSeatTag(s)).append("   ");
            count++;
            if (count % itemsPerRow == 0) {
                System.out.println(sb.toString());
                sb = new StringBuilder("   ");
            }
            if (count >= maxDisplay) {
                if (sb.length() > 3) {
                    System.out.println(sb.toString());
                }
                int remaining = seats.size() - count;
                if (remaining > 0) {
                    System.out.println("   ... [Còn " + remaining + " ghế nữa trong khán đài]");
                }
                return;
            }
        }
        if (sb.length() > 3) {
            System.out.println(sb.toString());
        }
    }

    private String formatSeatSlice(List<Seat> list, int start, int count) {
        if (list == null || start >= list.size()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < Math.min(start + count, list.size()); i++) {
            sb.append(formatSeatTag(list.get(i))).append(" ");
        }
        return sb.toString().trim();
    }

    private String formatSeatTag(Seat s) {
        String tag;
        if (s.getStatus() == SeatStatus.AVAILABLE) {
            tag = "[O]";
        } else if (s.getStatus() == SeatStatus.LOCKED) {
            tag = "[L]";
        } else {
            tag = "[X]";
        }
        return String.format("%-7s:%s", s.getSeatId(), tag);
    }
}