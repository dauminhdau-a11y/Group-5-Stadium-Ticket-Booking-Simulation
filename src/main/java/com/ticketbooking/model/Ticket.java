package com.ticketbooking.model;

public class Ticket {
    private String ticketId;
    private String fanId;

    public Ticket() {
    }

    public Ticket(String ticketId, String fanId) {
        this.ticketId = ticketId;
        this.fanId = fanId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getFanId() {
        return fanId;
    }
}