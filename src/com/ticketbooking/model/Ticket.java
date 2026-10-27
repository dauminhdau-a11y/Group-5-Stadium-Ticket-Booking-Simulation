package com.ticketbooking.model;

import com.ticketbooking.model.enums.TicketStatus;

public class Ticket extends BaseEntity {
    private String ticketId;
    private String transactionId;
    private String seatId;
    private String matchId;
    private String fanId;
    private double price;
    private TicketStatus status;
    private String bookingDate;

    public Ticket() {
        super();
        this.status = TicketStatus.AVAILABLE;
        this.price = 0.0;
        this.transactionId = "";
        this.fanId = "";
        this.bookingDate = "";
    }

    public Ticket(String ticketId, String seatId, String matchId, double price, String status) {
        this(ticketId, "", seatId, matchId, price, status);
    }

    public Ticket(String ticketId, String transactionId, String seatId, String matchId, double price, String status) {
        super(ticketId);
        this.ticketId = ticketId;
        this.transactionId = transactionId;
        this.seatId = seatId;
        this.matchId = matchId;
        this.price = price;
        this.fanId = "";
        this.bookingDate = "";
        setStatus(status);
    }

    public Ticket(String ticketId, String matchId, String seatId, double price, TicketStatus status) {
        this(ticketId, matchId, seatId, "", price, status, "");
    }

    public Ticket(String ticketId, String matchId, String seatId, double price, TicketStatus status,
            String bookingDate) {
        this(ticketId, matchId, seatId, "", price, status, bookingDate);
    }

    public Ticket(String ticketId, String matchId, String seatId, String fanId, double price,
            TicketStatus status, String bookingDate) {
        super(ticketId);
        this.ticketId = ticketId;
        this.transactionId = "";
        this.matchId = matchId;
        this.seatId = seatId;
        this.fanId = fanId;
        this.price = price;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    @Override
    public String getId() {
        return ticketId != null ? ticketId : super.getId();
    }

    public String getTicketId() {
        return ticketId != null ? ticketId : getId();
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
        setId(ticketId);
    }

    public String getTransactionId() {
        return transactionId != null ? transactionId : "";
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public TicketStatus getStatus() {
        return status != null ? status : TicketStatus.AVAILABLE;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setStatus(String statusStr) {
        if (statusStr != null) {
            try {
                this.status = TicketStatus.valueOf(statusStr.trim());
            } catch (IllegalArgumentException e) {
                this.status = TicketStatus.AVAILABLE;
            }
        }
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean createTicket() {
        return ticketId != null && !ticketId.trim().isEmpty();
    }

    public boolean updateTicketPrice(double newPrice) {
        if (newPrice >= 0) {
            this.price = newPrice;
            return true;
        }
        return false;
    }

    public boolean reserveTicket() {
        this.status = TicketStatus.RESERVED;
        return true;
    }

    public boolean removeTicket() {
        this.status = TicketStatus.CANCELLED;
        return true;
    }

    public boolean confirmTicketSold() {
        this.status = TicketStatus.SOLD;
        return true;
    }

    @Override
    public String toCsvLine() {
        return String.join(",",
                getTicketId(),
                transactionId != null ? transactionId : "",
                seatId != null ? seatId : "",
                matchId != null ? matchId : "",
                Double.toString(price),
                status != null ? status.name() : TicketStatus.AVAILABLE.name());
    }

    public void fromCsvLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return;
        }
        String[] values = csvLine.split(",", -1);
        if (values.length >= 6) {
            ticketId = values[0].trim();
            try {
                price = Double.parseDouble(values[4].trim());
                transactionId = values[1].trim();
                seatId = values[2].trim();
                matchId = values[3].trim();
                setStatus(values[5].trim());
            } catch (NumberFormatException e) {
                matchId = values[1].trim();
                seatId = values[2].trim();
                try {
                    price = Double.parseDouble(values[3].trim());
                } catch (NumberFormatException ex) {
                    price = 0.0;
                }
                setStatus(values[4].trim());
                bookingDate = values[5].trim();
            }
        } else if (values.length >= 5) {
            ticketId = values[0].trim();
            transactionId = "";
            seatId = values[1].trim();
            matchId = values[2].trim();
            try {
                price = Double.parseDouble(values[3].trim());
            } catch (NumberFormatException e) {
                price = 0.0;
            }
            setStatus(values[4].trim());
        }
        setId(ticketId);
    }
}