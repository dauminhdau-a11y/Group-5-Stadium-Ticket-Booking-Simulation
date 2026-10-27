package repository;

import model.Match;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MatchRepository implements CsvRepository<Match> {
    private static final Path FILE_PATH = Paths.get("data", "matches.csv");

    @Override
    public List<Match> findAll() {
        List<Match> matches = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return matches;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Match match = new Match();
                match.fromCsvLine(line);
                if (match.getMatchId() != null) {
                    matches.add(match);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading matches file: " + e.getMessage());
        }
        return matches;
    }

    @Override
    public Match findById(String id) {
        if (id == null) return null;
        return findAll().stream()
                .filter(m -> m.getMatchId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Match match) {
        List<Match> matches = findAll();
        int existingIndex = -1;
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getMatchId().equalsIgnoreCase(match.getMatchId())) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex >= 0) {
            matches.set(existingIndex, match);
        } else {
            matches.add(match);
        }
        writeAll(matches);
    }

    @Override
    public void delete(String id) {
        List<Match> matches = findAll();
        matches.removeIf(m -> m.getMatchId().equalsIgnoreCase(id));
        writeAll(matches);
    }

    @Override
    public List<Match> findByCondition(Predicate<Match> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    private void writeAll(List<Match> matches) {
        try {
            if (!Files.exists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                for (Match match : matches) {
                    writer.write(match.toCsvLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing matches file: " + e.getMessage());
        }
    }
}
