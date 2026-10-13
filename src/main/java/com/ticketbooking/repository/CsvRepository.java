package com.ticketbooking.repository;

import com.ticketbooking.model.BaseEntity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CsvRepository<T extends BaseEntity>{
    private Path filePath;
    
    public CsvRepository(String fileName){
        this.filePath = Paths.get(fileName);
    }

    public abstract T parseLine(String line);

    public List<T> findAll(){
        List<T> entities = new ArrayList<>();
        if (!Files.exists(filePath)){
            return entities;
        }
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)){
            if (!line.trim().isEmpty()){
                entities.add(parseLine(line));
            }
        }
        return entities;
    }
    public void save(T entity) throws IOException{
        if (entity == null || entity.getId()==null || entity.getId().trim().isEmpty()){
            throw new IllegalArgumentException("Entity and entity ID not must be empty");
        }
        List<T> entities = findAll();
        boolean replaced = false;
        for (int i = 0; i < entities.size();i++){
            if (entity.getId().equals(entities.get(i).getId())){
                entities.set(i, entity);
                replaced = true;
                break;
            }
        }
        if (!replaced){
            entities.add(entity);
        }
        writeAll(entities);
    }
    public boolean deleteById(String id) throws IOException{
        List<T> entities = findAll();
        boolean removed = false;
        for (int i=0; i < entities.size();i++){
            if (id.equals(entities.get(i).getId())){
                entities.remove(i);
                removed = true;
                break;
            }
        }
        if (removed){
            writeAll(entities);
        }
        return removed;
    }
    public void writeAll(List<T> entities) throws IOException{
        Path parent=filePath.getParent();
        if (parent != null){
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        for (T entity : entities){
            lines.add(entity.toCsvLine());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}