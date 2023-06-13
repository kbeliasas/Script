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
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.ArrayList;
import java.util.Locale;

public class Combating {

    private static final Area CHICKENS = new Area(3231, 3296, 3235, 3290);
    private static final Area COWS = new Area(3254, 3293, 3264, 3256);

    public static void attack() {

        if (!Players.getLocal().isAnimating()) {

            if (Players.getLocal().isInCombat()) {
                Main.state = States.COMBATING;
                return;
            }

            if (Skills.getRealLevel(Main.skillToTrain) >= Main.goal) {
                Logger.log("Target level reached!");
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    Sleep.sleep(Calculations.random(800, 1200));
                    ScriptManager.getScriptManager().stop();
                }
            }

            if (Inventory.isFull()) {
                Main.state = States.PRAYER;
            }

            if (Main.state.equals(States.PRAYER)) {
                Prayering.buryBones();
                if (Main.state.equals(States.IDLE) && Inventory.emptySlotCount() < 5) {
                    Main.state = States.BANKING;
                }
                return;
            }

            if (Main.state.equals(States.BANKING)) {
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Main.state = States.IDLE;
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
                    Walking.walk(COWS.getRandomTile());
            }
        }
    }


    private static NPC getMob() {
        return NPCs.closest(npc -> npc.getName().equalsIgnoreCase("Cow")
                && npc.canReach()
                && !npc.isInCombat()
                && npc.distance() < 15);
    }

    private static ArrayList<GroundItem> getLoot() {
        var tile = getLootTile();
        if (tile == null) {
            return new ArrayList<>();
        }
        var items = GroundItems.getForTile(tile);
        var itemsFiltered = new ArrayList<GroundItem>();
        items.forEach(item -> {
            var price = LivePrices.get(item.getID());
            if (item.getName().equalsIgnoreCase("Coins")
                    || item.getName().equalsIgnoreCase("Bones")
                    || item.getName().equalsIgnoreCase("Cowhide")
                    || item.getName().toLowerCase(Locale.ROOT).contains("clue")) {
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

    private static Tile getLootTile() {
        return GroundItems.closest(item -> item.distance() < 5
                        && item.canReach())
                .getTile();
    }
}
