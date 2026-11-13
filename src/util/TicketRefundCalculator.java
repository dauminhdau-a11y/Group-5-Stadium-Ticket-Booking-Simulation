package util;

import model.Ticket;
import model.Transaction;
import java.util.List;

public class TicketRefundCalculator {
    public TicketRefundCalculator() {
    }

    public double getVoucherDiscount(Ticket ticket, Transaction transaction, List<Ticket> transactionTickets) {
        double subtotal = 0.0;
        for (Ticket transactionTicket : transactionTickets) {
            subtotal += Math.max(0.0, transactionTicket.getPrice());
        }
        if (subtotal <= 0.0 || transaction.getDiscountAmount() <= 0.0) {
            return 0.0;
        }
        double discount = transaction.getDiscountAmount()
                * Math.max(0.0, ticket.getPrice()) / subtotal;
        return Math.min(Math.max(0.0, ticket.getPrice()), discount);
    }

    public double calculateRefundAmount(Ticket ticket, Transaction transaction,
            List<Ticket> transactionTickets) {
        double paidAmount = Math.max(0.0,
                ticket.getPrice() - getVoucherDiscount(ticket, transaction, transactionTickets));
        return Math.round(paidAmount * 0.5);
    }
}
