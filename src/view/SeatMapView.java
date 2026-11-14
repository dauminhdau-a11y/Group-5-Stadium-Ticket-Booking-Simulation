package view;

import model.Seat;
import model.enums.SeatStatus;
import model.enums.Stand;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatMapView {
    // ANSI Color Escape Codes for terminal rendering
    private String RESET = "\u001B[0m";
    private String BOLD = "\u001B[1m";
    private String RED = "\u001B[31m";
    private String GREEN = "\u001B[1;32m";
    private String YELLOW = "\u001B[1;33m";
    private String CYAN = "\u001B[1;36m";

    public SeatMapView() {
    }

    public void displaySeatMap(String matchId, List<Seat> seats) {
        displaySeatMap(matchId, seats, false);
    }

    public void displaySeatMap(String matchId, List<Seat> seats, boolean showAll) {
        int totalWidth = 98;
        String lineSeparator = repeatStr("=", totalWidth);

        System.out.println("\n" + lineSeparator);
        System.out.println(centerVisible(BOLD + "[ VISUAL STADIUM SEAT MAP ]" + RESET, totalWidth));
        System.out.println(centerVisible("[ MATCH: " + matchId + " | 4 STANDS SURROUNDING PITCH FIELD ]", totalWidth));
        if (showAll) {
            System.out.println(centerVisible(YELLOW + "-- MODE: SHOWING ALL SEATS (FULL DETAIL) --" + RESET, totalWidth));
        }
        System.out.println(lineSeparator);

        if (seats == null || seats.isEmpty()) {
            System.out.println(centerVisible("No seats available to display for match " + matchId, totalWidth));
            System.out.println(lineSeparator + "\n");
            return;
        }

        // 1. Group seats by stand
        List<Seat> standA = seats.stream().filter(s -> s.getStand() == Stand.A).collect(Collectors.toList());
        List<Seat> standB = seats.stream().filter(s -> s.getStand() == Stand.B).collect(Collectors.toList());
        List<Seat> standC = seats.stream().filter(s -> s.getStand() == Stand.C).collect(Collectors.toList());
        List<Seat> standD = seats.stream().filter(s -> s.getStand() == Stand.D).collect(Collectors.toList());

        // 2. North Stand (Stand B)
        System.out.println("\n" + centerVisible(CYAN + "[^] STAND B (NORTH) [^]" + RESET, totalWidth));
        System.out.println(centerVisible(formatStandPrice(standB), totalWidth));
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");
        renderSeatGrid(standB, 5, totalWidth, showAll);
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");

        // 3. Central area: Stand C (West) + Soccer Pitch Field + Stand D (East)
        System.out.println();
        int leftColWidth = 24;
        int centerColWidth = 46;
        int rightColWidth = 24;

        String headerC = CYAN + "STAND C (WEST)" + RESET;
        String subC = formatStandPrice(standC);
        String headerPitch = BOLD + "[ SOCCER PITCH FIELD ]" + RESET;
        String subPitch = "Center Line & 2 Goal Posts";
        String headerD = CYAN + "STAND D (EAST)" + RESET;
        String subD = formatStandPrice(standD);

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

        String[] pitchRows = new String[]{
                "+------+------------------------------+------+",
                "| |--| |              |               | |--| |",
                "| |G1| |              |               | |G2| |",
                "| |--| |            ( o )             | |--| |",
                "| Goal |        Center Circle         | Goal |",
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

        // 4. South Stand (Stand A)
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");
        System.out.println(centerVisible(CYAN + "[v] STAND A (SOUTH) [v]" + RESET, totalWidth));
        System.out.println(centerVisible(formatStandPrice(standA), totalWidth));
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");
        renderSeatGrid(standA, 5, totalWidth, showAll);
        System.out.println("+" + repeatStr("-", totalWidth - 2) + "+");

        // 5. Legend & Statistics
        long availableCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.AVAILABLE).count();
        long lockedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.LOCKED).count();
        long bookedCount = seats.stream().filter(s -> s.getStatus() == SeatStatus.BOOKED).count();

        System.out.println("\n[*] SEAT STATUS LEGEND:");
        System.out.println("   " + formatSeatBadge("[O]", GREEN) + "  : AVAILABLE  -  Ghế trống (Sẵn sàng đặt)");
        System.out.println("   " + formatSeatBadge("[L]", YELLOW) + "  : LOCKED     -  Tạm giữ (Đang giữ chỗ / Đã đặt chờ Staff xác nhận)");
        System.out.println("   " + formatSeatBadge("[X]", RED) + "  : BOOKED     -  Đã đặt (Staff đã xác nhận thanh toán)");

        System.out.printf("\n[#] Statistics (%s): Total: %d seats | %sAvailable: %d%s | %sTạm giữ (Locked/Pending): %d%s | %sBooked: %d%s%n",
                matchId, seats.size(),
                GREEN, availableCount, RESET,
                YELLOW, lockedCount, RESET,
                RED, bookedCount, RESET);

        // 6. Available seats list
        System.out.println("\n" + lineSeparator);
        System.out.println("  [+] AVAILABLE SEATS BY STAND (QUICK SELECTION):");
        printAvailableStandSeats("Stand A (South)", standA, showAll);
        printAvailableStandSeats("Stand B (North)", standB, showAll);
        printAvailableStandSeats("Stand C (West)", standC, showAll);
        printAvailableStandSeats("Stand D (East)", standD, showAll);
        System.out.println("  --> TIP: Take note of available seat IDs [O] above to enter during ticket booking.");
        if (!showAll && (standA.size() > 30 || standB.size() > 30)) {
            System.out.println("  --> NOTE: Stands with high capacity display a preview of the first 30 seats.");
            System.out.println("            Select 'View all seats' to inspect every single seat.");
        }
        System.out.println(lineSeparator + "\n");
    }

    public List<String> formatSeatsForSideColumn(List<Seat> sideSeats, int seatsPerLine) {
        List<String> lines = new ArrayList<>();
        if (sideSeats == null || sideSeats.isEmpty()) {
            lines.add("(No seats)");
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

    public void printAvailableStandSeats(String standTitle, List<Seat> standSeats, boolean showAll) {
        List<String> avail = standSeats.stream()
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .map(Seat::getSeatId)
                .collect(Collectors.toList());
        if (avail.isEmpty()) {
            System.out.println("   * " + standTitle + ": (Sold out)");
        } else {
            if (!showAll && avail.size() > 40) {
                List<String> preview = avail.subList(0, 40);
                System.out.println("   * " + standTitle + ": " + GREEN + String.join(", ", preview) + RESET
                        + " ... (" + (avail.size() - 40) + " more available seats)");
            } else {
                System.out.println("   * " + standTitle + ": " + GREEN + String.join(", ", avail) + RESET);
            }
        }
    }

    public String formatStandPrice(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            return "Ticket price: Not updated";
        }
        double minimumPrice = Double.POSITIVE_INFINITY;
        double maximumPrice = Double.NEGATIVE_INFINITY;
        for (Seat seat : seats) {
            double price = seat.getPrice();
            if (Double.isFinite(price) && price >= 0) {
                minimumPrice = Math.min(minimumPrice, price);
                maximumPrice = Math.max(maximumPrice, price);
            }
        }
        if (!Double.isFinite(minimumPrice)) {
            return "Ticket price: Not updated";
        }
        if (minimumPrice == maximumPrice) {
            return String.format("Ticket price: %,.0f VND/seat", minimumPrice);
        }
        return String.format("Ticket price: %,.0f - %,.0f VND/seat",
                minimumPrice, maximumPrice);
    }

    public void renderSeatGrid(List<Seat> seats, int itemsPerRow, int totalWidth, boolean showAll) {
        if (seats == null || seats.isEmpty()) {
            System.out.println(centerVisible("(No seats in this section)", totalWidth));
            return;
        }

        int count = 0;
        int rowNumber = 1;
        int maxDisplay = showAll ? Integer.MAX_VALUE : 30;
        StringBuilder rowSb = new StringBuilder();

        for (Seat s : seats) {
            rowSb.append(formatSingleSeat(s)).append("  ");
            count++;

            if (count % itemsPerRow == 0) {
                String rowPrefix = String.format("Row %d: ", rowNumber++);
                System.out.println(centerVisible(rowPrefix + trimSpaces(rowSb.toString()), totalWidth));
                rowSb = new StringBuilder();
            }

            if (count >= maxDisplay) {
                if (rowSb.length() > 0) {
                    String rowPrefix = String.format("Row %d: ", rowNumber);
                    System.out.println(centerVisible(rowPrefix + trimSpaces(rowSb.toString()), totalWidth));
                }
                int remaining = seats.size() - count;
                if (remaining > 0) {
                    System.out.println(centerVisible("... [" + remaining + " more seats in this stand. Enter 'all' to show all]", totalWidth));
                }
                return;
            }
        }

        if (rowSb.length() > 0) {
            String rowPrefix = String.format("Row %d: ", rowNumber);
            System.out.println(centerVisible(rowPrefix + trimSpaces(rowSb.toString()), totalWidth));
        }
    }

    public String formatSingleSeat(Seat s) {
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

    public String formatSeatBadge(String tag, String color) {
        return color + "[ SEAT : " + tag + " ]" + RESET;
    }

    public String repeatStr(String s, int n) {
        if (n <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public String trimSpaces(String s) {
        if (s == null) return "";
        int start = 0;
        while (start < s.length() && (s.charAt(start) == ' ' || s.charAt(start) == '\t' || s.charAt(start) == '\r' || s.charAt(start) == '\n')) {
            start++;
        }
        int end = s.length();
        while (end > start && (s.charAt(end - 1) == ' ' || s.charAt(end - 1) == '\t' || s.charAt(end - 1) == '\r' || s.charAt(end - 1) == '\n')) {
            end--;
        }
        return s.substring(start, end);
    }

    public String stripAnsi(String text) {
        if (text == null) return "";
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public String centerVisible(String text, int targetWidth) {
        if (text == null) text = "";
        int visLen = stripAnsi(text).length();
        int totalPad = targetWidth - visLen;
        if (totalPad <= 0) return text;
        int leftPad = totalPad / 2;
        int rightPad = totalPad - leftPad;
        return repeatStr(" ", leftPad) + text + repeatStr(" ", rightPad);
    }
}
