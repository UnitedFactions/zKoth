package fr.maxlego08.koth;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KothParticipantLedgerTest {

    private final UUID capturer = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID ally = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID enemy = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    void classifiesEveryUniqueEntrantByTheirFirstTeam() {
        KothParticipantLedger ledger = new KothParticipantLedger();
        ledger.record(capturer, "Capturer", "red");
        ledger.record(ally, "Ally", "red");
        ledger.record(enemy, "Enemy", "blue");
        ledger.record(enemy, "Enemy", "red");

        assertEquals(3, ledger.size());
        assertEquals(2, ledger.winners("red").size());
        assertEquals(1, ledger.losers("red").size());
        assertEquals("blue", ledger.get(enemy).teamId());
    }

    @Test
    void clearRemovesEntriesBetweenEvents() {
        KothParticipantLedger ledger = new KothParticipantLedger();
        ledger.record(capturer, "Capturer", "red");
        ledger.clear();

        assertEquals(0, ledger.size());
        assertEquals(0, ledger.all().size());
    }
}
