package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Combating {

    private static final Area CHICKENS = new Area(3231, 3296, 3235, 3290);
    private static final Area COWS = new Area(3254, 3293, 3264, 3256);
    private static final Area LESSER_DEMON = new Area(3108, 3163, 3111, 3159, 2);
    private static final Area LUMBRIDGE_SWAMP = new Area(3158, 3193, 3230, 3168);
    private static final Area HILL_GIANTS_EDGEVILLE = new Area(3110, 9847, 3120, 9834);

    public static final String FOOD = "trout";
    public static final int FOOD_AMOUNT = 4;
    private static boolean finishGame = false;
    private static boolean noMoreFood = false;

    public static void attack() {

        if (!Players.getLocal().isAnimating()) {

            if (Players.getLocal().isInCombat() && !noMoreFood) {
                Main.state = States.COMBATING;
                if (Skills.getBoostedLevel(Skill.HITPOINTS) <= 15) {
                    Inventory.interact(food -> food.getName().toLowerCase(Locale.ROOT).contains(FOOD));
                }
                return;
            }

            if (Skills.getRealLevel(Main.skillToTrain) >= Main.goal || finishGame) {
                Logger.log("Target level reached!");
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Main.printResults();
                    ScriptManager.getScriptManager().stop();
                }
            }

            if (Inventory.isFull()) {
                Main.state = States.PRAYER;
            }

            if (Inventory.count(FOOD) <= 3 && Main.state.equals(States.IDLE)) {
                Main.state = States.PRAYER;
                noMoreFood = true;
            }

            if (Main.state.equals(States.PRAYER)) {
                Prayering.buryBones();
                if (Main.state.equals(States.IDLE)
                        && (Inventory.emptySlotCount() < 5 || noMoreFood)) {
                    Main.state = States.BANKING;
                }
                return;
            }

            if (Main.state.equals(States.BANKING)) {
                if (Banking.openBank()) {
                    Bank.depositAllExcept(item -> item.getName().toLowerCase(Locale.ROOT).contains(FOOD)
                            || item.getName().toLowerCase(Locale.ROOT).contains("brass key"));
                    var needMore = FOOD_AMOUNT - Inventory.count(FOOD);
                    Bank.withdraw(FOOD, needMore);
                    if (Bank.count(FOOD) <= FOOD_AMOUNT) {
                        finishGame = true;
                    }
//                    if (Bank.count("Cowhide") >= Main.goal) {
//                        finishGame = true;
//                    }
                    Main.state = States.IDLE;
                    noMoreFood = false;
                }
                return;
            }


//            var item = GroundItems.closest("Coins", "Bones", "Cowhide");
//            if (item != null && item.distance() < 3) {
//                item.interact("Take");
//                Main.state = States.IDLE;
            var loot = getLoot();
            if (!loot.isEmpty()) {
                loot.forEach(item -> {
                    item.interact("Take");
                    Sleep.sleep(Calculations.random(800, 1500));
                    Main.state = States.IDLE;
                });
            } else {
                Logger.log("Finding npc");
                var npc = getMob();
                if (npc != null) {
                    npc.interact("Attack");
                } else
                    Walking.walk(HILL_GIANTS_EDGEVILLE.getRandomTile());
            }
        }
    }

    public static void magic() {
        if (!Players.getLocal().isAnimating()) {

            if (Players.getLocal().isInCombat() && !noMoreFood) {
                Main.state = States.COMBATING;
                if (Skills.getBoostedLevel(Skill.HITPOINTS) <= 15) {
                    Inventory.interact(food -> food.getName().toLowerCase(Locale.ROOT).contains(FOOD));
                }
                return;
            }

            if (Skills.getRealLevel(Main.skillToTrain) >= Main.goal
                    || finishGame
                    || Inventory.count(rune -> rune.getName().toLowerCase(Locale.ROOT).contains("mind")) <= 30) {
                Logger.log("Target level reached!");
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Main.printResults();
                    ScriptManager.getScriptManager().stop();
                }
            }

            if (Inventory.isFull()) {
                Main.state = States.PRAYER;
            }

            if (Inventory.count(FOOD) <= 3 && Main.state.equals(States.IDLE)) {
                Main.state = States.PRAYER;
                noMoreFood = true;
            }

            if (Main.state.equals(States.PRAYER)) {
                Prayering.buryBones();
                if (Main.state.equals(States.IDLE)
                        && (Inventory.emptySlotCount() < 5 || noMoreFood)) {
                    Main.state = States.BANKING;
                }
                return;
            }

            if (Main.state.equals(States.BANKING)) {
                if (Banking.openBank()) {
                    Bank.depositAllExcept(item -> item.getName().toLowerCase(Locale.ROOT).contains(FOOD)
                            || item.getName().toLowerCase(Locale.ROOT).contains("mind"));
                    var needMore = FOOD_AMOUNT - Inventory.count(FOOD);
                    Bank.withdraw(FOOD, needMore);
                    if (Bank.count(FOOD) <= FOOD_AMOUNT) {
                        finishGame = true;
                    }
//                    if (Bank.count("Cowhide") >= Main.goal) {
//                        finishGame = true;
//                    }
                    Main.state = States.IDLE;
                    noMoreFood = false;
                }
                return;
            }


//            var item = GroundItems.closest("Coins", "Bones", "Cowhide");
//            if (item != null && item.distance() < 3) {
//                item.interact("Take");
//                Main.state = States.IDLE;
            var loot = getLoot();
            if (!loot.isEmpty() && false) {
                loot.forEach(item -> {
                    item.interact("Take");
                    Sleep.sleep(Calculations.random(800, 1500));
                    Main.state = States.IDLE;
                });
            } else {
                Logger.log("Finding npc");
                var npc = getMob();
                if (npc != null) {
                    npc.interact("Attack");
                } else
                    Walking.walk(LESSER_DEMON.getRandomTile());
            }
        }
    }


    private static NPC getMob() {
        return NPCs.closest(npc -> npc.getName().toLowerCase(Locale.ROOT).contains("demon")
//                && npc.canReach()
//                && !npc.isInCombat()
                && npc.distance() < 5);
    }

    private static ArrayList<GroundItem> getLoot() {
        var itemsFiltered = new ArrayList<GroundItem>();
        var items = getGroundItems();
        items.forEach(item -> {
            var price = LivePrices.get(item.getID());
            if (item.getName().equalsIgnoreCase("Coins")
                    || item.getName().toLowerCase(Locale.ROOT).contains("bones")
                    || item.getName().toLowerCase(Locale.ROOT).contains("rune")
                    || item.getName().equalsIgnoreCase("Cowhide")
                    || item.getName().toLowerCase(Locale.ROOT).contains("clue")
                    || item.getName().toLowerCase(Locale.ROOT).contains("key")) {
                itemsFiltered.add(item);
                Logger.info("Looted: " + item.getName() + ". Manually included from List");
                Util.addLoot(item.getName());
            } else if (price > 1000) {
                itemsFiltered.add(item);
                Logger.info("Looted: " + item.getName() + " for :" + price);
                Util.addLoot(item.getName());
            } else {
                Logger.info("ignored: " + item.getName() + " for :" + price);
                Util.addIgnored(item.getName());
            }
        });
        return itemsFiltered;
    }

    private static List<GroundItem> getGroundItems() {
        return GroundItems.all(item -> item.distance() < 5
                && item.canReach());
//        if (groundItems != null && !groundItems.isEmpty()) {
//            var tiles = new ArrayList<Tile>();
//            groundItems.forEach(groundItem -> tiles.add(groundItem.getTile()));
//            return tiles;
//        }
//        return null;
    }
}
