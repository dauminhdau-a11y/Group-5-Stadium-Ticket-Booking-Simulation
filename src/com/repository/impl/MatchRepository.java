package com.repository.impl;

import java.nio.file.Path;

import com.model.Match;

public class MatchRepository extends AbstractCsvRepository<Match> {
	public MatchRepository() {
		super(Match.class, "matches.csv");
	}

	public MatchRepository(Path dataDirectory) {
		super(Match.class, dataDirectory.resolve("matches.csv"));
	}
}
