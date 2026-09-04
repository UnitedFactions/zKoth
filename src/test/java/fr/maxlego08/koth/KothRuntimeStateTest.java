package fr.maxlego08.koth;

import fr.maxlego08.koth.api.KothPhase;
import fr.maxlego08.koth.api.KothStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KothRuntimeStateTest {

    @Test
    void mapsAllPlayerFacingPhases() {
        assertEquals(KothPhase.INACTIVE, KothRuntimeState.phase(KothStatus.STOP, false, false));
        assertEquals(KothPhase.PREPARATION, KothRuntimeState.phase(KothStatus.COOLDOWN, false, false));
        assertEquals(KothPhase.LIVE, KothRuntimeState.phase(KothStatus.START, false, false));
        assertEquals(KothPhase.CAPTURING, KothRuntimeState.phase(KothStatus.START, true, false));
        assertEquals(KothPhase.CONTESTED, KothRuntimeState.phase(KothStatus.START, true, true));
    }

    @Test
    void sameTeamDoesNotContestCapture() {
        assertFalse(KothRuntimeState.hasCompetingTeam("red", List.of("red", "red")));
    }

    @Test
    void opposingTeamContestsCapture() {
        assertTrue(KothRuntimeState.hasCompetingTeam("red", List.of("red", "blue")));
    }

    @Test
    void disabledNativeScoreboardNeverTakesOwnership() {
        assertFalse(KothRuntimeState.shouldManageNativeScoreboard(false));
        assertTrue(KothRuntimeState.shouldManageNativeScoreboard(true));
    }
}
