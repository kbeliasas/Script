package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

import static org.dreambot.api.methods.widget.helpers.Smithing.*;

public class Smithing {

    static Area FURNACE = new Area(3105, 3501, 3109, 3496);
    static Area ANVIL = new Area(3185, 3427, 3190, 3420);
    static String ORE = "silver ore";
    static String BAR = "silver bar";
    static String PRODUCT = "platebody";
    static int ORES_PER_BAR = 1;

    public static void smelt() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE))) {
                Main.state = States.BANKING;
                if (Banking.openBank()) {
                    var bar = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("bar"));
                    if (bar != null) {
                        var amount = Inventory.count(bar.getID());
                        Util.addLoot(bar.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE))) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Bank.withdrawAll(ORE);
//                    Bank.withdraw(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE), 14);
                    Sleep.sleep(Calculations.random(500, 1000));
                    Main.goal = Bank.count(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE)) / ORES_PER_BAR;
                    Main.state = States.IDLE;
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(FURNACE.getRandomTile());
                var furnace = getClosestFurnace();
                if (furnace != null) {
                    Main.state = States.SMITHING;
                    if (furnace.interact("Smelt")) {
                        Sleep.sleep(Calculations.random(1500, 1800));
                        Keyboard.typeSpecialKey(32); //org.dreambot.api.input.Keyboard
                    }
                }
            }

            if (Main.state.equals(States.SMITHING) && Dialogues.inDialogue()) {
                var furnace = getClosestFurnace();
                if (furnace != null) {
                    if (furnace.interact("Smelt")) {
                        Sleep.sleep(Calculations.random(1500, 1800));
                        Keyboard.typeSpecialKey(32);
                    }
                }
            }
        }
    }

    public static void smeltTiara() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains(BAR)) {
                if (Banking.openBank()) {
                    var tiaras = Inventory.get(tiara ->
                            tiara.getName().toLowerCase(Locale.ROOT).contains("tiara")
                                    && !tiara.getName().toLowerCase(Locale.ROOT).contains("mould"));
                    if (tiaras != null) {
                        var amount = Inventory.count(tiaras.getID());
                        Util.addLoot(tiaras.getName(), amount);
                    }
                    Bank.depositAllExcept("Tiara mould");
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(BAR)) {
                        Logger.log("Goal reached");
                        Main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.goal = Bank.count(bar -> bar.getName().toLowerCase(Locale.ROOT).contains(BAR));
                    Bank.withdrawAll(BAR);
                    Main.state = States.IDLE;
                }
            }

            if (Main.state.equals(States.SMITHING) && Dialogues.inDialogue()) {
                var furnace = getClosestFurnace();
                if (furnace != null) {
                    furnace.interact("Smelt");
                    Sleep.sleep(Calculations.random(1500, 1800));
                    var widget = Widgets.get(6, 28, 3);
                    if (widget != null) {
                        widget.interact();
                    }
                }
            }
            if (Main.state.equals(States.SMITHING)) {
                var widget = Widgets.get(6, 28, 3);
                if (widget != null) {
                    var furnace = getClosestFurnace();
                    if (furnace != null) {
                        furnace.interact("Smelt");
                        Sleep.sleep(Calculations.random(1500, 1800));
                        widget.interact();
                    }
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(FURNACE.getRandomTile());
                var furnace = getClosestFurnace();
                if (furnace != null) {
                    Main.state = States.SMITHING;
                    if (furnace.interact("Smelt")) {
                        Sleep.sleep(Calculations.random(1500, 1800));
                        var widget = Widgets.get(6, 28, 3);
                        if (widget != null) {
                            widget.interact();
                        }
//                        Keyboard.typeSpecialKey(32);
                    }
                }
            }
        }
    }

    public static void smith() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains(bar -> bar.getName().toLowerCase(Locale.ROOT).contains(BAR))) {
                Main.state = States.BANKING;
                if (Banking.openBank()) {
                    var product = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains(PRODUCT));
                    if (product != null) {
                        var amount = Inventory.count(product.getID());
                        Util.addLoot(product.getName(), amount);
                    }
                    Bank.depositAllExcept(hammer -> hammer.getName().toLowerCase(Locale.ROOT).contains("hammer"));
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(bar -> bar.getName().toLowerCase(Locale.ROOT).contains(BAR))) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.goal = Bank.count(bar -> bar.getName().toLowerCase(Locale.ROOT).contains(BAR)) / 5;
                    Bank.withdraw(bar -> bar.getName().toLowerCase(Locale.ROOT).contains(BAR), 25);

                    Main.state = States.IDLE;
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(ANVIL.getRandomTile());
                var anvil = getClosestAnvil();
                if (anvil != null && anvil.interact("Smith")) {
                    Main.state = States.SMITHING;
                    Sleep.sleep(Calculations.random(4000, 5000));
                    makeAll(item -> item.getName().toLowerCase(Locale.ROOT).contains(PRODUCT));
                }
            }

            if (Main.state.equals(States.SMITHING) && Dialogues.inDialogue()) {
                var anvil = getClosestAnvil();
                if (anvil != null && anvil.interact("Smith")) {
                    Sleep.sleep(Calculations.random(4000, 5000));
                    makeAll(item -> item.getName().toLowerCase(Locale.ROOT).contains(PRODUCT));
                }
            }
        }
    }

    private static GameObject getClosestFurnace() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase("Furnace")
                        && object.hasAction("Smelt")
                        && object.distance() <= 10
        );
    }

    private static GameObject getClosestAnvil() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase("Anvil")
                        && object.hasAction("Smith")
                        && object.distance() <= 5
                        && object.canReach()
        );
    }
}
