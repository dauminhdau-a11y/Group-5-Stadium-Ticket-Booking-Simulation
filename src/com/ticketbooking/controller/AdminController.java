package com.ticketbooking.controller;

public class AdminController {
    private AccountController accountController;
    private MatchController matchController;
    private StadiumController stadiumController;
    private SectionController sectionController;
    private SeatController seatController;
    private TicketController ticketController;
    private VoucherController voucherController;
    private SimulatorController simulatorController;
    private ReportController reportController;

    public AdminController(AccountController accountController,
            MatchController matchController,
            StadiumController stadiumController,
            SectionController sectionController,
            SeatController seatController,
            TicketController ticketController,
            VoucherController voucherController,
            SimulatorController simulatorController,
            ReportController reportController) {
        this.accountController = accountController;
        this.matchController = matchController;
        this.stadiumController = stadiumController;
        this.sectionController = sectionController;
        this.seatController = seatController;
        this.ticketController = ticketController;
        this.voucherController = voucherController;
        this.simulatorController = simulatorController;
        this.reportController = reportController;
    }

    public AccountController getAccountController() {
        return accountController;
    }

    public void setAccountController(AccountController accountController) {
        this.accountController = accountController;
    }

    public MatchController getMatchController() {
        return matchController;
    }

    public void setMatchController(MatchController matchController) {
        this.matchController = matchController;
    }

    public StadiumController getStadiumController() {
        return stadiumController;
    }

    public void setStadiumController(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
    }

    public SectionController getSectionController() {
        return sectionController;
    }

    public void setSectionController(SectionController sectionController) {
        this.sectionController = sectionController;
    }

    public SeatController getSeatController() {
        return seatController;
    }

    public void setSeatController(SeatController seatController) {
        this.seatController = seatController;
    }

    public TicketController getTicketController() {
        return ticketController;
    }

    public void setTicketController(TicketController ticketController) {
        this.ticketController = ticketController;
    }

    public VoucherController getVoucherController() {
        return voucherController;
    }

    public void setVoucherController(VoucherController voucherController) {
        this.voucherController = voucherController;
    }

    public SimulatorController getSimulatorController() {
        return simulatorController;
    }

    public void setSimulatorController(SimulatorController simulatorController) {
        this.simulatorController = simulatorController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public void setReportController(ReportController reportController) {
        this.reportController = reportController;
    }
}