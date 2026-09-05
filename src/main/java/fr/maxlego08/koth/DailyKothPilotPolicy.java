package fr.maxlego08.koth;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

final class DailyKothPilotPolicy {

    enum Decision {
        START,
        DISABLED,
        OUTSIDE_EVALUATION_MINUTE,
        ALREADY_EVALUATED,
        PILOT_COMPLETE,
        COOLDOWN_ACTIVE,
        KOTH_ALREADY_ACTIVE,
        NOT_ENOUGH_PLAYERS,
        NOT_ENOUGH_TEAMS,
        INVALID_ARENAS
    }

    record Input(
            boolean enabled,
            boolean evaluationMinute,
            LocalDate today,
            DayOfWeek dayOfWeek,
            LocalDate lastEvaluatedDate,
            LocalDate pilotStartedDate,
            int pilotDurationDays,
            Instant now,
            Instant lastStartedAt,
            int cooldownHours,
            boolean kothActive,
            int onlinePlayers,
            int weekdayMinimumPlayers,
            int saturdayMinimumPlayers,
            int onlineTeams,
            int minimumTeams,
            List<String> validArenas
    ) {
    }

    private DailyKothPilotPolicy() {
    }

    static Decision evaluate(Input input) {
        if (!input.enabled()) return Decision.DISABLED;
        if (!input.evaluationMinute()) return Decision.OUTSIDE_EVALUATION_MINUTE;
        if (input.today().equals(input.lastEvaluatedDate())) return Decision.ALREADY_EVALUATED;
        if (input.pilotDurationDays() <= 0 || (input.pilotStartedDate() != null
                && !input.today().isBefore(input.pilotStartedDate().plusDays(input.pilotDurationDays())))) {
            return Decision.PILOT_COMPLETE;
        }
        if (input.lastStartedAt() != null && Duration.between(input.lastStartedAt(), input.now()).toHours() < input.cooldownHours()) {
            return Decision.COOLDOWN_ACTIVE;
        }
        if (input.kothActive()) return Decision.KOTH_ALREADY_ACTIVE;
        int minimumPlayers = input.dayOfWeek() == DayOfWeek.SATURDAY
                ? input.saturdayMinimumPlayers() : input.weekdayMinimumPlayers();
        if (input.onlinePlayers() < minimumPlayers) return Decision.NOT_ENOUGH_PLAYERS;
        if (input.onlineTeams() < input.minimumTeams()) return Decision.NOT_ENOUGH_TEAMS;
        if (input.validArenas().isEmpty()) return Decision.INVALID_ARENAS;
        return Decision.START;
    }

    static String selectArena(List<String> arenas, String lastArena, int consecutiveSameArena, int randomIndex,
                              int maxConsecutiveSameArena) {
        if (arenas.isEmpty()) throw new IllegalArgumentException("At least one arena is required");
        if (arenas.size() == 1) return arenas.getFirst();
        if (lastArena != null && consecutiveSameArena >= maxConsecutiveSameArena) {
            return arenas.stream().filter(arena -> !arena.equalsIgnoreCase(lastArena)).findFirst().orElse(arenas.getFirst());
        }
        return arenas.get(Math.floorMod(randomIndex, arenas.size()));
    }
}
