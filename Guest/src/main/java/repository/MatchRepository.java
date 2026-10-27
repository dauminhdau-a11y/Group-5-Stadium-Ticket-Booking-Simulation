package repository;

import model.Match;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MatchRepository {
    private String filePath;

    public MatchRepository() {
        this.filePath = resolveFilePath();
    }

    private String resolveFilePath() {
        String fileName = "matches.csv";
        String[] candidatePaths = {
            "Guest/data/" + fileName,
            "data/" + fileName,
            "../data/" + fileName,
            "../Guest/data/" + fileName,
            "../../Guest/data/" + fileName,
            "../../Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "../../Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "../Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "../Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
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

    public synchronized List<Match> findAll() {
        List<Match> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Match match = Match.fromCsv(line);
                    if (match != null) list.add(match);
                }
            }
        } catch (IOException e) {
            System.err.println("Loi doc file matches.csv: " + e.getMessage());
        }  
        return list;
    }

    public Optional<Match> findById(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) return Optional.empty();
        return findAll().stream()
                .filter(m -> m.getMatchId().equalsIgnoreCase(matchId.trim()))
                .findFirst();
    }
}
