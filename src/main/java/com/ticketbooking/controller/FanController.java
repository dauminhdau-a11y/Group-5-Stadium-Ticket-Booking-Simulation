package com.ticketbooking.controller;

import com.ticketbooking.model.Fan;
import com.ticketbooking.repository.FanRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;
import com.ticketbooking.model.enums.Role;

public class FanController {

    private FanRepository fanRepository;
    private Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9() -]{8,20}$");

    public FanController(FanRepository fanRepository){
        if (fanRepository == null){
            throw new IllegalArgumentException("Fan repository must not be null.");
        }
        this.fanRepository = fanRepository;
    }

    public Fan getProfile(Fan fan) throws IOException{
        if (fan == null || fan.getId() == null || fan.getId().trim().isEmpty()){
            throw new IllegalArgumentException("A valid fan is required.");
        }

        for (Fan savedFan : fanRepository.findAll()){
            if (fan.getId().equals(savedFan.getId())){
                return savedFan;
            }
        }
        throw new IllegalArgumentException("Fan account was not found.");
    }

    public Fan updateProfile(Fan fan, String email, String phone, LocalDate dateOfBirth)
            throws IOException{
        Fan savedFan = getProfile(fan);
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()){
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!normalizedPhone.isEmpty()
                && !PHONE_PATTERN.matcher(normalizedPhone).matches()){
            throw new IllegalArgumentException("Enter a valid phone number.");
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }

        savedFan.setEmail(normalizedEmail);
        savedFan.setPhone(normalizedPhone.isEmpty() ? null : normalizedPhone);
        savedFan.setDateOfBirth(dateOfBirth);
        fanRepository.save(savedFan);

        fan.setEmail(savedFan.getEmail());
        fan.setPhone(savedFan.getPhone());
        fan.setDateOfBirth(savedFan.getDateOfBirth());
        return fan;
    }
    public Fan register(String username, String password, String email, String phone, LocalDate dateOfBirth)throws IOException{
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();
        if (normalizedUsername.isEmpty()){
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.length()<8){
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()){
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!normalizedPhone.isEmpty()){
            if (!PHONE_PATTERN.matcher(normalizedPhone).matches()){
                throw new IllegalArgumentException("Enter a valid phone number.");
            }
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        if (fanRepository.findByUsername(normalizedUsername).isPresent()){
            throw new IllegalArgumentException("Username is already taken.");
        }
        String id = "FAN_" + UUID.randomUUID().toString();
        Fan fan = new Fan(id, id, normalizedUsername, password, normalizedEmail,Role.FAN, false, normalizedPhone.isEmpty() ? null : normalizedPhone, dateOfBirth, 0);
        fanRepository.save(fan);
        return fan;
    }
}