package com.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.model.Match;
import com.model.MatchSeat;
import com.model.Seat;
import com.model.SeatInspectionDetails;
import com.model.enums.SeatStatus;
import com.model.enums.SeatZone;

public class SeatMapView {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void render(Collection<Seat> seats) {
        seats.forEach(seat -> System.out.println(seat.getSeatNumber() + " - " + seat.getStatus()));
    }

    public void renderMatchSeats(List<MatchSeat> seats) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats found for this match.");
            return;
        }
        System.out.printf("%-14s %-12s %-5s %-7s %-12s %-9s %s%n",
                "Stand", "Block", "Row", "Seat", "Seat ID", "Status", "Price");
        for (MatchSeat seat : seats) {
            String color = seat.getStatus() == SeatStatus.AVAILABLE ? ANSI_GREEN
                    : seat.getStatus() == SeatStatus.BOOKED ? ANSI_RED : ANSI_YELLOW;
            System.out.printf("%-14s %-12s %-5d %-7s %-12s " + color + "%-9s" + ANSI_RESET + " %,.0f%n",
                    SeatZone.displayNameFor(seat.getSectionId()), seat.getBlockId(), seat.getRowNumber(),
                    seat.getSeatNumber(), seat.getSeatId(), seat.getStatus(), seat.getPrice());
        }
    }

    public void renderDashboard(Match match, List<MatchSeat> seats) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seat data available for dashboard.");
            return;
        }

        int totalSeats = seats.size();
        int bookedCount = 0;
        int lockedCount = 0;
        int availableCount = 0;

        Map<String, int[]> standStats = new LinkedHashMap<String, int[]>();
        for (SeatZone zone : SeatZone.values()) {
            standStats.put(zone.getSectionId(), new int[4]); // [total, available, locked, booked]
        }

        for (MatchSeat seat : seats) {
            String section = seat.getSectionId();
            int[] stats = standStats.get(section);
            if (stats == null) {
                stats = new int[4];
                standStats.put(section, stats);
            }
            stats[0]++; // total

            if (seat.getStatus() == SeatStatus.BOOKED) {
                bookedCount++;
                stats[3]++;
            } else if (seat.getStatus() == SeatStatus.LOCKED) {
                lockedCount++;
                stats[2]++;
            } else {
                availableCount++;
                stats[1]++;
            }
        }

        double bookedPct = totalSeats > 0 ? (bookedCount * 100.0 / totalSeats) : 0.0;
        double lockedPct = totalSeats > 0 ? (lockedCount * 100.0 / totalSeats) : 0.0;
        double availablePct = totalSeats > 0 ? (availableCount * 100.0 / totalSeats) : 0.0;
        double totalOccupancyPct = bookedPct + lockedPct;

        String matchIdStr = match != null ? match.getMatchId() : "N/A";
        String matchTimeStr = match != null && match.getStartTime() != null
                ? match.getStartTime().format(TIME_FORMATTER) : "N/A";
        String nowStr = LocalDateTime.now().format(TIME_FORMATTER);

        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "==========================================================================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + "                  REAL-TIME STADIUM SEATING DASHBOARD (MONITOR)                           " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "==========================================================================================" + ANSI_RESET);
        System.out.printf("  Trận đấu    : " + ANSI_BOLD + "%-14s" + ANSI_RESET + " | Thời gian thi đấu: %s%n", matchIdStr, matchTimeStr);
        System.out.printf("  Cập nhật lúc: %-14s | Chế độ            : " + ANSI_GREEN + "LIVE REAL-TIME" + ANSI_RESET + "%n", nowStr);
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(ANSI_BOLD + "  SỨC CHỨA & TỶ LỆ LẤP ĐẦY TOÀN SÂN:" + ANSI_RESET);
        System.out.printf("    - Tổng số ghế   : " + ANSI_BOLD + "%,d" + ANSI_RESET + " ghế%n", totalSeats);
        System.out.printf("    - Đã bán " + ANSI_RED + "[X]" + ANSI_RESET + "    : " + ANSI_RED + "%,6d" + ANSI_RESET + " ghế (%5.1f%%)  %s%n",
                bookedCount, bookedPct, makeProgressBar(bookedPct, 20, ANSI_RED));
        System.out.printf("    - Đang giữ " + ANSI_YELLOW + "[L]" + ANSI_RESET + "  : " + ANSI_YELLOW + "%,6d" + ANSI_RESET + " ghế (%5.1f%%)  %s%n",
                lockedCount, lockedPct, makeProgressBar(lockedPct, 20, ANSI_YELLOW));
        System.out.printf("    - Còn trống " + ANSI_GREEN + "[O]" + ANSI_RESET + " : " + ANSI_GREEN + "%,6d" + ANSI_RESET + " ghế (%5.1f%%)  %s%n",
                availableCount, availablePct, makeProgressBar(availablePct, 20, ANSI_GREEN));
        System.out.printf("    ==> TỔNG LẤP ĐẦY: " + ANSI_BOLD + "%5.1f%%" + ANSI_RESET + " (Đã bán + Đang giữ)%n", totalOccupancyPct);

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(ANSI_BOLD + "  TÌNH TRẠNG THEO TỪNG KHÁN ĐÀI (STAND BREAKDOWN):" + ANSI_RESET);
        System.out.println("  +------------+--------------------+--------+--------+--------+--------+----------+--------------+");
        System.out.println("  | Khán đài   | Tên khu vực        | Tổng   | Trống  | Giữ    | Đã bán | Lấp đầy% | Trạng thái   |");
        System.out.println("  +------------+--------------------+--------+--------+--------+--------+----------+--------------+");

        for (Map.Entry<String, int[]> entry : standStats.entrySet()) {
            String sectionId = entry.getKey();
            int[] st = entry.getValue();
            int stTotal = st[0];
            int stAvail = st[1];
            int stLocked = st[2];
            int stBooked = st[3];
            double occ = stTotal > 0 ? ((stBooked + stLocked) * 100.0 / stTotal) : 0.0;

            String statusLabel;
            if (stTotal == 0) {
                statusLabel = "CHƯA MỞ";
            } else if (stAvail == 0) {
                statusLabel = ANSI_RED + ANSI_BOLD + "[SOLD OUT]" + ANSI_RESET;
            } else if (occ >= 90.0) {
                statusLabel = ANSI_YELLOW + ANSI_BOLD + "[SẮP HẾT]" + ANSI_RESET;
            } else {
                statusLabel = ANSI_GREEN + "[CÒN VÉ]" + ANSI_RESET;
            }

            System.out.printf("  | %-10s | %-18s | %,6d | " + ANSI_GREEN + "%,6d" + ANSI_RESET + " | "
                    + ANSI_YELLOW + "%,6d" + ANSI_RESET + " | " + ANSI_RED + "%,6d" + ANSI_RESET
                    + " |   %5.1f%% | %-22s |%n",
                    sectionId,
                    SeatZone.displayNameFor(sectionId),
                    stTotal, stAvail, stLocked, stBooked, occ, statusLabel);
        }
        System.out.println("  +------------+--------------------+--------+--------+--------+--------+----------+--------------+");
        System.out.println("  Chú thích màu: " + ANSI_GREEN + "[O] Xanh lá = Available" + ANSI_RESET + " | "
                + ANSI_YELLOW + "[L] Vàng = Locked" + ANSI_RESET + " | "
                + ANSI_RED + "[X] Đỏ = Booked" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "==========================================================================================" + ANSI_RESET);
    }

    public void renderStadiumLayout(List<MatchSeat> seats) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats found for this match.");
            return;
        }
        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "                         [ FAR STAND ]" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + " [ WEST WING ]              PITCH              [ EAST WING ]" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "                    [ VIP + NEAR MAIN STAND ]" + ANSI_RESET);
        System.out.println("Legend: " + ANSI_GREEN + "[O] Available" + ANSI_RESET + ", "
                + ANSI_YELLOW + "[L] Locked" + ANSI_RESET + ", "
                + ANSI_RED + "[X] Booked" + ANSI_RESET + " (Format: [SeatNumber:Status])");

        renderBlocks(groupSeats(seats));
    }

    public void renderStandLayout(List<MatchSeat> seats, String sectionId) {
        if (seats == null || seats.isEmpty()) {
            System.out.println("No seats found for this match.");
            return;
        }
        List<MatchSeat> filtered = new ArrayList<MatchSeat>();
        for (MatchSeat seat : seats) {
            if (sectionId != null && sectionId.equalsIgnoreCase(seat.getSectionId())) {
                filtered.add(seat);
            }
        }
        if (filtered.isEmpty()) {
            System.out.println("No seats found in section: " + SeatZone.displayNameFor(sectionId));
            return;
        }

        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "=== SƠ ĐỒ KHÁN ĐÀI: "
                + SeatZone.displayNameFor(sectionId) + " (" + sectionId + ") ===" + ANSI_RESET);
        System.out.println("Legend: " + ANSI_GREEN + "[O] Available" + ANSI_RESET + ", "
                + ANSI_YELLOW + "[L] Locked" + ANSI_RESET + ", "
                + ANSI_RED + "[X] Booked" + ANSI_RESET + " (Format: [SeatNumber:SeatID:Status])");

        renderBlocks(groupSeats(filtered));
    }

    public void renderSeatInspection(SeatInspectionDetails details) {
        if (details == null || details.getSeat() == null) {
            System.out.println("[WARN] Không có thông tin ghế.");
            return;
        }
        MatchSeat seat = details.getSeat();
        String statusColor = seat.getStatus() == SeatStatus.AVAILABLE ? ANSI_GREEN
                : seat.getStatus() == SeatStatus.BOOKED ? ANSI_RED : ANSI_YELLOW;

        System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "==================== SEAT INSPECTION DETAILS ====================" + ANSI_RESET);
        System.out.printf("  Mã ghế (Seat ID)         : " + ANSI_BOLD + "%s" + ANSI_RESET + "%n", seat.getSeatId());
        System.out.printf("  Mã vé trận (MatchSeat ID): %s%n", seat.getMatchSeatId());
        System.out.printf("  Khán đài (Stand/Section) : %s (%s)%n", SeatZone.displayNameFor(seat.getSectionId()), seat.getSectionId());
        System.out.printf("  Khối (Block)             : %s%n", seat.getBlockId());
        System.out.printf("  Hàng ghế (Row)           : Hàng %d%n", seat.getRowNumber());
        System.out.printf("  Số ghế (Seat Number)     : %s%n", seat.getSeatNumber());
        System.out.printf("  Giá vé niêm yết (Price)  : %,.0f VND%n", seat.getPrice());
        System.out.printf("  Trạng thái hiện tại      : " + statusColor + ANSI_BOLD + "%s" + ANSI_RESET + "%n", seat.getStatus());
        System.out.println("-----------------------------------------------------------------");

        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            System.out.println(ANSI_GREEN + "  [THÔNG TIN NGHIỆP VỤ] Ghế còn trống, sẵn sàng mở bán tại quầy hoặc online." + ANSI_RESET);
        } else if (seat.getStatus() == SeatStatus.LOCKED) {
            System.out.println(ANSI_YELLOW + "  [THÔNG TIN GIỮ CHỖ (LOCK / PENDING PAYMENT)]" + ANSI_RESET);
            System.out.printf("    - Đang giữ bởi         : %s%n", seat.getLockedBy() != null ? seat.getLockedBy() : "N/A");
            System.out.printf("    - Thời điểm giữ        : %s%n", seat.getLockedAt() != null ? seat.getLockedAt().format(TIME_FORMATTER) : "N/A");
            long remSec = details.getRemainingLockSeconds();
            if (remSec > 0) {
                long minutes = remSec / 60;
                long seconds = remSec % 60;
                System.out.printf("    - Thời hạn còn lại     : " + ANSI_YELLOW + ANSI_BOLD + "%d phút %02d giây" + ANSI_RESET
                        + " (Quá hạn 15 phút sẽ tự động giải phóng)%n", minutes, seconds);
            } else {
                System.out.println("    - Thời hạn còn lại     : " + ANSI_RED + "ĐÃ HẾT HẠN (Sắp tự động dọn dẹp)" + ANSI_RESET);
            }
            if (details.getTransaction() != null) {
                System.out.printf("    - Tên khách hàng giữ   : %s%n", details.getTransaction().getBuyerName());
                System.out.printf("    - Liên hệ              : %s%n", details.getTransaction().getBuyerContact());
            }
        } else if (seat.getStatus() == SeatStatus.BOOKED) {
            System.out.println(ANSI_RED + "  [THÔNG TIN VÉ ĐÃ BÁN (BOOKED / CONFIRMED)]" + ANSI_RESET);
            if (details.getTicket() != null) {
                System.out.printf("    - Mã vé (Ticket ID)    : %s%n", details.getTicket().getTicketId());
                System.out.printf("    - Giá thực thu         : %,.0f VND%n", details.getTicket().getPrice());
            }
            if (details.getTransaction() != null) {
                System.out.printf("    - Mã giao dịch (Tx ID) : %s%n", details.getTransaction().getTransactionId());
                System.out.printf("    - Tên người mua vé     : %s%n", details.getTransaction().getBuyerName());
                System.out.printf("    - SĐT / Email liên hệ  : %s%n", details.getTransaction().getBuyerContact());
                System.out.printf("    - Thời gian đặt mua    : %s%n", details.getTransaction().getBookingDate() != null
                        ? details.getTransaction().getBookingDate().format(TIME_FORMATTER) : "N/A");
                if (details.getTransaction().getStaffId() != null && !details.getTransaction().getStaffId().isEmpty()) {
                    System.out.printf("    - Nhân viên xuất vé    : %s%n", details.getTransaction().getStaffId());
                }
            } else {
                System.out.println("    - Thông tin đặt vé     : Đã xác nhận trên hệ thống.");
            }
        }
        System.out.println(ANSI_BOLD + ANSI_CYAN + "=================================================================" + ANSI_RESET);
    }

    private Map<String, Map<Integer, List<MatchSeat>>> groupSeats(List<MatchSeat> seats) {
        Map<String, Map<Integer, List<MatchSeat>>> layout = new LinkedHashMap<String, Map<Integer, List<MatchSeat>>>();
        for (MatchSeat seat : seats) {
            String blockKey = seat.getSectionId() + "\u0000" + seat.getBlockId();
            Map<Integer, List<MatchSeat>> rows = layout.get(blockKey);
            if (rows == null) {
                rows = new TreeMap<Integer, List<MatchSeat>>();
                layout.put(blockKey, rows);
            }
            List<MatchSeat> row = rows.get(seat.getRowNumber());
            if (row == null) {
                row = new ArrayList<MatchSeat>();
                rows.put(seat.getRowNumber(), row);
            }
            row.add(seat);
        }
        return layout;
    }

    private void renderBlocks(Map<String, Map<Integer, List<MatchSeat>>> layout) {
        List<String> blockKeys = new ArrayList<String>(layout.keySet());
        blockKeys.sort(new Comparator<String>() {
            @Override public int compare(String left, String right) {
                String[] leftParts = left.split("\u0000", -1);
                String[] rightParts = right.split("\u0000", -1);
                int sectionOrder = Integer.compare(SeatZone.orderFor(leftParts[0]), SeatZone.orderFor(rightParts[0]));
                return sectionOrder != 0 ? sectionOrder : leftParts[1].compareTo(rightParts[1]);
            }
        });

        for (String blockKey : blockKeys) {
            String[] parts = blockKey.split("\u0000", -1);
            System.out.println("\n" + ANSI_BOLD + ANSI_CYAN + "▶ " + SeatZone.displayNameFor(parts[0])
                    + " / Khối " + parts[1] + ANSI_RESET);
            Map<Integer, List<MatchSeat>> rows = layout.get(blockKey);
            for (Map.Entry<Integer, List<MatchSeat>> rowEntry : rows.entrySet()) {
                List<MatchSeat> rowSeats = rowEntry.getValue();
                rowSeats.sort(new Comparator<MatchSeat>() {
                    @Override public int compare(MatchSeat a, MatchSeat b) {
                        return a.getSeatNumber().compareTo(b.getSeatNumber());
                    }
                });
                StringBuilder rowText = new StringBuilder(String.format("R%02d ", rowEntry.getKey()));
                for (MatchSeat seat : rowSeats) {
                    char statusChar = seat.getStatus() == SeatStatus.AVAILABLE ? 'O'
                            : seat.getStatus() == SeatStatus.BOOKED ? 'X' : 'L';
                    String color = seat.getStatus() == SeatStatus.AVAILABLE ? ANSI_GREEN
                            : seat.getStatus() == SeatStatus.BOOKED ? ANSI_RED : ANSI_YELLOW;
                    rowText.append(color).append('[').append(seat.getSeatNumber()).append(':')
                            .append(statusChar).append(']').append(ANSI_RESET).append(' ');
                }
                System.out.println(rowText.toString());
            }
        }
    }

    private String makeProgressBar(double percentage, int totalBars, String color) {
        int filledBars = (int) Math.round((percentage / 100.0) * totalBars);
        if (filledBars > totalBars) filledBars = totalBars;
        if (filledBars < 0) filledBars = 0;
        StringBuilder sb = new StringBuilder("[");
        sb.append(color);
        for (int i = 0; i < filledBars; i++) {
            sb.append('#');
        }
        sb.append(ANSI_RESET);
        for (int i = filledBars; i < totalBars; i++) {
            sb.append('-');
        }
        sb.append(']');
        return sb.toString();
    }
}
