package util;

import model.Match;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public final class RefundPolicy {
    public static final long MIN_HOURS_BEFORE_KICKOFF = 12;

    private RefundPolicy() {
    }

    public static LocalDateTime getKickoff(Match match) {
        if (match == null || match.getMatchDate() == null
                || match.getMatchTime() == null || match.getMatchTime().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The match schedule is unavailable, so refund eligibility cannot be verified. Please contact staff.");
        }

        try {
            return LocalDateTime.of(match.getMatchDate(), LocalTime.parse(match.getMatchTime().trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "The match schedule has an invalid time, so refund eligibility cannot be verified. Please contact staff.",
                    e);
        }
    }

    public static boolean isRefundAllowed(Match match, LocalDateTime requestTime) {
        if (requestTime == null) {
            throw new IllegalArgumentException("Refund request time must not be null.");
        }
        LocalDateTime deadline = getKickoff(match).minusHours(MIN_HOURS_BEFORE_KICKOFF);
        return !requestTime.isAfter(deadline);
    }
}
