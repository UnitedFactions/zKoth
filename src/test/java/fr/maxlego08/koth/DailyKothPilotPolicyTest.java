package fr.maxlego08.koth;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DailyKothPilotPolicyTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 7);
    private static final Instant NOW = Instant.parse("2026-09-07T17:55:00Z");

    @Test
    void weekdayStartsAtEightPlayersAndTwoTeams() {
        assertEquals(DailyKothPilotPolicy.Decision.START, evaluate(DayOfWeek.MONDAY, 8, 2, null, null, false));
    }

    @Test
    void weekdaySkipsAtSevenPlayers() {
        assertEquals(DailyKothPilotPolicy.Decision.NOT_ENOUGH_PLAYERS,
                evaluate(DayOfWeek.MONDAY, 7, 2, null, null, false));
    }

    @Test
    void saturdayUsesLowerFourPlayerGate() {
        assertEquals(DailyKothPilotPolicy.Decision.START,
                evaluate(DayOfWeek.SATURDAY, 4, 2, null, null, false));
    }

    @Test
    void requiresTwoDistinctTeams() {
        assertEquals(DailyKothPilotPolicy.Decision.NOT_ENOUGH_TEAMS,
                evaluate(DayOfWeek.MONDAY, 8, 1, null, null, false));
    }

    @Test
    void refusesDuplicateEvaluation() {
        assertEquals(DailyKothPilotPolicy.Decision.ALREADY_EVALUATED,
                evaluate(DayOfWeek.MONDAY, 8, 2, TODAY, null, false));
    }

    @Test
    void refusesBeforeTwentyFourHourCooldownExpires() {
        assertEquals(DailyKothPilotPolicy.Decision.COOLDOWN_ACTIVE,
                evaluate(DayOfWeek.MONDAY, 8, 2, null, NOW.minusSeconds(23 * 3600), false));
    }

    @Test
    void allowsExactlyTwentyFourHoursAfterPreviousStart() {
        assertEquals(DailyKothPilotPolicy.Decision.START,
                evaluate(DayOfWeek.MONDAY, 8, 2, null, NOW.minusSeconds(24 * 3600), false));
    }

    @Test
    void refusesWhenAnotherKothIsActive() {
        assertEquals(DailyKothPilotPolicy.Decision.KOTH_ALREADY_ACTIVE,
                evaluate(DayOfWeek.MONDAY, 8, 2, null, null, true));
    }

    @Test
    void stopsAfterFourteenCalendarDays() {
        DailyKothPilotPolicy.Input input = input(DayOfWeek.MONDAY, 8, 2, null, null, false,
                TODAY.minusDays(14));
        assertEquals(DailyKothPilotPolicy.Decision.PILOT_COMPLETE, DailyKothPilotPolicy.evaluate(input));
    }

    @Test
    void arenaSelectionIsRandomUntilRepeatLimit() {
        List<String> arenas = List.of("North", "South");
        assertEquals("North", DailyKothPilotPolicy.selectArena(arenas, "North", 1, 0, 2));
        assertEquals("South", DailyKothPilotPolicy.selectArena(arenas, "North", 2, 0, 2));
    }

    @Test
    void refusesWhenNoConfiguredArenaExists() {
        DailyKothPilotPolicy.Input input = new DailyKothPilotPolicy.Input(true, true, TODAY, DayOfWeek.MONDAY,
                null, TODAY, 14, NOW, null, 24, false, 8, 8, 4, 2, 2, List.of());
        assertEquals(DailyKothPilotPolicy.Decision.INVALID_ARENAS, DailyKothPilotPolicy.evaluate(input));
    }

    @Test
    void refusesOutsideConfiguredEvaluationMinute() {
        DailyKothPilotPolicy.Input input = new DailyKothPilotPolicy.Input(true, false, TODAY, DayOfWeek.MONDAY,
                null, TODAY, 14, NOW, null, 24, false, 8, 8, 4, 2, 2, List.of("North"));
        assertEquals(DailyKothPilotPolicy.Decision.OUTSIDE_EVALUATION_MINUTE,
                DailyKothPilotPolicy.evaluate(input));
    }

    private DailyKothPilotPolicy.Decision evaluate(DayOfWeek day, int players, int teams,
                                                    LocalDate lastEvaluation, Instant lastStart, boolean active) {
        return DailyKothPilotPolicy.evaluate(input(day, players, teams, lastEvaluation, lastStart, active, TODAY));
    }

    private DailyKothPilotPolicy.Input input(DayOfWeek day, int players, int teams,
                                             LocalDate lastEvaluation, Instant lastStart, boolean active,
                                             LocalDate pilotStart) {
        return new DailyKothPilotPolicy.Input(true, true, TODAY, day, lastEvaluation, pilotStart, 14,
                NOW, lastStart, 24, active, players, 8, 4, teams, 2, List.of("North", "South"));
    }
}
