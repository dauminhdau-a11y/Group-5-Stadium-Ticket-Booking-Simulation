package repository;

import model.Seat;
import model.SeatStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SeatsRepository implements CsvRepository<Seat> {
    private static final Path FILE_PATH = Paths.get("data", "seats.csv");

    @Override
    public List<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return seats;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length < 2 || fields.length > 3) continue;
                Seat seat = new Seat(fields[0].trim(), fields[1].trim());
                seat.setStatus(fields.length == 3 && !fields[2].trim().isEmpty()
                        ? SeatStatus.valueOf(fields[2].trim()) : SeatStatus.AVAILABLE);
                seats.add(seat);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read seats file: " + e.getMessage(), e);
        }
        return seats;
    }

    @Override
    public Seat findById(String id) {
        return findAll().stream().filter(seat -> seat.getSeatId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void save(Seat seat) {
        List<Seat> seats = findAll();
        seats.removeIf(existing -> existing.getSeatId().equals(seat.getSeatId()));
        seats.add(seat);
        writeAll(seats);
    }

    @Override
    public void delete(String id) {
        List<Seat> seats = findAll();
        seats.removeIf(seat -> seat.getSeatId().equals(id));
        writeAll(seats);
    }

    @Override
    public List<Seat> findByCondition(Predicate<Seat> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    private void writeAll(List<Seat> seats) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Seat seat : seats) {
                    writer.write(seat.getSeatId() + "," + seat.getMatchId() + "," + seat.getStatus().name());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write seats file: " + e.getMessage(), e);
        }
    }
}
