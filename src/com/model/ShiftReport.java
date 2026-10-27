package com.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A comprehensive shift report that aggregates data from transactions,
 * payments,
 * tickets and seats for a specific staff member within a time window.
 */
public class ShiftReport extends SimpleEntity {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String reportId;
    private String staffId;
    private String staffName;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    // --- Transaction counts by status ---
    private int totalTransactions;
    private int confirmedTransactions;
    private int pendingTransactions;
    private int cancelledTransactions;
    private int refundRequestedTransactions;
    private int refundedTransactions;

    // --- Revenue breakdown by payment method (confirmed + paid only) ---
    private double cashRevenue;
    private double cardRevenue;
    private double bankTransferRevenue;

    // --- Gross amounts (all statuses) ---
    private double totalGrossAmount;
    private double totalDiscountAmount;

    // --- Revenue from refunded transactions (money returned) ---
    private double refundedAmount;

    // --- Ticket counts ---
    private int totalTickets;
    private int confirmedTickets;
    private int pendingTickets;
    private int cancelledTickets;
    private int refundedTickets;

    // --- Seat counts (across all matches touched by this staff in the window) ---
    private int seatsAvailable;
    private int seatsBooked;
    private int seatsLocked;

    public ShiftReport() {
    }

    @Override
    public String getId() {
        return reportId;
    }

    // --- Calculated ---
    /** Net revenue = confirmed cash + card + bank - refunded */
    public double getNetRevenue() {
        return cashRevenue + cardRevenue + bankTransferRevenue - refundedAmount;
    }

    public double getTotalConfirmedRevenue() {
        return cashRevenue + cardRevenue + bankTransferRevenue;
    }

    // ---------- Getters / Setters ----------

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public LocalDateTime getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(LocalDateTime shiftStart) {
        this.shiftStart = shiftStart;
    }

    public LocalDateTime getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(LocalDateTime shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getConfirmedTransactions() {
        return confirmedTransactions;
    }

    public void setConfirmedTransactions(int confirmedTransactions) {
        this.confirmedTransactions = confirmedTransactions;
    }

    public int getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(int pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public int getCancelledTransactions() {
        return cancelledTransactions;
    }

    public void setCancelledTransactions(int cancelledTransactions) {
        this.cancelledTransactions = cancelledTransactions;
    }

    public int getRefundRequestedTransactions() {
        return refundRequestedTransactions;
    }

    public void setRefundRequestedTransactions(int refundRequestedTransactions) {
        this.refundRequestedTransactions = refundRequestedTransactions;
    }

    public int getRefundedTransactions() {
        return refundedTransactions;
    }

    public void setRefundedTransactions(int refundedTransactions) {
        this.refundedTransactions = refundedTransactions;
    }

    public double getCashRevenue() {
        return cashRevenue;
    }

    public void setCashRevenue(double cashRevenue) {
        this.cashRevenue = cashRevenue;
    }

    public double getCardRevenue() {
        return cardRevenue;
    }

    public void setCardRevenue(double cardRevenue) {
        this.cardRevenue = cardRevenue;
    }

    public double getBankTransferRevenue() {
        return bankTransferRevenue;
    }

    public void setBankTransferRevenue(double bankTransferRevenue) {
        this.bankTransferRevenue = bankTransferRevenue;
    }

    public double getTotalGrossAmount() {
        return totalGrossAmount;
    }

    public void setTotalGrossAmount(double totalGrossAmount) {
        this.totalGrossAmount = totalGrossAmount;
    }

    public double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public double getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(double refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getConfirmedTickets() {
        return confirmedTickets;
    }

    public void setConfirmedTickets(int confirmedTickets) {
        this.confirmedTickets = confirmedTickets;
    }

    public int getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(int pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public int getCancelledTickets() {
        return cancelledTickets;
    }

    public void setCancelledTickets(int cancelledTickets) {
        this.cancelledTickets = cancelledTickets;
    }

    public int getRefundedTickets() {
        return refundedTickets;
    }

    public void setRefundedTickets(int refundedTickets) {
        this.refundedTickets = refundedTickets;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public int getSeatsLocked() {
        return seatsLocked;
    }

    public void setSeatsLocked(int seatsLocked) {
        this.seatsLocked = seatsLocked;
    }

    // ---------- CSV persistence (kept for backward compat, though not the primary
    // use) ----------

    @Override
    public String toCsvLine() {
        return reportId + "," + (shiftStart == null ? "" : shiftStart) + "," + getNetRevenue();
    }

    @Override
    public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        reportId = values[0];
        shiftStart = LocalDateTime.parse(values[1]);
    }

    // ---------- Pretty print ----------

    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        String line = "============================================================";
        sb.append("\n").append(line).append("\n");
        sb.append("                    SHIFT REPORT\n");
        sb.append(line).append("\n");
        sb.append(String.format("  Staff          : %s (%s)%n", staffName, staffId));
        sb.append(String.format("  Shift start    : %s%n", shiftStart == null ? "N/A" : shiftStart.format(FMT)));
        sb.append(String.format("  Shift end      : %s%n", shiftEnd == null ? "N/A" : shiftEnd.format(FMT)));
        sb.append(line).append("\n");

        sb.append("  TRANSACTIONS\n");
        sb.append(String.format("    Total              : %d%n", totalTransactions));
        sb.append(String.format("    Confirmed          : %d%n", confirmedTransactions));
        sb.append(String.format("    Pending            : %d%n", pendingTransactions));
        sb.append(String.format("    Cancelled / Expired: %d%n", cancelledTransactions));
        sb.append(String.format("    Refund requested   : %d%n", refundRequestedTransactions));
        sb.append(String.format("    Refunded           : %d%n", refundedTransactions));
        sb.append(line).append("\n");

        sb.append("  REVENUE BY PAYMENT METHOD (confirmed only)\n");
        sb.append(String.format("    Cash               : %,15.0f VND%n", cashRevenue));
        sb.append(String.format("    Card               : %,15.0f VND%n", cardRevenue));
        sb.append(String.format("    Bank transfer      : %,15.0f VND%n", bankTransferRevenue));
        sb.append(String.format("    -------------------------------------%n"));
        sb.append(String.format("    Total confirmed    : %,15.0f VND%n", getTotalConfirmedRevenue()));
        sb.append(line).append("\n");

        sb.append("  FINANCIAL SUMMARY\n");
        sb.append(String.format("    Gross amount       : %,15.0f VND%n", totalGrossAmount));
        sb.append(String.format("    Discounts applied  : %,15.0f VND%n", totalDiscountAmount));
        sb.append(String.format("    Refunded           : %,15.0f VND%n", refundedAmount));
        sb.append(String.format("    -------------------------------------%n"));
        sb.append(String.format("    NET REVENUE        : %,15.0f VND%n", getNetRevenue()));
        sb.append(line).append("\n");

        sb.append("  TICKETS\n");
        sb.append(String.format("    Total              : %d%n", totalTickets));
        sb.append(String.format("    Sold (confirmed)   : %d%n", confirmedTickets));
        sb.append(String.format("    Pending payment    : %d%n", pendingTickets));
        sb.append(String.format("    Cancelled / Expired: %d%n", cancelledTickets));
        sb.append(String.format("    Refunded           : %d%n", refundedTickets));
        sb.append(line).append("\n");

        sb.append("  SEAT STATUS (all matches in period)\n");
        sb.append(String.format("    Available          : %d%n", seatsAvailable));
        sb.append(String.format("    Booked             : %d%n", seatsBooked));
        sb.append(String.format("    Locked (held)      : %d%n", seatsLocked));
        sb.append(line).append("\n");

        return sb.toString();
    }
}
