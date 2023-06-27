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
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

public class Mining {

    static Area SOUTH_EAST_VARROCK_MINE = new Area(3285, 3372, 3289, 3369);
    static Area SOUTH_WEST_VARROCK_MINE = new Area(3183, 3374, 3173, 3365);
    static Area AL_KHARID_MINE = new Area(3297, 3315, 3301, 3302);
    static String TIN_ROCK_NAME = "Tin rocks";
    static String COPPER_NAME = "Copper rocks";
    static String IRON_NAME = "Iron rocks";
    static String SILVER_NAME = "Silver rocks";
    static String ORE = "iron ore";

    public static void mine() {

        if (!Players.getLocal().isAnimating()) {
            if (Inventory.isFull()) {
                Main.state = States.BANKING;
                if (Banking.openBank()) {
                    var ore = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("ore"));
                    if (ore != null) {
                        var amount = Inventory.count(ore.getID());
                        Util.addLoot(ore.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    var bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(ORE));
                    Main.bankedAmount = bankedAmount;
                    if (bankedAmount >= Main.goal) {
                        Main.printResults();
                        Bank.close();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.state = States.IDLE;
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(SOUTH_EAST_VARROCK_MINE.getRandomTile());
                var ore = getClosest();
                if (ore != null) {
                    Main.state = States.MINING;
                    if (ore.interact("Mine")) {
                        Sleep.sleep(Calculations.random(200, 300));
                    }
                }
            }

            if (Main.state.equals(States.MINING)) {
                var ore = getClosest();
                if (ore != null) {
                    if (ore.interact("Mine")) {
                        Sleep.sleep(Calculations.random(200, 300));
                    }
                }
            }
        }
    }

    private static GameObject getClosest() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase(IRON_NAME)
                        && object.hasAction("Mine")
                        && object.distance() <= 20
                        && object.getModelColors() != null
        );
    }
}
