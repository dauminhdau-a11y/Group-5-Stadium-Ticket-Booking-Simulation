package repository;

import model.Seat;

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

public class SeatRepository implements CsvRepository<Seat> {
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
                Seat seat = new Seat();
                seat.fromCsvLine(line);
                if (seat.getSeatId() != null) {
                    seats.add(seat);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read seats file: " + e.getMessage(), e);
        }
        return seats;
    }

    @Override
    public Seat findById(String id) {
        if (id == null) return null;
        return findAll().stream().filter(seat -> seat.getSeatId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    @Override
    public void save(Seat seat) {
        List<Seat> seats = findAll();
        int existingIndex = -1;
        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getSeatId().equalsIgnoreCase(seat.getSeatId())) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex >= 0) {
            seats.set(existingIndex, seat);
        } else {
            seats.add(seat);
        }
        writeAll(seats);
    }

    @Override
    public void delete(String id) {
        List<Seat> seats = findAll();
        seats.removeIf(seat -> seat.getSeatId().equalsIgnoreCase(id));
        writeAll(seats);
    }

    @Override
    public List<Seat> findByCondition(Predicate<Seat> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    private void writeAll(List<Seat> seats) {
        try {
            if (!Files.exists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Seat seat : seats) {
                    writer.write(seat.toCsvLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write seats file: " + e.getMessage(), e);
        }
    }
}
