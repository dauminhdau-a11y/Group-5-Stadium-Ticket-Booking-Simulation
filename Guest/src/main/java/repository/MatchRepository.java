
package repository;
import model.Match;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MatchRepository {
  private final String filePath = "data/matches.csv";

    public synchronized List<Match> findAll() {
        List<Match> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Match match = Match.fromCsv(line);
                    if (match != null) list.add(match);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file matches.csv: " + e.getMessage());
        }  
        return list;
    }
}
