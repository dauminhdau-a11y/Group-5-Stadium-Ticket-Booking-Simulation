package controller;

import model.Seat;
import model.enums.SeatStatus;
import model.enums.Stand;
import repository.SeatRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeatController {
    private SeatRepository seatRepository;

    public SeatController() {
        this(new SeatRepository("data/seats.csv"));
    }

    public SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public SeatRepository getSeatRepository() {
        return seatRepository;
    }

    public void setSeatRepository(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllSeats() throws IOException {
        return seatRepository.findAll();
    }

    public List<Seat> getSeatsByMatch(String matchId) throws IOException {
        List<Seat> results = new ArrayList<>();
        if (matchId == null) {
            return results;
        }
        for (Seat seat : seatRepository.findAll()) {
            if (matchId.equalsIgnoreCase(seat.getMatchId())) {
                results.add(seat);
            }
        }
        return results;
    }

    public List<Seat> getSeatsBySection(String matchId, String sectionId) throws IOException {
        List<Seat> results = new ArrayList<>();
        if (matchId == null || sectionId == null) {
            return results;
        }
        for (Seat seat : seatRepository.findAll()) {
            if (matchId.equalsIgnoreCase(seat.getMatchId())
                    && sectionId.equalsIgnoreCase(seat.getSectionId())) {
                results.add(seat);
            }
        }
        return results;
    }

    public Seat getSeatById(String seatId) throws IOException {
        return seatRepository.findById(seatId);
    }

    public List<Seat> searchSeats(String keyword) throws IOException {
        String query = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Seat> results = new ArrayList<>();
        for (Seat seat : seatRepository.findAll()) {
            if (query.isEmpty()
                    || (seat.getSeatId() != null && seat.getSeatId().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getMatchId() != null && seat.getMatchId().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getStand() != null && seat.getStand().name().toLowerCase(Locale.ROOT).contains(query))
                    || (seat.getStatus() != null && seat.getStatus().name().toLowerCase(Locale.ROOT).contains(query))) {
                results.add(seat);
            }
        }
        return results;
    }

    public boolean createSeat(Seat seat) throws IOException {
        if (seat == null || seat.getSeatId() == null || seat.getSeatId().trim().isEmpty()) {
            return false;
        }
        if (seatRepository.findById(seat.getSeatId().trim()) != null) {
            return false;
        }
        seatRepository.save(seat);
        return true;
    }

    public int createSeatsBatch(String matchId, String sectionId, Stand stand, int quantity, double price) throws IOException {
        if (matchId == null || matchId.trim().isEmpty() || sectionId == null || sectionId.trim().isEmpty() || quantity <= 0) {
            return 0;
        }
        String mId = matchId.trim();
        String secId = sectionId.trim();

        int existingCount = 0;
        for (Seat s : seatRepository.findAll()) {
            if (mId.equalsIgnoreCase(s.getMatchId())) {
                existingCount++;
            }
        }

        int created = 0;
        int seatsPerRow = 20;
        for (int i = 1; i <= quantity; i++) {
            int currentNum = existingCount + i;
            String seatId = String.format("SEAT_%05d", currentNum);
            while (seatRepository.findById(seatId) != null) {
                currentNum++;
                seatId = String.format("SEAT_%05d", currentNum);
            }

            int rowIdx = ((i - 1) / seatsPerRow) + 1;
            String rowLabel = "R" + rowIdx;
            int seatNum = ((i - 1) % seatsPerRow) + 1;

            Seat seat = new Seat(seatId, mId, secId, rowLabel, seatNum, SeatStatus.AVAILABLE, 1);
            seat.setStand(stand);
            seat.setPrice(price);
            seatRepository.save(seat);
            created++;
        }
        return created;
    }

    public boolean updateSeat(String seatId, SeatStatus status) throws IOException {
        Seat seat = seatRepository.findById(seatId);
        if (seat == null) {
            return false;
        }
        if (status != null) {
            seat.setStatus(status);
        }
        seatRepository.save(seat);
        return true;
    }

    public int updateSeatsStatusBySection(String matchId, String sectionId, SeatStatus newStatus) throws IOException {
        if (matchId == null || sectionId == null || newStatus == null) {
            return 0;
        }
        List<Seat> targetSeats = getSeatsBySection(matchId.trim(), sectionId.trim());
        int updatedCount = 0;
        for (Seat seat : targetSeats) {
            seat.setStatus(newStatus);
            seatRepository.save(seat);
            updatedCount++;
        }
        return updatedCount;
    }

    public boolean removeSeat(String seatId) throws IOException {
        return seatRepository.deleteById(seatId);
    }

    public int removeSeatsBySection(String matchId, String sectionId) throws IOException {
        if (matchId == null || sectionId == null) {
            return 0;
        }
        List<Seat> targetSeats = getSeatsBySection(matchId.trim(), sectionId.trim());
        int deletedCount = 0;
        for (Seat seat : targetSeats) {
            if (seatRepository.deleteById(seat.getSeatId())) {
                deletedCount++;
            }
        }
        return deletedCount;
    }
}
