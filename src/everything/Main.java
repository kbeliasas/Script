package everything;

import everything.naturalmouse.api.MouseMotionFactory;
import everything.naturalmouse.support.DefaultMouseMotionNature;
import everything.naturalmouse.support.RsMouseInfoAccessor;
import everything.naturalmouse.support.RsSystemCalls;
import everything.naturalmouse.util.FactoryTemplates;
import everything.skills.*;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "Everything", description = "My script description!", author = "Karolis",
        version = 1.0, category = Category.UTILITY, image = "")
public class Main extends AbstractScript {

    public static States state = States.IDLE;
    public static MouseMotionFactory mouseMotionFactory;
    public static Map<String, Integer> looted = new HashMap<>();
    public static Map<String, Integer> ignored = new HashMap<>();
    public static Skill skillToTrain = Skill.COOKING;
    public static int goal = 0;
    public static int goalXp = 22406;
    public static int bankedAmount = 0;
    public static boolean paintTimeToGoalItems = true;
    public static boolean paintTimeToGoalLevels = false;
    private static States cashedState = States.IDLE;
    private static Instant startTime;

    @Override

    public void onStart() {
        Logger.info("Welcome");
        var nature = new DefaultMouseMotionNature(new RsSystemCalls(), new RsMouseInfoAccessor());
        mouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory(nature);
        Client.getInstance().setMouseMovementAlgorithm(new NaturalMouse());
        SkillTracker.start(skillToTrain);
        startTime = Instant.now();
    }

    @Override
    public int onLoop() {
//        Bank.open(BankLocation.FALADOR_EAST);
        Cooking.cook();
        stateTracker();
        return Calculations.random(1000, 2000);
    }

    @Override
    public void onPaint(Graphics2D g) {
        var y = 15;
        var duration = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        var timeToLvl = SkillTracker.getTimeToLevel(skillToTrain);
        var timeRunning = String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
        var levels = String.format("Skill %s. Levels gained: %s. Time to next level: %s",
                skillToTrain.getName(),
                SkillTracker.getGainedLevels(skillToTrain),
                String.format("%d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timeToLvl),
                        TimeUnit.MILLISECONDS.toMinutes(timeToLvl),
                        (TimeUnit.MILLISECONDS.toSeconds(timeToLvl)) % 60));
        g.drawString(timeRunning, 5, y += 20);
        g.drawString(levels, 5, y += 20);
        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            message.append("Looted: ");
            looted.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Profit: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            g.drawString(message.toString(), 5, y += 20);
        }
        if (!ignored.isEmpty()) {
            var message = new StringBuilder();
            message.append("Ignored: ");
            ignored.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Lost: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            g.drawString(message.toString(), 5, y += 20);
        }
        if (paintTimeToGoalItems) {
            var message = new StringBuilder();
            message.append("Time to goal: ");
            looted.forEach((lootName, lootCount) -> {
                var itemsPerSecond = duration / lootCount;
//                message.append("Items per Second: ").append(itemsPerSecond);
                var timeLeft = itemsPerSecond * (Main.goal);
//                message.append(" timeLeft: ").append(timeLeft);
                message.append(String.format("%d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, (timeLeft % 60)));
            });
            g.drawString(message.toString(), 5, y += 20);

//            var bankedMessage = new StringBuilder();
//            bankedMessage.append("Banked amount: ");
//            bankedMessage.append(bankedAmount);
//            g.drawString(bankedMessage.toString(), 5, y += 20);
        }

        if (paintTimeToGoalLevels) {
            var message = new StringBuilder();
            message.append("Time to goal: ");
            var xpPerHour = SkillTracker.getGainedExperiencePerHour(skillToTrain);
            if (xpPerHour > 0) {
                var xpPerSecond = xpPerHour / (60 * 60);
                var xpLeft = goalXp - Skills.getExperience(skillToTrain);
                var timeLeft = xpLeft / xpPerSecond;
                if (xpLeft > 0) {
                    message.append(String.format("%d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, (timeLeft % 60)));
                }
                g.drawString(message.toString(), 5, y += 20);
            }

            String bankedMessage = "Banked amount: " + bankedAmount;
            g.drawString(bankedMessage, 5, y += 20);
        }
    }

    public static void printResults() {
        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            message.append("Looted : ");
            looted.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Profit: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            Logger.info(message.toString());
        }
        if (!ignored.isEmpty()) {
            var message = new StringBuilder();
            message.append("Ignored : ");
            ignored.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Lost: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            Logger.info(message.toString());
        }
    }

    private void stateTracker() {
        if (!state.equals(cashedState)) {
            Logger.info("Stated changed from " + cashedState.name() + " to " + state.name());
            cashedState = state;
        }
    }
}