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
    private CsvRepository<Booking> ticketRepo;

    private BookingTransaction bookNoLock(String fanId, String matchId, List<String> seatIds) throws Exception {
        return bookSynchronized(fanId, matchId, seatIds);
    }

    public BookingController(SyncMechanism mechanism, CsvRepository<Seat> seatRepo, CsvRepository<BookingTransaction> transRepo,
        CsvRepository<Booking> ticketRepo) {
        this.mechanism = mechanism;
        this.seatRepo = seatRepo;
        this.transRepo = transRepo;
        this.ticketRepo = ticketRepo;
    }
    public BookingTransaction bookSeats(String fanId, String matchId, List<String> seatIds) throws Exception {
        if(this.mechanism == SyncMechanism.SYNCHRONIZED){
            return bookSynchronized(fanId, matchId, seatIds);
        } else{
            return bookNoLock(fanId, matchId, seatIds);
        }
    }
    private BookingTransaction bookSynchronized(String fanId, String matchId, List<String> seatIds) throws Exception {
        List<Seat> seatsToBook = new ArrayList<>();

        for(String seatId : seatIds){
            Seat seat = seatRepo.findById(seatId);
            if(seat == null){
                throw new Exception("Không tìm thấy mã ghế: " + seatId);
            }
            if(seat.getStatus() == SeatStatus.AVAILABLE){
                seatsToBook.add(seat);
            } else{
                throw new Exception("Rất tiếc, ghế  " + seatId + " đã được đặt trước rồi.");
            }
            seatsToBook.add(seat);
        }
        double ticketPrice = 500000;
        double totalAmount = seatsToBook.size() * ticketPrice;

        String transId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        BookingTransaction transaction = new BookingTransaction(transId, fanId, seatsToBook.get(0).getMatchId(), totalAmount, java.time.LocalDateTime.now(), BookingStatus.CONFIRMED);
        transRepo.save(transaction);
        return transaction;
    }

}
