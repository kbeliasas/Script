package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Locale;

public class Fishing {

    private static final Area FISHING_SPOT_LUMBRIDGE = new Area(3238, 3152, 3239, 3144);
    private static final Area FISHING_SPOT_BARBARIAN_VILLAGE = new Area(3100, 3427, 3103, 3424);
    private static final Area MUSA_POINT = new Area(2923, 3180, 2925, 3175);
    private static final String FISH = "raw";

    public static void fish() {
        if (!Players.getLocal().isAnimating()) {

            if (Skills.getExperience(Skill.FISHING) >= Main.goalXp) {
                if (Banking.openBank()) {
                    var fish = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH));
                    var amount = Inventory.count(fish.getID());
                    Util.addLoot(fish.getName(), amount);
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Main.printResults();
                    ScriptManager.getScriptManager().stop();
                    return;
                }
            }

            if (Inventory.isFull()) {
                Main.state = States.BANKING;
            }

            if (Main.state.equals(States.BANKING)) {
                if (Banking.openBank()) {
                    var fish = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH));
                    var amount = Inventory.count(fish.getID());
                    Util.addLoot(fish.getName(), amount);
                    Bank.depositAllExcept(item -> item.getName().toLowerCase(Locale.ROOT).contains("pot")
                            || item.getName().toLowerCase(Locale.ROOT).contains("coins"));
                    Sleep.sleep(Calculations.random(500, 1000));
                    Main.bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(FISH));
                    Main.state = States.IDLE;
                    return;
                }
            }

            if (!Main.state.equals(States.BANKING)) {

                var spot = getFishingSpot();
                if (spot != null) {
                    Main.state = States.FISHING;
                    spot.interact("Cage");
                } else {
                    Main.state = States.WALKING;
                    Walking.walk(MUSA_POINT.getRandomTile());
                }
            }
        }
    }

    private static NPC getFishingSpot() {
        return NPCs.closest(spot ->
                spot.getName().toLowerCase(Locale.ROOT).contains("fishing spot")
                        && spot.distance() <= 15
                        && spot.hasAction("Cage")
        );
    }

}
