package repository;

import model.Fan;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FanRepository {
    private String filePath;

    public FanRepository() {
        this.filePath = resolveFilePath();
        initFile();
    }

    private String resolveFilePath() {
        String fileName = "fans.csv";
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
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }

        String[] candidateDirs = {
            "Guest/data",
            "data",
            "../data",
            "../Guest/data",
            "../../Guest/data",
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data",
            "Group-5-Stadium-Ticket-Booking-Simulation/data",
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data",
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data"
        };
        for (String dir : candidateDirs) {
            File d = new File(dir);
            if (d.exists() && d.isDirectory()) {
                return new File(d, fileName).getAbsolutePath();
            }
        }

        File defaultDir = new File("Guest/data");
        if (defaultDir.exists() || defaultDir.mkdirs()) {
            return new File(defaultDir, fileName).getAbsolutePath();
        }

        return new File("data", fileName).getAbsolutePath();
    }

    private void initFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    pw.println("fanId,username,fullName,email,phone,password");
                }
            }
        } catch (IOException e) {
            System.err.println("Loi khoi tao fans.csv: " + e.getMessage());
        }
    }

    private List<File> getAllTargetFiles() {
        Set<String> visitedCanonicalPaths = new HashSet<>();
        List<File> targets = new ArrayList<>();

        if (filePath != null) {
            File primary = new File(filePath);
            try {
                visitedCanonicalPaths.add(primary.getCanonicalPath());
                targets.add(primary);
            } catch (Exception ignored) {
                targets.add(primary);
            }
        }

        String fileName = "fans.csv";
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

        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    String cp = f.getCanonicalPath();
                    if (!visitedCanonicalPaths.contains(cp)) {
                        visitedCanonicalPaths.add(cp);
                        targets.add(f);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return targets;
    }

    public synchronized List<Fan> findAll() {
        List<Fan> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            initFile();
            return list;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Header
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Fan fan = Fan.fromCsv(line);
                    if (fan != null) list.add(fan);
                }
            }
        } catch (IOException e) {
            System.err.println("Loi doc fans.csv: " + e.getMessage());
        }
        return list;
    }

    public synchronized boolean save(Fan fan) {
        List<File> allFiles = getAllTargetFiles();
        boolean anySaved = false;

        for (File file : allFiles) {
            try {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                boolean writeHeader = !file.exists() || file.length() == 0;
                boolean needsLeadingNewline = false;

                if (file.exists() && file.length() > 0) {
                    try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                        raf.seek(file.length() - 1);
                        byte lastByte = raf.readByte();
                        if (lastByte != '\n' && lastByte != '\r') {
                            needsLeadingNewline = true;
                        }
                    } catch (Exception ignored) {
                    }
                }

                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
                    if (writeHeader) {
                        pw.println("fanId,username,fullName,email,phone,password");
                    } else if (needsLeadingNewline) {
                        pw.println();
                    }
                    pw.println(fan.toCsv());
                    pw.flush();
                    anySaved = true;
                    System.out.println("[FILE] Da cap nhat file: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Loi ghi file " + file.getAbsolutePath() + ": " + e.getMessage());
            }
        }
        return anySaved;
    }

    public synchronized boolean update(Fan updatedFan) {
        List<Fan> fans = findAll();
        boolean found = false;
        for (int i = 0; i < fans.size(); i++) {
            if (fans.get(i).getFanId().equalsIgnoreCase(updatedFan.getFanId())) {
                fans.set(i, updatedFan);
                found = true;
                break;
            }
        }
        if (!found) return false;

        List<File> allFiles = getAllTargetFiles();
        boolean anyUpdated = false;
        for (File file : allFiles) {
            if (writeAllFans(file, fans)) {
                anyUpdated = true;
                System.out.println("[FILE] Da cap nhat file: " + file.getAbsolutePath());
            }
        }
        return anyUpdated;
    }

    private boolean writeAllFans(File file, List<Fan> fans) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                pw.println("fanId,username,fullName,email,phone,password");
                for (Fan f : fans) {
                    pw.println(f.toCsv());
                }
                pw.flush();
                return true;
            }
        } catch (IOException e) {
            System.err.println("Loi cap nhat file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
