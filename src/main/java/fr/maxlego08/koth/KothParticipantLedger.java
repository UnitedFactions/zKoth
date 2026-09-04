package fr.maxlego08.koth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class KothParticipantLedger {

    private final Map<UUID, KothParticipant> participants = new LinkedHashMap<>();

    synchronized void record(UUID uniqueId, String playerName, String teamId) {
        participants.putIfAbsent(uniqueId, new KothParticipant(uniqueId, playerName, teamId));
    }

    synchronized KothParticipant get(UUID uniqueId) {
        return participants.get(uniqueId);
    }

    synchronized Collection<KothParticipant> all() {
        return List.copyOf(participants.values());
    }

    synchronized List<KothParticipant> winners(String winningTeamId) {
        return filterByTeam(winningTeamId, true);
    }

    synchronized List<KothParticipant> losers(String winningTeamId) {
        return filterByTeam(winningTeamId, false);
    }

    synchronized int size() {
        return participants.size();
    }

    synchronized void clear() {
        participants.clear();
    }

    private List<KothParticipant> filterByTeam(String winningTeamId, boolean matches) {
        List<KothParticipant> result = new ArrayList<>();
        for (KothParticipant participant : participants.values()) {
            if (participant.teamId().equals(winningTeamId) == matches) result.add(participant);
        }
        return result;
    }
}
