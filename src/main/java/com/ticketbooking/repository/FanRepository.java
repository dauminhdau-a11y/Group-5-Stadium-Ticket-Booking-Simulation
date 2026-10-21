package com.ticketbooking.repository;

import com.ticketbooking.model.Fan;
import java.io.IOException;
import java.util.Optional;

public class FanRepository extends CsvRepository<Fan>{
    public FanRepository(){
        super("fans.csv");
    }
    public FanRepository(String fileName){
        super(fileName);
    }
    @Override 
    public Fan parseLine(String line){
        return new Fan().fromCsvLine(line);
    }

    @Override
    public boolean isHeaderLine(String line){
        return "fanId,username,password,email".equalsIgnoreCase(line.trim())
                || "id,accountId,username,passwordHash,email,role,locked,phone,dateOfBirth,loyaltyPoints"
                        .equalsIgnoreCase(line.trim());
    }

    public Optional<Fan> findByUsername(String username) throws IOException{
        if (username == null || username.trim().isEmpty()){
            return Optional.empty();
        }
        for (Fan fan : findAll()){
            if (fan.getUsername().equalsIgnoreCase(username.trim())){
                return Optional.of(fan);
            }
        }
        return Optional.empty();
    }
}