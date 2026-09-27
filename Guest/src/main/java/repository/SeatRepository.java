/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import model.Seat;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List; 
public class SeatRepository {
   private final String filePath = "data/seat.csv";
   public synchronized List<Seat> findAll(){
    List<Seat> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine(); 
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Seat seat = Seat.fromCsv(line);
                    if (seat != null) list.add(seat);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file seats.csv: " + e.getMessage());
        }
        return list;
   }
    
}
