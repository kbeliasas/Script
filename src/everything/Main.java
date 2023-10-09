package everything;

import everything.naturalmouse.api.MouseMotionFactory;
import everything.naturalmouse.support.DefaultMouseMotionNature;
import everything.naturalmouse.support.RsMouseInfoAccessor;
import everything.naturalmouse.support.RsSystemCalls;
import everything.naturalmouse.util.FactoryTemplates;
import everything.skills.GenericSkill;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "Everything", description = "My script description!", author = "Karolis",
        version = 1.0, category = Category.UTILITY, image = "")
public class Main extends AbstractScript {

    private Util util;

    public static States state = States.IDLE;
    public static MouseMotionFactory mouseMotionFactory;
    public static Map<String, Integer> looted = new HashMap<>();
    public static Map<String, Integer> ignored = new HashMap<>();
    private Skill skillToTrain;
    public static int goal = 158;
    public static int goalXp = 100000; //49
    public static int bankedAmount = 0;
    private static States cashedState = States.IDLE;
    private Instant startTime;
    private GenericSkill genericSkill;
    private Config config;
    private boolean start = false;
    private String stateString = "None";

    @Override
    public void onStart() {
        Logger.info("Welcome");
        var nature = new DefaultMouseMotionNature(new RsSystemCalls(), new RsMouseInfoAccessor());
        mouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory(nature);
//        Mouse.setMouseAlgorithm(new NaturalMouse());
        Client.getInstance().setMouseMovementAlgorithm(new NaturalMouse());
        startTime = Instant.now();
        util = new Util();
        SwingUtilities.invokeLater(() -> new GUI(this));
    }

    @Override
    public int onLoop() {
        if (!start) {
            return Calculations.random(1000, 2000);
        }
        genericSkill.execute();
//        stateTracker();
//        turnOnRun();
        return Calculations.random(1000, 2000);
    }

    @Override
    public void onPaint(Graphics2D g) {
        if (!start) {
            return;
        }
        var y = 15;
        var duration = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        var timeToLvl = SkillTracker.getTimeToLevel(skillToTrain);
        var timeRunning = String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
        var levels = String.format("Skill %s. Current XP: %s K. Current lvl: %s. XP gained: %s K. Lvl gained: %s. Time to next level: %s",
                skillToTrain.getName(),
                Skills.getExperience(skillToTrain) / 1000,
                Skills.getRealLevel(skillToTrain),
                SkillTracker.getGainedExperience(skillToTrain) / 1000,
                SkillTracker.getGainedLevels(skillToTrain),
                String.format("%d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timeToLvl),
                        (TimeUnit.MILLISECONDS.toMinutes(timeToLvl)) % 60,
                        (TimeUnit.MILLISECONDS.toSeconds(timeToLvl)) % 60));
        g.drawString(timeRunning, 5, y += 20);
        g.drawString(levels, 5, y += 20);
        var stateMessage = String.format("Current state: %s", stateString);
        g.drawString(stateMessage, 5, y += 20);
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
        if (config.isPaintTimeToGoalItems()) {
            var message = new StringBuilder();
            message.append("Time to goal: ");
            looted.forEach((lootName, lootCount) -> {
                var itemsPerSecond = duration / lootCount;
                var timeLeft = itemsPerSecond * (Main.goal);
                message.append(String.format("%d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, (timeLeft % 60)));
            });
            g.drawString(message.toString(), 5, y += 20);
        }

        if (config.isPaintTimeToGoalItemsCollect()) {
            var message = new StringBuilder();
            looted.forEach((lootName, lootCount) -> {
                message.append("Time to goal: ");
                var itemsPerSecond = duration / lootCount;
                var timeLeft = itemsPerSecond * (goal - bankedAmount);
                message.append(String.format("%d:%02d:%02d ", timeLeft / 3600, (timeLeft % 3600) / 60, (timeLeft % 60)));
            });
            g.drawString(message.toString(), 5, y += 20);

            String bankedMessage = "Banked amount: " +
                    bankedAmount;
            g.drawString(bankedMessage, 5, y += 20);
        }

        if (config.isPaintTimeToGoalLevels()) {
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
        var xpRate = SkillTracker.getGainedExperiencePerHour(skillToTrain);
        var message = "XP rate: " + xpRate / 1000 + " K per hour";
        g.drawString(message, 5, y += 20);
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

    private void turnOnRun() {
        if (!Walking.isRunEnabled()) {
            if (Walking.getRunEnergy() >= 35) {
                Walking.toggleRun();
            }
        }
    }

    public Skill getSkillToTrain() {
        return skillToTrain;
    }

    public void setSkillToTrain(Skill skillToTrain) {
        this.skillToTrain = skillToTrain;
    }

    public void setGenericSkill(GenericSkill genericSkill) {
        this.genericSkill = genericSkill;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void setStateString(String stateString) {
        this.stateString = stateString;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getGoalXp() {
        return goalXp;
    }

    public void setGoalXp(int goal) {
        goalXp = goal;
    }

    public Util getUtil() {
        return util;
    }
}