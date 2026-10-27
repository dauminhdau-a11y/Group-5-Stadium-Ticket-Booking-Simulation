package com;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import com.model.BookingTransaction;
import com.model.MatchSeat;
import com.model.Ticket;
import com.model.User;
import com.model.enums.BookingStatus;
import com.model.enums.SeatStatus;
import com.model.enums.TicketStatus;
import com.model.enums.UserRole;
import com.repository.impl.BookingTransactionRepository;
import com.repository.impl.MatchSeatRepository;
import com.repository.impl.StaffAccountRepository;
import com.repository.impl.TicketRepository;
import com.util.PasswordHasher;

public final class StaffMenuDemoSetup {
    public static void main(String[] args) {
        StaffAccountRepository accounts = new StaffAccountRepository(
                Paths.get(System.getProperty("stadium.data.dir", "data")));
        accounts.saveInitialAdmin(new User("ADMIN-DEMO", "admin-demo",
                PasswordHasher.hash("AdminPassword123!"), "", UserRole.ADMIN.name()));
        accounts.save(new User("STAFF-DEMO", "staff-demo", PasswordHasher.hash("StaffPassword123!"),
                "staff-demo@example.com", UserRole.STAFF.name()));

        MatchSeatRepository seats = new MatchSeatRepository();
        BookingTransactionRepository transactions = new BookingTransactionRepository();
        TicketRepository tickets = new TicketRepository();

        MatchSeat paymentSeat = new MatchSeat("MS-DEMO-PAY", "M-PAY", "S-PAY", "VIP", "P1", 100000);
        paymentSeat.hold("FAN-DEMO-PAY");
        seats.save(paymentSeat);
        transactions.save(new BookingTransaction("TX-DEMO-PAY", "FAN-DEMO-PAY", "", "M-PAY", "",
                100000, 0, 100000, LocalDateTime.now(), BookingStatus.PENDING));
        tickets.save(new Ticket("T-DEMO-PAY", "TX-DEMO-PAY", "S-PAY", "M-PAY", 100000,
                TicketStatus.PENDING.name()));

        MatchSeat refundSeat = new MatchSeat("MS-DEMO-REFUND", "M-REFUND", "S-REFUND", "VIP", "R1", 100000);
        refundSeat.setStatus(SeatStatus.BOOKED);
        seats.save(refundSeat);
        transactions.save(new BookingTransaction("TX-DEMO-REFUND", "FAN-DEMO-REFUND", "", "M-REFUND", "",
                100000, 0, 100000, LocalDateTime.now(), BookingStatus.REFUND_REQUESTED));
        tickets.save(new Ticket("T-DEMO-REFUND", "TX-DEMO-REFUND", "S-REFUND", "M-REFUND", 100000,
                TicketStatus.CONFIRMED.name()));
    }
}
