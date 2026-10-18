package com.ticketbooking.controller;

import com.ticketbooking.exception.AccountLockedException;
import com.ticketbooking.exception.InvalidCredentialsException;
import com.ticketbooking.model.Fan;
import com.ticketbooking.repository.FanRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

public class AuthController {
    private final FanRepository fanRepository;
    private Fan currentFan;

    public AuthController(FanRepository fanRepository){
        if (fanRepository == null){
            throw new IllegalArgumentException("Fan repository must not be null.");
        }
        this.fanRepository = fanRepository;
    }

    public Fan login(String username, String password) throws IOException{
        if (username == null || password == null){
            throw new InvalidCredentialsException();
        }
        Optional<Fan> fanResult = fanRepository.findByUsername(username);
        if (!fanResult.isPresent()
                || !passwordMatches(password, fanResult.get().getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        Fan fan = fanResult.get();
        if (fan.isLocked()){
            throw new AccountLockedException();
        }
        currentFan = fan;
        return currentFan;
    }

    public void logout(){
        currentFan = null;
    }

    public Optional<Fan> getCurrentFan(){
        return Optional.ofNullable(currentFan);
    }

    private boolean passwordMatches(String password, String storedPassword){
        if (storedPassword == null){
            return false;
        }
        return MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }
}