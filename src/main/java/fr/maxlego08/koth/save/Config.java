package fr.maxlego08.koth.save;

import fr.maxlego08.koth.KothPlugin;
import fr.maxlego08.koth.api.utils.PlayerResult;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;

public class Config {

	public static boolean enableDebug = true;
	public static boolean enableDebugTime = false;
	public static long playerMoveEventCooldown = 50;
	public static long schedulerMillisecond = 1000;
	public static List<Integer> displayMessageCooldown = Arrays.asList(300, 120, 60, 30, 10, 5, 4, 3, 2, 1);
	public static List<Integer> displayMessageKothCap = Arrays.asList(300, 120, 60, 30, 10, 5, 4, 3, 2, 1);
    public static String noPlayer = "X";
    public static String noFaction = "X";
    public static String noKoth = "X";
	public static int removeChestSec = 120;
	public static boolean enableCapturePermission = false;
	public static String capturePermission = "zkoth.capture";
	public static boolean dailyKothEnabled = false;
	public static String dailyKothTimezone = "Europe/Amsterdam";
	public static String dailyKothEvaluationTime = "19:55";
	public static int dailyKothWeekdayMinimumPlayers = 8;
	public static int dailyKothSaturdayMinimumPlayers = 4;
	public static int dailyKothMinimumTeams = 2;
	public static int dailyKothCooldownHours = 24;
	public static int dailyKothPilotDurationDays = 14;
	public static List<String> dailyKothArenas = Arrays.asList("NORTH_OUTPOST", "South_Outpost");
	public static int dailyKothMaxConsecutiveSameArena = 2;

	public static PlayerResult defaultPlayerResult = new PlayerResult("X", 0, "X", "X", "X");

    /**
	 * static Singleton instance.
	 */
	private static volatile Config instance;


	/**
	 * Private constructor for singleton.
	 */
	private Config() {
	}

	/**
	 * Return a singleton instance of Config.
	 */
	public static Config getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	public void load(KothPlugin plugin) {

		YamlConfiguration configuration = (YamlConfiguration) plugin.getConfig();

		enableDebug = configuration.getBoolean("enableDebug", false);
		enableDebugTime = configuration.getBoolean("enableDebugTime", false);
		playerMoveEventCooldown = configuration.getInt("playerMoveEventCooldown", 50);

		displayMessageCooldown = configuration.getIntegerList("displayMessageCooldown");
		displayMessageKothCap = configuration.getIntegerList("displayMessageKothCap");
		noPlayer = configuration.getString("noPlayer", "X");
		noFaction = configuration.getString("noFaction", "X");
		noKoth = configuration.getString("noKoth", "X");
		schedulerMillisecond = configuration.getLong("schedulerMillisecond", 1000);
		removeChestSec = configuration.getInt("removeChestSec", 120);
		enableCapturePermission = configuration.getBoolean("enableCapturePermission", false);
		capturePermission = configuration.getString("capturePermission", "zkoth.capture");
		dailyKothEnabled = configuration.getBoolean("dailyKothPilot.enabled", false);
		dailyKothTimezone = configuration.getString("dailyKothPilot.timezone", "Europe/Amsterdam");
		dailyKothEvaluationTime = configuration.getString("dailyKothPilot.evaluationTime", "19:55");
		dailyKothWeekdayMinimumPlayers = configuration.getInt("dailyKothPilot.weekdayMinimumPlayers", 8);
		dailyKothSaturdayMinimumPlayers = configuration.getInt("dailyKothPilot.saturdayMinimumPlayers", 4);
		dailyKothMinimumTeams = configuration.getInt("dailyKothPilot.minimumTeams", 2);
		dailyKothCooldownHours = configuration.getInt("dailyKothPilot.cooldownHours", 24);
		dailyKothPilotDurationDays = configuration.getInt("dailyKothPilot.pilotDurationDays", 14);
		dailyKothArenas = configuration.getStringList("dailyKothPilot.arenas");
		dailyKothMaxConsecutiveSameArena = configuration.getInt("dailyKothPilot.maxConsecutiveSameArena", 2);

		defaultPlayerResult = new PlayerResult(
				configuration.getString("defaultPlayerResult.playerName", "X"),
				configuration.getInt("defaultPlayerResult.points", 0),
				configuration.getString("defaultPlayerResult.teamName", "X"),
				configuration.getString("defaultPlayerResult.teamId", "X"),
				configuration.getString("defaultPlayerResult.teamLeader", "X")
		);
	}

}
