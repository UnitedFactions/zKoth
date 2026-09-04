package fr.maxlego08.koth.placeholder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalPlaceholderTest {

    @Test
    void choosesMostSpecificRegisteredPrefix() {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("uf_test_phase", (player, argument) -> "global");
        placeholder.register("uf_test_phase_", (player, argument) -> "arena:" + argument);

        assertEquals("arena:UF_SIM_008", placeholder.onRequest(null, "uf_test_phase_UF_SIM_008"));
    }
}
