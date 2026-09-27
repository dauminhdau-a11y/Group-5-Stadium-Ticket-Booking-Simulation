package com.stadium.controller;

import com.stadium.model.Fan;
import com.stadium.model.Ticket;
import com.stadium.repository.impl.FanRepository;
import com.stadium.repository.impl.TicketRepository;
import java.util.List;

public class FanController {
    private final FanRepository fanRepo;
    private final TicketRepository ticketRepo;

    public FanController(FanRepository fanRepo, TicketRepository ticketRepo) {
        this.fanRepo = fanRepo; this.ticketRepo = ticketRepo;
    }
    public boolean register(Fan fan) { return fanRepo.save(fan); }
    public Fan login(String email) { return fanRepo.findByEmail(email).orElse(null); }
    public List<Ticket> getMyTickets(String fanId) { return List.of(); }
}
