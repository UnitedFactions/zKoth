package fr.maxlego08.koth;

import fr.maxlego08.koth.api.Koth;
import fr.maxlego08.koth.api.KothStatus;
import fr.maxlego08.koth.save.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

final class DailyKothScheduler {

    private final KothPlugin plugin;
    private final KothManager kothManager;
    private final File stateFile;
    private BukkitTask task;
    private boolean invalidConfigurationLogged;

    DailyKothScheduler(KothPlugin plugin, KothManager kothManager) {
        this.plugin = plugin;
        this.kothManager = kothManager;
        this.stateFile = new File(plugin.getDataFolder(), "daily-koth-pilot-state.yml");
    }

    void start() {
        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this::tick, 20L, 400L);
    }

    void stop() {
        if (this.task != null) this.task.cancel();
    }

    private void tick() {
        if (!Config.dailyKothEnabled) return;

        try {
            ZoneId zone = ZoneId.of(Config.dailyKothTimezone);
            LocalTime evaluationTime = LocalTime.parse(Config.dailyKothEvaluationTime);
            ZonedDateTime now = ZonedDateTime.now(zone);
            boolean evaluationMinute = now.getHour() == evaluationTime.getHour()
                    && now.getMinute() == evaluationTime.getMinute();
            if (!evaluationMinute) return;

            YamlConfiguration state = YamlConfiguration.loadConfiguration(this.stateFile);
            LocalDate today = now.toLocalDate();
            LocalDate lastEvaluatedDate = parseDate(state.getString("lastEvaluatedDate"));
            LocalDate pilotStartedDate = parseDate(state.getString("pilotStartedDate"));
            Instant lastStartedAt = parseInstant(state.getString("lastStartedAt"));

            validateConfiguration();

            List<? extends Player> eligiblePlayers = Bukkit.getOnlinePlayers().stream()
                    .filter(Player::isConnected)
                    .filter(player -> player.getGameMode() != GameMode.SPECTATOR)
                    .toList();
            Set<String> teams = new HashSet<>();
            for (Player player : eligiblePlayers) {
                String teamId = this.kothManager.getKothTeam().getTeamId(player);
                teams.add(teamId == null || teamId.isBlank() ? player.getUniqueId().toString() : teamId);
            }

            List<String> validArenas = new ArrayList<>();
            for (String arena : Config.dailyKothArenas) {
                if (this.kothManager.getKoth(arena).isPresent()) validArenas.add(arena);
                else this.plugin.getLogger().severe("Daily KOTH pilot references missing arena: " + arena);
            }

            DailyKothPilotPolicy.Decision decision = DailyKothPilotPolicy.evaluate(new DailyKothPilotPolicy.Input(
                    true, true, today, now.getDayOfWeek(), lastEvaluatedDate, pilotStartedDate,
                    Config.dailyKothPilotDurationDays, now.toInstant(), lastStartedAt, Config.dailyKothCooldownHours,
                    !this.kothManager.getStartKoths().isEmpty(), eligiblePlayers.size(),
                    Config.dailyKothWeekdayMinimumPlayers, Config.dailyKothSaturdayMinimumPlayers,
                    teams.size(), Config.dailyKothMinimumTeams, validArenas));

            if (decision == DailyKothPilotPolicy.Decision.ALREADY_EVALUATED) return;
            state.set("lastEvaluatedDate", today.toString());
            if (pilotStartedDate == null) state.set("pilotStartedDate", today.toString());
            saveState(state);

            if (decision != DailyKothPilotPolicy.Decision.START) {
                this.plugin.getLogger().info("Daily KOTH pilot skipped: " + decision
                        + " (players=" + eligiblePlayers.size() + ", teams=" + teams.size() + ")");
                return;
            }

            String lastArena = state.getString("lastArena");
            int consecutive = state.getInt("consecutiveSameArena", 0);
            String arena = DailyKothPilotPolicy.selectArena(validArenas, lastArena, consecutive,
                    ThreadLocalRandom.current().nextInt(), Config.dailyKothMaxConsecutiveSameArena);
            Koth koth = this.kothManager.getKoth(arena).orElseThrow();
            koth.spawn(Bukkit.getConsoleSender(), false);

            if (koth.getStatus() != KothStatus.COOLDOWN) {
                this.plugin.getLogger().severe("Daily KOTH pilot failed to start arena " + arena);
                return;
            }

            int newConsecutive = lastArena != null && arena.equalsIgnoreCase(lastArena) ? consecutive + 1 : 1;
            state.set("lastStartedAt", now.toInstant().toString());
            state.set("lastArena", arena);
            state.set("consecutiveSameArena", newConsecutive);
            state.set("starts", state.getInt("starts", 0) + 1);
            saveState(state);
            this.plugin.getLogger().info("Daily KOTH pilot started " + arena
                    + " (players=" + eligiblePlayers.size() + ", teams=" + teams.size() + ")");
        } catch (DateTimeException | IOException exception) {
            if (!this.invalidConfigurationLogged) {
                this.invalidConfigurationLogged = true;
                this.plugin.getLogger().severe("Daily KOTH pilot disabled by invalid configuration/state: "
                        + exception.getMessage());
            }
        } catch (RuntimeException exception) {
            this.plugin.getLogger().severe("Daily KOTH pilot evaluation failed closed: " + exception.getMessage());
        }
    }

    private void saveState(YamlConfiguration state) throws IOException {
        state.save(this.stateFile);
    }

    private static void validateConfiguration() {
        if (Config.dailyKothWeekdayMinimumPlayers < 1
                || Config.dailyKothSaturdayMinimumPlayers < 1
                || Config.dailyKothMinimumTeams < 1
                || Config.dailyKothCooldownHours < 1
                || Config.dailyKothPilotDurationDays < 1
                || Config.dailyKothMaxConsecutiveSameArena < 1
                || Config.dailyKothArenas.isEmpty()) {
            throw new IllegalArgumentException("dailyKothPilot numeric values and arenas must be positive/non-empty");
        }
    }

    private static LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private static Instant parseInstant(String value) {
        return value == null || value.isBlank() ? null : Instant.parse(value);
    }
}
