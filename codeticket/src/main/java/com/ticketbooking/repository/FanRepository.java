package com.ticketbooking.repository;

import com.ticketbooking.model.Fan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FanRepository implements CsvRepository<Fan> {

    private final List<Fan> fans = new ArrayList<>();

    @Override
    public List<Fan> findAll() {
        return new ArrayList<>(fans);
    }

    @Override
    public Fan findById(String id) {
        return fans.stream()
                .filter(fan -> Objects.equals(fan.getFanId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Fan entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Fan cannot be null.");
        }

        if (entity.getFanId() == null || entity.getFanId().trim().isEmpty()) {
            throw new IllegalArgumentException("Fan ID cannot be empty.");
        }

        if (findById(entity.getFanId()) != null) {
            throw new IllegalArgumentException(
                    "Fan ID already exists: " + entity.getFanId()
            );
        }

        fans.add(entity);
    }

    @Override
    public void delete(String id) {
        Fan fan = findById(id);
        if (fan != null) {
            fans.remove(fan);
        }
    }

    @Override
    public List<Fan> findByCondition(Predicate<Fan> predicate) {
        return fans.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Class Diagram method.
     */
    public Fan findByEmail(String email) {
        if (email == null) {
            return null;
        }

        return fans.stream()
                .filter(fan -> email.equalsIgnoreCase(fan.getEmail()))
                .findFirst()
                .orElse(null);
    }
}
