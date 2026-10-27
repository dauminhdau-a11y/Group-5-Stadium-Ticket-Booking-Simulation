package repository;

import model.Seat;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SeatRepository {
    private String filePath;

    public SeatRepository() {
        this.filePath = resolveFilePath();
    }

    private String resolveFilePath() {
        String fileName = "seat.csv";
        String[] candidatePaths = {
            "Guest/data/" + fileName,
            "data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data/" + fileName
        };
        for (String path : candidatePaths) {
            File f = new File(path);
            if (f.exists()) return f.getAbsolutePath();
        }
        return "data/" + fileName;
    }

    public synchronized List<Seat> findAll() {
        List<Seat> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Seat seat = Seat.fromCsv(line);
                    if (seat != null) list.add(seat);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file seat.csv: " + e.getMessage());
        }
        return list;
    }
}
