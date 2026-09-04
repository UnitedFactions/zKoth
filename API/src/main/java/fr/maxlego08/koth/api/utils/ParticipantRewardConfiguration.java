package fr.maxlego08.koth.api.utils;

import java.util.List;

public class ParticipantRewardConfiguration {

    private final boolean enabled;
    private final boolean dryRun;
    private final boolean requireOnlineAtWin;
    private final List<String> requiredPlugins;
    private final List<String> winnerCommands;
    private final List<String> loserCommands;
    private final List<String> capturerCommands;

    public ParticipantRewardConfiguration(boolean enabled, boolean dryRun, boolean requireOnlineAtWin,
                                          List<String> requiredPlugins, List<String> winnerCommands,
                                          List<String> loserCommands, List<String> capturerCommands) {
        this.enabled = enabled;
        this.dryRun = dryRun;
        this.requireOnlineAtWin = requireOnlineAtWin;
        this.requiredPlugins = List.copyOf(requiredPlugins);
        this.winnerCommands = List.copyOf(winnerCommands);
        this.loserCommands = List.copyOf(loserCommands);
        this.capturerCommands = List.copyOf(capturerCommands);
    }

    public static ParticipantRewardConfiguration disabled() {
        return new ParticipantRewardConfiguration(false, false, true, List.of(), List.of(), List.of(), List.of());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public boolean isRequireOnlineAtWin() {
        return requireOnlineAtWin;
    }

    public List<String> getRequiredPlugins() {
        return requiredPlugins;
    }

    public List<String> getWinnerCommands() {
        return winnerCommands;
    }

    public List<String> getLoserCommands() {
        return loserCommands;
    }

    public List<String> getCapturerCommands() {
        return capturerCommands;
    }
}
