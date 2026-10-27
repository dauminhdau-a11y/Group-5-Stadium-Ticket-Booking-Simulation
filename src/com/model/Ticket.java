package com.model;

import java.util.List;

import com.util.CsvParser;

public class Ticket extends SimpleEntity {
    private String ticketId;
    private String transactionId;
    private String seatId;
    private String matchId;
    private double price;
    private String status;
    public Ticket() { }
    public Ticket(String ticketId, String seatId, String matchId, double price, String status) {
        this(ticketId, "", seatId, matchId, price, status);
    }
    public Ticket(String ticketId, String transactionId, String seatId, String matchId, double price, String status) {
        this.ticketId = ticketId; this.transactionId = transactionId;
        this.seatId = seatId; this.matchId = matchId;
        this.price = price; this.status = status;
    }
    @Override public String getId() { return ticketId; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
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
    @Override public String toCsvLine() {
        return CsvParser.encode(ticketId, transactionId, seatId, matchId, Double.toString(price), status);
    }
    @Override public void fromCsvLine(String csvLine) {
        List<String> values = CsvParser.parseLine(csvLine);
        if (values.size() >= 6) {
            ticketId = values.get(0); transactionId = values.get(1); seatId = values.get(2);
            matchId = values.get(3); price = Double.parseDouble(values.get(4)); status = values.get(5);
        } else if (values.size() >= 5) {
            ticketId = values.get(0); transactionId = ""; seatId = values.get(1);
            matchId = values.get(2); price = Double.parseDouble(values.get(3)); status = values.get(4);
        }
    }
}
