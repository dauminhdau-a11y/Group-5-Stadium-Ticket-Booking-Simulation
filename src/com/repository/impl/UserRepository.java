package com.repository.impl;

import java.nio.file.Path;

import com.model.User;

public class UserRepository extends AbstractCsvRepository<User> {
	public UserRepository() {
		super(User.class, "users.csv");
	}

	public UserRepository(Path dataDirectory) {
		super(User.class, dataDirectory.resolve("users.csv"));
	}
}
