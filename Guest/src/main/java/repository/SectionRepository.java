package repository;

import model.Section;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SectionRepository {
    private String filePath;

    public SectionRepository() {
        this.filePath = resolveFilePath();
    }

    private String resolveFilePath() {
        String fileName = "sections.csv";
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

    public synchronized List<Section> findAll() {
        List<Section> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Section sec = Section.fromCsv(line);
                    if (sec != null) list.add(sec);
                }
            }
        } catch (IOException e) {
            System.err.println("Loi doc file sections.csv: " + e.getMessage());
        }
        return list;
    }

    public List<Section> findByStadium(String stadiumId) {
        if (stadiumId == null || stadiumId.trim().isEmpty()) return findAll();
        return findAll().stream()
                .filter(s -> s.getStadiumId().equalsIgnoreCase(stadiumId.trim()))
                .collect(Collectors.toList());
    }
}
