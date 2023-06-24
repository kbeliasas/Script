package everything.skills;

import everything.Main;
import everything.States;
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

public class Smithing {

    static Area FURNACE = new Area(3105, 3501, 3109, 3496);
    static Area ANVIL = new Area(3185, 3427, 3190, 3420);
    static String ORE = "Silver ore";
    static String BAR = "Silver bar";

    public static void smelt() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains(ORE)) {
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(ORE)) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Bank.withdrawAll(ORE);
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
                        Keyboard.typeSpecialKey(32);
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
                    Bank.depositAllExcept("Tiara mould");
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(BAR)) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Bank.withdrawAll(BAR);
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
                        var widget = Widgets.get(6, 28, 3);
                        if (widget != null) {
                            widget.interact();
                        }
//                        Keyboard.typeSpecialKey(32);
                    }
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
        }
    }

    public static void smith() {
        if (!Players.getLocal().isAnimating()) {
            if (!Inventory.contains("Bronze bar")) {
                Banking.putAllAndGetAllExcept("Bronze bar", "Hammer");
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(ANVIL.getRandomTile());
                var anvil = getClosestAnvil();
                if (anvil != null && anvil.interact("Smith")) {
                    Main.state = States.SMITHING;
                    Sleep.sleep(Calculations.random(1500, 2000));
                    org.dreambot.api.methods.widget.helpers.Smithing.makeAll("Bronze axe");
                }
            }

            if (Main.state.equals(States.SMITHING) && Dialogues.inDialogue()) {
                var anvil = getClosestAnvil();
                if (anvil != null && anvil.interact("Smith")) {
                    Sleep.sleep(Calculations.random(1500, 2000));
                    org.dreambot.api.methods.widget.helpers.Smithing.makeAll("Bronze axe");
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
                        && object.distance() <= 10
                        && object.canReach()
        );
    }
}
