package fr.maxlego08.koth;

import fr.maxlego08.koth.api.utils.ParticipantRewardConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

record KothParticipantRewardPlan(List<RewardCommand> commands, int entrants, int winners, int losers) {

    static KothParticipantRewardPlan create(KothParticipantLedger ledger, UUID capturerId,
                                            ParticipantRewardConfiguration configuration) {
        KothParticipant capturer = ledger.get(capturerId);
        if (capturer == null) throw new IllegalStateException("Capturer was not recorded as a KOTH participant");

        List<KothParticipant> winners = ledger.winners(capturer.teamId());
        List<KothParticipant> losers = ledger.losers(capturer.teamId());
        List<RewardCommand> commands = new ArrayList<>();

        add(commands, winners, configuration.getWinnerCommands());
        add(commands, losers, configuration.getLoserCommands());
        add(commands, List.of(capturer), configuration.getCapturerCommands());

        return new KothParticipantRewardPlan(List.copyOf(commands), ledger.size(), winners.size(), losers.size());
    }

    private static void add(List<RewardCommand> target, List<KothParticipant> participants,
                            List<String> configuredCommands) {
        for (KothParticipant participant : participants) {
            for (String command : configuredCommands) {
                target.add(new RewardCommand(participant, command));
            }
        }
    }

    record RewardCommand(KothParticipant participant, String command) {
    }
}
