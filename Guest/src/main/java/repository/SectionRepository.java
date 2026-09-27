
package repository;
import model.Section;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class SectionRepository {
   private final String filePath = "data/sections.csv";

    public synchronized List<Section> findAll() {
        List<Section> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Section sec = Section.fromCsv(line);
                    if (sec != null) list.add(sec);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file sections.csv: " + e.getMessage());
        }
        return list;
    }
}
