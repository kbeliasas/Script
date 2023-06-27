package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

public class Woodcutting {

    static Area OAK = new Area(3167, 3426, 3172, 3416);
    static Area WILLOW = new Area(3084, 3238, 3092, 3229);
    static String TREE = "willow";


    public static void cut() {
        if (!Players.getLocal().isAnimating()) {

            if (Skills.getRealLevel(Skill.WOODCUTTING) >= Main.goal) {
                Logger.log("Target level reached!");
                Main.printResults();
                ScriptManager.getScriptManager().stop();
            }

            if (Inventory.isFull()) {
                Main.state = States.BANKING;
                if (Banking.openBank()) {
                    var logs = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(TREE));
                    var amount = Inventory.count(logs.getID());
                    Util.addLoot(logs.getName(), amount);
                    Bank.depositAllItems();
                    Main.state = States.IDLE;
                    Sleep.sleep(Calculations.random(500, 1000));
                    Main.bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(TREE));
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(WILLOW.getRandomTile());
                var tree = getCloset();
                if (tree != null) {
                    Main.state = States.WOODCUTTING;
                    tree.interact("Chop Down");
                }
            }

            if (Main.state.equals(States.WOODCUTTING)) {
                var tree = getCloset();
                if (tree != null) {
                    tree.interact("Chop Down");
                }
            }
        }
    }

    private static GameObject getCloset() {
        return GameObjects.closest(object ->
                object.getName().toLowerCase(Locale.ROOT).contains(TREE)
                        && object.canReach()
                        && object.hasAction("Chop Down")
                        && object.distance() <= 20);
    }
}
