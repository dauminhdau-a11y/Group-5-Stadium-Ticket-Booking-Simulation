package com.stadium.model;

public class Ticket extends SimpleEntity {
    private String ticketId;
    private String seatId;
    private String matchId;
    private double price;
    private String status;
    public Ticket() { }
    public Ticket(String ticketId, String seatId, String matchId, double price, String status) {
        this.ticketId = ticketId; this.seatId = seatId; this.matchId = matchId;
        this.price = price; this.status = status;
    }
    @Override public String getId() { return ticketId; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean createTicket() { return false; }
    public boolean updateTicketPrice(double newPrice) { return false; }
    public boolean reserveTicket() { return false; }
    public boolean removeTicket() { return false; }
    public boolean confirmTicketSold() { return false; }
    @Override public String toCsvLine() { return ticketId + "," + seatId + "," + matchId + "," + price + "," + status; }
    @Override public void fromCsvLine(String csvLine) {
        String[] values = csvLine.split(",", -1);
        ticketId = values[0]; seatId = values[1]; matchId = values[2];
        price = Double.parseDouble(values[3]); status = values[4];
    }
}
