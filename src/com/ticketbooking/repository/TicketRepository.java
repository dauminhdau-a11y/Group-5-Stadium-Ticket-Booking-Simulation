package com.ticketbooking.repository;

import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.enums.TicketStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository extends CsvRepository<Ticket> {
    public TicketRepository() {
        this("data/tickets.csv");
    }

    public TicketRepository(String fileName) {
        super(fileName);
    }

    @Override
    public boolean isHeaderLine(String line) {
        return "ticketId,matchId,seatId,price,status,date".equalsIgnoreCase(line.trim())
                || "ticketId,matchId,seatId,fanId,price,status,bookingDate".equalsIgnoreCase(line.trim());
    }

    @Override
    public Ticket parseLine(String line) {
        Ticket ticket = new Ticket();
        ticket.fromCsvLine(line);
        return ticket;
    }

    public List<Ticket> findByMatchId(String matchId) throws IOException {
        List<Ticket> result = new ArrayList<>();
        if (matchId == null) {
            return result;
        }
        for (Ticket ticket : findAll()) {
            if (matchId.equalsIgnoreCase(ticket.getMatchId())) {
                result.add(ticket);
            }
        }
        return result;
    }

    public List<Ticket> findByFanId(String fanId) throws IOException {
        List<Ticket> result = new ArrayList<>();
        if (fanId == null) {
            return result;
        }
        for (Ticket ticket : findAll()) {
            if (fanId.equalsIgnoreCase(ticket.getFanId())) {
                result.add(ticket);
            }
        }
        return result;
    }

    public Ticket findBySeatId(String seatId) throws IOException {
        if (seatId == null) {
            return null;
        }
        for (Ticket ticket : findAll()) {
            if (seatId.equalsIgnoreCase(ticket.getSeatId())) {
                return ticket;
            }
        }
        return null;
    }
}