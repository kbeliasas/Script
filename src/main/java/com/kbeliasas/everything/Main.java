package com.kbeliasas.everything;

import com.kbeliasas.everything.models.Loot;
import com.kbeliasas.everything.naturalmouse.api.MouseMotionFactory;
import com.kbeliasas.everything.naturalmouse.support.DefaultMouseMotionNature;
import com.kbeliasas.everything.naturalmouse.support.RsMouseInfoAccessor;
import com.kbeliasas.everything.naturalmouse.support.RsSystemCalls;
import com.kbeliasas.everything.naturalmouse.util.FactoryTemplates;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ScriptManifest(name = "com.kbeliasas.everything", description = "My script description!", author = "Karolis",
        version = 1.0, category = Category.UTILITY, image = "")
@Getter
@Setter
public class Main extends AbstractScript {

    private Util util;

    public static MouseMotionFactory mouseMotionFactory;
    public static Map<String, Integer> looted = new HashMap<>();
    private Map<Integer, Loot> lootedV2 = new HashMap<>();
    public static Map<String, Integer> ignored = new HashMap<>();
    private Skill skillToTrain;
    private List<Skill> additionalTrackingSkills = new ArrayList<>();
    private String additionalMessage;
    private int goal = 0;
    private int goalXp = 0;
    private int bankedAmount = 0;
    private int profit = 0;
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
        var levels = String.format("Skill %s. Current XP: %s K. Current lvl: %s. Gained XP: %s K. Gained Lvl: %s. Time to next level: %s",
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

        if (!additionalTrackingSkills.isEmpty()) {
            for (Skill skill : additionalTrackingSkills) {
                var timeToLvlSkill = SkillTracker.getTimeToLevel(skill);
                var level = String.format("Skill %s. Current XP: %s K. Current lvl: %s. Gained XP: %s K. Gained Lvl: %s. Time to next level: %s",
                        skill.getName(),
                        Skills.getExperience(skill) / 1000,
                        Skills.getRealLevel(skill),
                        SkillTracker.getGainedExperience(skill) / 1000,
                        SkillTracker.getGainedLevels(skill),
                        String.format("%d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(timeToLvlSkill),
                                (TimeUnit.MILLISECONDS.toMinutes(timeToLvlSkill)) % 60,
                                (TimeUnit.MILLISECONDS.toSeconds(timeToLvlSkill)) % 60));
                g.drawString(level, 5, y += 20);
            }
        }

        var stateMessage = String.format("Current state: %s", stateString);
        g.drawString(stateMessage, 5, y += 20);
        if (profit != 0) {
            var message = new StringBuilder();
            message.append("PROFIT: ");
            message.append(profit / 1000);
            message.append("K");
            g.drawString(message.toString(), 5, y += 20);
        }
        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            message.append("Looted: ");
            looted.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Profit: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            g.drawString(message.toString(), 5, y += 20);
        }

        if (!lootedV2.isEmpty()) {
            var sortedLoot = lootedV2.values().stream()
                    .sorted(Comparator.comparing(Loot::getGenericProfit).reversed())
                    .collect(Collectors.toList());

            var message = new StringBuilder();
            message.append("Looted: ");
            sortedLoot.forEach(loot -> message.append(loot.getMessage()));
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
            lootedV2.forEach((id, loot) -> {
                var itemsPerSecond = (double) duration / loot.getAmount();
                var timeLeft = (long) (itemsPerSecond * (goal));
                message.append(String.format("%d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, (timeLeft % 60)));
            });
            g.drawString(message.toString(), 5, y += 20);
        }

        if (config.isPaintTimeToGoalItemsCollect()) {
            var message = new StringBuilder();

            lootedV2.values().stream()
                    .sorted(Comparator.comparing(Loot::getAmount).reversed())
                    .forEach(loot -> {
                        message.append("Time to goal: ");
                        var itemsPerSecond = (double) duration / loot.getAmount();
                        var timeLeft = (long) (itemsPerSecond * (goal - bankedAmount));
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
                var xpPerSecond = (double) xpPerHour / (60 * 60);
                var xpLeft = goalXp - Skills.getExperience(skillToTrain);
                var timeLeft = (long) (xpLeft / xpPerSecond);
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

        if (!StringUtils.isEmpty(additionalMessage)) {
            g.drawString(additionalMessage, 5, y += 20);
        }
    }

    public void printResults() {
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

    public void showResults() {
        var results = getResults();
        SwingUtilities.invokeLater(() -> new ResultsGUI(results));
    }

    public void addLoot(Integer id, String name) {
        addLoot(id, name, 1);
    }

    public void addLoot(Integer id, String name, Integer amount) {
        if (lootedV2.containsKey(id)) {
            var loot = lootedV2.get(id);
            loot.setAmount(loot.getAmount() + amount);
            lootedV2.put(id, loot);
        } else {
            var loot = new Loot();
            loot.setId(id);
            loot.setAmount(amount);
            loot.setName(name);
            lootedV2.put(id, loot);
        }
    }

    private List<String> getResults() {
        var result = new ArrayList<String>();

        addLevelResults(result, skillToTrain);
        additionalTrackingSkills.forEach(skill -> addLevelResults(result, skill));

        var duration = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        var timeRan = String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
        result.add("Time ran: " + timeRan);

        if (profit != 0) {
            var message = "PROFIT: " + profit / 1000 + "K";
            result.add(message);
        }

        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            message.append("Looted: ");
            looted.forEach((lootName, lootCount) ->
                    message.append(lootCount).append(" ").append(lootName)
                            .append(". Profit: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K "));
            result.add(message.toString());
        }

        if (!lootedV2.isEmpty()) {
            result.add("Looted :");

            var sortedLoot = lootedV2.values().stream()
                    .sorted(Comparator.comparing(Loot::getGenericProfit).reversed())
                    .collect(Collectors.toList());

            sortedLoot.forEach(loot -> result.add(loot.getMessage()));
        }

        return result;
    }

    private void addLevelResults(ArrayList<String> result, Skill skill) {
        var level = "Level Trained: " + skill.getName();
        result.add(level);

        var gainedExperience = "Gained XP: " + SkillTracker.getGainedExperience(skill) / 1000 + "K";
        result.add(gainedExperience);

        var gainedLeveles = "Gained Levels: " + SkillTracker.getGainedLevels(skill);
        result.add(gainedLeveles);

        var currentXP = "Current XP: " + Skills.getExperience(skill) / 1000 + "K";
        result.add(currentXP);

        var currentLevel = "Current Level: " + Skills.getRealLevel(skill);
        result.add(currentLevel);

        var xpRate = SkillTracker.getGainedExperiencePerHour(skill);
        var xpRateMessage = "XP rate: " + xpRate / 1000 + " K per hour";
        result.add(xpRateMessage);
    }
}