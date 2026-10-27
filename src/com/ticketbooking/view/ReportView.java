package com.ticketbooking.view;

import com.ticketbooking.controller.ReportController;
import java.io.IOException;

public class ReportView {
    private ReportController reportController;

    public ReportView(ReportController reportController) {
        this.reportController = reportController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public void setReportController(ReportController reportController) {
        this.reportController = reportController;
    }

    public void displayRevenueReport() {
        try {
            System.out.println(reportController.generateSummaryReport());
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}
