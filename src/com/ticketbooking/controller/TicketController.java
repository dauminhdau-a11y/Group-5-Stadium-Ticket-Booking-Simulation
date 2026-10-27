package com.ticketbooking.controller;

import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.repository.TicketRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketController {
    private TicketRepository ticketRepository;

    public TicketController() {
        this(new TicketRepository("data/tickets.csv"));
    }

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }

    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() throws IOException {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(String ticketId) throws IOException {
        return ticketRepository.findById(ticketId);
    }

    public List<Ticket> getTicketsByMatch(String matchId) throws IOException {
        return ticketRepository.findByMatchId(matchId);
    }

    public List<Ticket> getTicketsByFan(String fanId) throws IOException {
        return ticketRepository.findByFanId(fanId);
    }

    public boolean createTicket(Ticket ticket) throws IOException {
        if (ticket == null || ticket.getTicketId() == null || ticket.getTicketId().trim().isEmpty()) {
            return false;
        }
        if (ticketRepository.findById(ticket.getTicketId()) != null) {
            return false;
        }
        ticketRepository.save(ticket);
        return true;
    }

    public boolean updateTicketPrice(String ticketId, double newPrice) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || newPrice < 0) {
            return false;
        }
        ticket.setPrice(newPrice);
        ticketRepository.save(ticket);
        return true;
    }

    public boolean removeTicket(String ticketId) throws IOException {
        return ticketRepository.deleteById(ticketId);
    }

    public boolean refundTicket(String ticketId) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null || ticket.getStatus() == TicketStatus.REFUNDED) {
            return false;
        }
        ticket.setStatus(TicketStatus.REFUNDED);
        ticketRepository.save(ticket);
        return true;
    }
}
