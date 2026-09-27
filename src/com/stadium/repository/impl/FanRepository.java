package com.stadium.repository.impl;

import com.stadium.model.Fan;
import java.util.Optional;

public class FanRepository extends AbstractCsvRepository<Fan> {
    public Optional<Fan> findByEmail(String email) {
        return findAll().stream().filter(fan -> email != null && email.equals(fan.getEmail())).findFirst();
    }
}
