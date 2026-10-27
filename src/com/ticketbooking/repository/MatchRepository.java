package com.ticketbooking.repository;

import com.ticketbooking.model.Match;

public class MatchRepository extends CsvRepository<Match> {
    public MatchRepository() {
        this("data/matches.csv");
    }

    public MatchRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Match parseLine(String line) {
        return new Match().fromCsvLine(line);
    }
}