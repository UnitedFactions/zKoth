package fr.maxlego08.koth;

import fr.maxlego08.koth.api.utils.ParticipantRewardConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KothParticipantRewardPlanTest {

    private final UUID capturer = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID ally = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID enemy = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    void capturerReceivesWinnerBaseAndCapturerBonusExactlyOnce() {
        KothParticipantLedger ledger = new KothParticipantLedger();
        ledger.record(capturer, "Capturer", "red");
        ledger.record(ally, "Ally", "red");
        ledger.record(enemy, "Enemy", "blue");
        ParticipantRewardConfiguration configuration = new ParticipantRewardConfiguration(
                true, true, true, List.of("CrazyCrates", "UFShardEconomy"),
                List.of("standard %participant%", "shard %participant%"),
                List.of("standard %participant%"),
                List.of("mystic %participant%", "gold %participant%", "extra-shard %participant%")
        );

        KothParticipantRewardPlan plan = KothParticipantRewardPlan.create(ledger, capturer, configuration);

        assertEquals(3, plan.entrants());
        assertEquals(2, plan.winners());
        assertEquals(1, plan.losers());
        assertEquals(8, plan.commands().size());
        assertEquals(5, countFor(plan, capturer));
        assertEquals(2, countFor(plan, ally));
        assertEquals(1, countFor(plan, enemy));
    }

    private long countFor(KothParticipantRewardPlan plan, UUID uniqueId) {
        return plan.commands().stream()
                .filter(command -> command.participant().uniqueId().equals(uniqueId))
                .count();
    }
}
