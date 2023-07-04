package everything.skills;

import everything.Main;
import everything.States;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Locale;

public class RuneEssence {

    static Area TELEPORT_LOCATION = new Area(3250, 3402, 3254, 3400);

    public static void mine() {

        if (!Players.getLocal().isAnimating()) {

            if (Inventory.isFull()) {
                Main.state = States.BANKING;
                var portal = getPortal();
                var portal2 = getPortalNpc();
                Logger.info(portal);
                Logger.info(portal2);
                if (portal != null) {
                    portal.interact();
                }
                if (portal2 != null) {
                    portal2.interact();
                }
                if (Banking.openBank()) {
                    var essence = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("essence"));
                    var amount = Inventory.count(essence.getID());
                    Util.addLoot(essence.getName(), amount);
                    Bank.depositAllItems();
                    Main.state = States.IDLE;
                    Sleep.sleep(Calculations.random(500,1000));
                    var bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("essence"));
                    if (bankedAmount >= Main.goal) {
                        Main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.bankedAmount = bankedAmount;
                }

            }

            if (Main.state.equals(States.MINING)) {
                var runeEssence = GameObjects.closest("Rune Essence");
                if (runeEssence != null) {
                    if (runeEssence.distance() < 15) {
                        runeEssence.interact("Mine");
                    } else {
                        Walking.walk(runeEssence.getTile().getRandomized());
                    }
                }
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(TELEPORT_LOCATION.getRandomTile());
                var aubury = getAubury();
                if (aubury != null) {
                    aubury.interact("Teleport");
                    Main.state = States.MINING;
                }
            }
        }

    }

    private static GameObject getPortal() {
        return GameObjects.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("portal")
                && object.canReach());
    }

    private static GameObject getRuneEssence() {
        return GameObjects.closest(object -> object.getName().equalsIgnoreCase("Rune Essence")
                && object.canReach());
    }

    private static NPC getPortalNpc() {
        return NPCs.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("portal"));
    }

    private static NPC getAubury() {
        return NPCs.closest(npc -> npc.getName().equalsIgnoreCase("Aubury")
                && npc.canReach()
                && npc.distance() < 5);
    }
}
