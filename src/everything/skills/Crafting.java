package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Locale;

public class Crafting {

    static Area TANNER = new Area(3270, 3193, 3276, 3189);
    static String ARMOR_TO_MAKE = "chaps";

    public static void makeLeather() {
        if (!Players.getLocal().isAnimating()) {

            if (!Inventory.contains("Cowhide") && !Inventory.contains("Leather")) {
                Banking.putAllAndGetAllExcept("Cowhide", "Coins");
            }

            if (Inventory.contains("Leather")) {
                Banking.putAllAndGetAllExcept("Cowhide", "Coins");
            }

            if (!Inventory.contains("Coins")) {
                Logger.error("No More Money!");
                ScriptManager.getScriptManager().stop();
            }

            if (Main.state.equals(States.CRAFTING)) {
                var widget = Widgets.get(324, 92);
                if (widget != null) {
                    widget.interact("Tan All");
                    Logger.info(widget.getActions());
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(TANNER.getRandomTile());
                var tanner = getClosestTanner();
                if (tanner != null) {
                    Main.state = States.CRAFTING;
                    if (tanner.interact("Trade")) {
                        Sleep.sleep(Calculations.random(2000, 3000));
                        var widget = Widgets.get(324, 92);
                        if (widget != null) {
                            widget.interact("Tan All");
                            Logger.info(widget.getActions());
                        }
                    }
                }
            }
        }
    }

    public static void makeLeatherArmor() {
        if (!Players.getLocal().isAnimating()) {

            if (Skills.getRealLevel(Skill.CRAFTING) >= Main.goal) {
                Logger.info("Goal reached");
                ScriptManager.getScriptManager().stop();
            }

            if (!Inventory.contains("Leather")) {
                if (Banking.openBank()) {
                    Bank.depositAllExcept(filter ->
                            filter.getName().equalsIgnoreCase("Needle")
                                    || filter.getName().equalsIgnoreCase("Thread"));
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains("Leather")) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Bank.withdrawAll("Leather");
                    Main.state = States.IDLE;
                    Sleep.sleep(Calculations.random(300, 500));
                    Bank.close();
                }
            }

            if (Main.state.equals(States.CRAFTING) && Dialogues.inDialogue()) {
                var needle = Inventory.get("Needle");
                var leather = Inventory.get("Leather");

                needle.useOn(leather);
                Sleep.sleep(Calculations.random(1000, 2000));
                if (ItemProcessing.isOpen()) {
                    Logger.info("Works");
                    ItemProcessing.makeAll(filter -> filter.getName().contains(ARMOR_TO_MAKE));
                }
            }

            if (Main.state.equals(States.IDLE)) {
                var needle = Inventory.get("Needle");
                var leather = Inventory.get("Leather");

                needle.useOn(leather);
                Sleep.sleep(Calculations.random(1000, 2000));
                if (ItemProcessing.isOpen()) {
                    Logger.info("Works");
                    ItemProcessing.makeAll(filter ->
                            filter.getName().toLowerCase(Locale.ROOT).contains(ARMOR_TO_MAKE));
                    Main.state = States.CRAFTING;
                }
            }

        }
    }

    private static NPC getClosestTanner() {
        return NPCs.closest(npc -> npc.getName().equalsIgnoreCase("Ellis")
                && npc.canReach()
                && npc.distance() <= 10);
    }
}
