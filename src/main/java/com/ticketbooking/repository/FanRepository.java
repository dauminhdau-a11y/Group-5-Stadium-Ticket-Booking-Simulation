package com.ticketbooking.repository;

import com.ticketbooking.model.Fan;

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
}