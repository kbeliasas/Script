package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Sleep;

import java.util.Locale;

public class Firemaking {

    static String LOG_NAME = "willow";
    static String ASHES = "ashes";
    static Area BURNING_AREA = new Area(3167, 3443, 3178, 3428);

    public static void fire() {


        if (!Players.getLocal().isAnimating()) {

            setState();

            switch (Main.state) {
                case WALKING:
                    Walking.walk(BURNING_AREA.getRandomTile());
                    break;
                case BANKING: {
                    if (Banking.openBank()) {
                        Main.goal = Bank.count(item -> item.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME));
                        if (Bank.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME))) {
                            Bank.withdrawAll(item -> item.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME));
                        } else {
                            Main.printResults();
                            Bank.close();
                            ScriptManager.getScriptManager().stop();
                        }
                        Sleep.sleep(Calculations.random(2000,3000));
                        var logs = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME));
                        if (logs != null) {
                            Util.addLoot(logs.getName(), Inventory.count(item -> item.getID() == logs.getID()));
                        }
                    }
                }
                break;
                case DROPPING:
                    Inventory.dropAll(item -> item.getName().toLowerCase(Locale.ROOT).contains(ASHES));
                    break;
                case FIREMAKING: {
                    var logs = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME));
                    var tinderbox = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains("tinderbox"));
                    if (logs != null) {
                        Inventory.interact(tinderbox, "Use");
                        Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(2000, 3000));
                        Inventory.interact(logs, "Use");
                    }

                    if (Dialogues.canContinue()) {
                        Dialogues.continueDialogue();
                    }
                }
                break;
                default: {
                    break;
                }
            }

        }
    }

    private static void setState() {

        if (!Inventory.contains(log -> log.getName().toLowerCase(Locale.ROOT).contains(LOG_NAME))) {
            if (Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains(ASHES))) {
                Main.state = States.DROPPING;
                return;
            }
            Main.state = States.BANKING;
            return;
        }

        if (!BURNING_AREA.contains(Players.getLocal())) {
            Main.state = States.WALKING;
            return;
        }

        var fire = GameObjects.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("fire")
                && object.getTile().equals(Players.getLocal().getTile()));
        var daisy = GameObjects.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("daisies")
                && object.getTile().equals(Players.getLocal().getTile()));
        if (fire != null || daisy != null) {
            Main.state = States.WALKING;
            return;
        }

        Main.state = States.FIREMAKING;
    }

}
