package com.stadium.model;

import java.time.LocalDateTime;

public class ShiftReport extends SimpleEntity {
    private String reportId;
    private LocalDateTime date;
    private double totalRevenue;
    public ShiftReport() { }
    public ShiftReport(String reportId, LocalDateTime date, double totalRevenue) {
        this.reportId = reportId; this.date = date; this.totalRevenue = totalRevenue;
    }
    @Override public String getId() { return reportId; }
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public boolean submitReport() { return false; }
    @Override public String toCsvLine() { return reportId + "," + date + "," + totalRevenue; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        reportId = values[0]; date = LocalDateTime.parse(values[1]); totalRevenue = Double.parseDouble(values[2]);
    }
}
