package repository;

import model.Ticket;
import model.enums.TicketStatus;
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
        String trimmed = line != null ? line.trim() : "";
        return "ticketId,matchId,seatId,price,status,date".equalsIgnoreCase(trimmed)
                || "ticketId,matchId,seatId,fanId,price,status,bookingDate".equalsIgnoreCase(trimmed)
                || "ticketId,transactionId,seatId,matchId,price,status".equalsIgnoreCase(trimmed)
                || trimmed.startsWith(">>>>>>>")
                || trimmed.startsWith("<<<<<<<")
                || trimmed.startsWith("=======");
    }

    @Override
    public Ticket parseLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            String fanId = parts[3].trim();
            double price = parseDoubleSafe(parts[4]);
            TicketStatus status = parseStatusSafe(parts[5]);
            String bookingDate = parts[6].trim();
            return new Ticket(ticketId, matchId, seatId, fanId, price, status, bookingDate);
        } else if (parts.length == 6 && isTicketStatus(parts[5])) {
            // Định dạng tạo bởi Ticket.toCsvLine(): ticketId, transactionId, seatId, matchId, price, status
            String ticketId = parts[0].trim();
            String transactionId = parts[1].trim();
            String seatId = parts[2].trim();
            String matchId = parts[3].trim();
            double price = parseDoubleSafe(parts[4]);
            TicketStatus status = parseStatusSafe(parts[5]);
            return new Ticket(ticketId, transactionId, seatId, matchId, price, status.name());
        } else if (parts.length >= 6) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            double price = parseDoubleSafe(parts[3]);
            TicketStatus status = parseStatusSafe(parts[4]);
            String bookingDate = parts[5].trim();
            return new Ticket(ticketId, matchId, seatId, price, status, bookingDate);
        } else if (parts.length >= 5) {
            String ticketId = parts[0].trim();
            String matchId = parts[1].trim();
            String seatId = parts[2].trim();
            double price = parseDoubleSafe(parts[3]);
            TicketStatus status = parseStatusSafe(parts[4]);
            return new Ticket(ticketId, matchId, seatId, price, status);
        }
        return new Ticket(parts[0].trim(), "", "", 0.0, TicketStatus.AVAILABLE);
    }

    private double parseDoubleSafe(String val) {
        if (val == null || val.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private TicketStatus parseStatusSafe(String val) {
        if (val == null || val.trim().isEmpty()) {
            return TicketStatus.AVAILABLE;
        }
        try {
            return TicketStatus.valueOf(val.trim());
        } catch (Exception e) {
            return TicketStatus.AVAILABLE;
        }
    }

    private boolean isTicketStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        for (TicketStatus status : TicketStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
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
