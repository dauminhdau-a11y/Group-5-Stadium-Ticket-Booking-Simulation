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
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            String fanId = parts[3].trim();
            double price = Double.parseDouble(parts[4].trim());
            TicketStatus status = TicketStatus.valueOf(parts[5].trim());
            String bookingDate = parts[6].trim();
            return new Ticket(ticketId, matchId, seatId, fanId, price, status, bookingDate);
        } else if (parts.length >= 6) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            TicketStatus status = TicketStatus.valueOf(parts[4].trim());
            String bookingDate = parts[5].trim();
            return new Ticket(ticketId, matchId, seatId, price, status, bookingDate);
        } else if (parts.length >= 5) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            TicketStatus status = TicketStatus.valueOf(parts[4].trim());
            return new Ticket(ticketId, matchId, seatId, price, status);
        }
        return new Ticket(parts[0].trim(), "", "", 0.0, TicketStatus.AVAILABLE);
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