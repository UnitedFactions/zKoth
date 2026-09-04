package fr.maxlego08.koth;

import fr.maxlego08.koth.api.KothPhase;
import fr.maxlego08.koth.api.KothStatus;

import java.util.Collection;
import java.util.Objects;

final class KothRuntimeState {

    private KothRuntimeState() {
    }

    static KothPhase phase(KothStatus status, boolean hasCapturer, boolean contested) {
        if (status == KothStatus.STOP) return KothPhase.INACTIVE;
        if (status == KothStatus.COOLDOWN) return KothPhase.PREPARATION;
        if (!hasCapturer) return KothPhase.LIVE;
        return contested ? KothPhase.CONTESTED : KothPhase.CAPTURING;
    }

    static boolean hasCompetingTeam(String capturingTeamId, Collection<String> occupyingTeamIds) {
        return occupyingTeamIds.stream().anyMatch(teamId -> !Objects.equals(capturingTeamId, teamId));
    }

    static boolean shouldManageNativeScoreboard(boolean enabled) {
        return enabled;
    }
}
