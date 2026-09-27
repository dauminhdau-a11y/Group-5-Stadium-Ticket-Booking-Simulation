package com.ticketbooking.controller;

import com.ticketbooking.model.Fan;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.repository.CsvRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FanController {
    private CsvRepository<Fan> fanRepository;
    private CsvRepository<Ticket> ticketRepository;

    public FanController(CsvRepository<Fan> fanRepository, CsvRepository<Ticket> ticketRepository) {
        this.fanRepository = fanRepository;
        this.ticketRepository = ticketRepository;
    }

    public boolean register(String username, String password, String email) {
        if (isBlank(username) || isBlank(password) || isBlank(email)) {
            return false;
        }
        if (fanRepository.findByCondition(fan -> email.equalsIgnoreCase(fan.getEmail())).stream().findFirst().isPresent()) {
            return false;
        }
        if (fanRepository.findByCondition(fan -> username.equalsIgnoreCase(fan.getUsername())).stream().findFirst().isPresent()) {
            return false;
        }
        Fan fan = new Fan(UUID.randomUUID().toString(), username.trim(), password, email.trim());
        fanRepository.save(fan);
        return true;
    }

    public Fan login(String username, String password) {
        if (isBlank(username) || password == null) {
            return null;
        }
        Fan fan = fanRepository.findByCondition(item -> username.equalsIgnoreCase(item.getUsername())).stream().findFirst().orElse(null);
        if (fan == null) {
            return null;
        }
        if (!fan.getPassword().equals(password)) {
            return null;
        }
        return fan;
    }

    public List<Ticket> getMyTickets(String fanId) {
        if (isBlank(fanId)) {
            return Collections.emptyList();
        }
        if (ticketRepository == null) {
            return Collections.emptyList();
        }
        return ticketRepository.findByCondition(ticket -> fanId.equals(ticket.getFanId()));
    }

    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

