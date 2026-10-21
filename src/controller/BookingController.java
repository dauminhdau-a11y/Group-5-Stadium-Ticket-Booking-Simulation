package controller;

import model.*;
import repository.CsvRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingController {
    private SyncMechanism mechanism;
    private CsvRepository<Seat> seatRepo;
    private CsvRepository<BookingTransaction> transRepo;

    private BookingTransaction bookNoLock(String fanId, String matchId,
            List<String> seatIds) throws Exception {
        return bookSynchronized(fanId, matchId, seatIds);
    }

    public BookingController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo,
            CsvRepository<BookingTransaction> transRepo) {
        this.mechanism = mechanism;
        this.seatRepo = seatRepo;
        this.transRepo = transRepo;
    }

    public BookingTransaction bookSeats(String fanId, String matchId,
            List<String> seatIds) throws Exception {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new Exception("Vui lòng nhập ít nhất 1 mã ghế.");
        }
        if (seatIds.size() > 4) {
            throw new Exception("Giới hạn giao dịch: Bạn chỉ được đặt tối đa 4 vé một lần!");
        }
        if (this.mechanism == SyncMechanism.SYNCHRONIZED) {
            return bookSynchronized(fanId, matchId, seatIds);
        } else {
            return bookNoLock(fanId, matchId, seatIds);
        }
    }

    public List<Seat> getAvailableSeats(String matchId) {
        return seatRepo.findByCondition(
                seat -> seat.getMatchId().equalsIgnoreCase(matchId) && seat.getStatus() == SeatStatus.AVAILABLE);
    }

    private BookingTransaction bookSynchronized(String fanId, String matchId,
            List<String> seatIds) throws Exception {
        List<Seat> seatsToBook = new ArrayList<>();

        for (String seatId : seatIds) {
            Seat seat = seatRepo.findById(seatId);
            if (!seat.getMatchId().equalsIgnoreCase(matchId)) {
                throw new Exception("Ghế " + seatId + " không thuộc trận đấu " + matchId);
            }
            if (seat.getStatus() == SeatStatus.AVAILABLE) {
                seatsToBook.add(seat);
            } else {
                throw new Exception("Sorry, seat " + seatId + " is already booked.");
            }
        }
        double ticketPrice = 500000;
        double totalAmount = seatsToBook.size() * ticketPrice;

        String transId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        BookingTransaction transaction = new BookingTransaction(transId,
                fanId, seatsToBook.get(0).getMatchId(), totalAmount,
                java.time.LocalDateTime.now(), BookingStatus.CONFIRMED);
        for (Seat seat : seatsToBook) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepo.save(seat);
        }
        transRepo.save(transaction);
        return transaction;
    }

}
