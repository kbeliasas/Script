package everything;

import everything.naturalmouse.api.MouseMotionFactory;
import everything.naturalmouse.support.DefaultMouseMotionNature;
import everything.naturalmouse.support.RsMouseInfoAccessor;
import everything.naturalmouse.support.RsSystemCalls;
import everything.naturalmouse.util.FactoryTemplates;
import everything.skills.Combating;
import org.dreambot.api.Client;
import org.dreambot.api.input.mouse.algorithm.StandardMouseAlgorithm;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ScriptManifest(name = "Everything", description = "My script description!", author = "Karolis",
        version = 1.0, category = Category.UTILITY, image = "")
public class Main extends AbstractScript {

    public static States state = States.IDLE;
    public static MouseMotionFactory mouseMotionFactory;
    public static Map<String, Integer> looted = new HashMap<>();
    public static Map<String, Integer> ignored = new HashMap<>();
    public static Skill skillToTrain = Skill.DEFENCE;
    public static int goal = 20;
    private static States cashedState = States.IDLE;

    @Override

    public void onStart() {
        Logger.info("Welcome");
        var nature = new DefaultMouseMotionNature(new RsSystemCalls(), new RsMouseInfoAccessor());
        mouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory(nature);
        Client.getInstance().setMouseMovementAlgorithm(new NaturalMouse());
        SkillTracker.start(skillToTrain);
    }

    @Override
    public int onLoop() {

//        Mining.mine();
//        Smithing.smelt();
//        Smithing.smith();
//        Woodcutting.cut();
//        Crafting.makeLeatherArmor();
//        Bank.open(BankLocation.FALADOR_EAST);
//        Bank.open(BankLocation.VARROCK_WEST);
        Combating.attack();
        stateTracker();
        return Calculations.random(1000, 2000);

//        if (Skills.getRealLevel(Skill.MAGIC) >= 17 || Inventory.count("Mind rune") <= 25) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                Logger.error("Something wrong with thread sleep", e);
//                throw new RuntimeException(e);
//            }
//            Logger.log("Target level reached!");
//            ScriptManager.getScriptManager().stop();
//        }

//        if (!Players.getLocal().isAnimating()) {
//
//            if (state.equals(States.BANKING)) {
//                Banking.putAllItems();
//            }
//            if (Inventory.isFull()) {
////                var portal = GameObjects.closest("Portal");
////                if (portal != null && portal.canReach()) {
////                    portal.interact("Exit");
////                    portal.interact("Use");
////                    if (Main.state.equals(States.BANKING)) {
////                        Walking.walk(portal.getTile());
////                        Logger.warn("Something wrong with teleport: " + portal);
////                        Logger.warn(portal.getActions());
////                    }
////                }
//                state = States.PRAYER;
////                Banking.putAllItems();
//            }

//            if (!Inventory.contains("Raw anchovies")) {
//                Logger.log("Inventory is full");
//                Inventory.dropAll("Burnt fish");

//            RuneCrafting.craft();
//            RuneEssence.mine();
//            everyting.skills.Cooking.cook();
//            everyting.Fishing.fish();

//            Bank.open(BankLocation.FALADOR_EAST);
//            Bank.open(BankLocation.VARROCK_WEST);
//            GrandExchange.open();
//            if (state.equals(States.PRAYER)) {
//                Prayering.buryBones();
//            } else {
//                Combating.attack();
//            }
//        }

    }

    @Override
    public void onPaint(Graphics2D g) {
        var duration = Instant.now().toEpochMilli() - SkillTracker.getStartTime(skillToTrain);
        var timeRunning = String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));
        var levels = String.format("Skill %s. Levels gained: %s.",
                skillToTrain.getName(),
                SkillTracker.getGainedLevels(skillToTrain));
        g.drawString(timeRunning, 5, 35);
        g.drawString(levels, 5, 55);
        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            looted.forEach((lootName, lootCount) ->
                    message.append("Looted : ").append(lootCount).append(" ").append(lootName).append(". "));
            g.drawString(message.toString(), 5, 75);
        }
        if (!ignored.isEmpty()) {
            var message = new StringBuilder();
            ignored.forEach((lootName, lootCount) ->
                    message.append("Ignored : ").append(lootCount).append(" ").append(lootName).append(". "));
            g.drawString(message.toString(), 5, 95);
        }
    }

    @Override
    public void stop() {
        Client.getInstance().setMouseMovementAlgorithm(new StandardMouseAlgorithm());
        if (!looted.isEmpty()) {
            var message = new StringBuilder();
            looted.forEach((lootName, lootCount) ->
                    message.append("Looted : ").append(lootCount).append(" ").append(lootName)
                            .append(". Profit: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K"));
            Logger.info(message.toString());
        }
        if (!ignored.isEmpty()) {
            var message = new StringBuilder();
            ignored.forEach((lootName, lootCount) ->
                    message.append("Ignored : ").append(lootCount).append(" ").append(lootName).append(". ")
                            .append(". Lost: ").append(LivePrices.get(lootName) * lootCount / 1000).append("K"));
            Logger.info(message.toString());
        }
        Sleep.sleep(2000);
    }

    private void stateTracker() {
        if (!state.equals(cashedState)) {
            Logger.info("Stated changed from " + cashedState.name() + " to " + state.name());
            cashedState = state;
        }
    }
}