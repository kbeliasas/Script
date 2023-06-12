package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;

public class Combating {

    private static final Area CHICKENS = new Area(3231, 3296, 3235, 3290);
    private static final Area COWS = new Area(3254, 3293, 3264, 3256);

    public static void attack() {

        if (!Players.getLocal().isAnimating()) {

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

            if (Players.getLocal().isInCombat()) {
                Main.state = States.COMBATING;
                return;
            }

            var item = GroundItems.closest("Coins", "Bones", "Cowhide");
            if (item != null && item.distance() < 3) {
                item.interact("Take");
                Main.state = States.IDLE;
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

    private static GroundItem getLoot() {
        return GroundItems.closest(item -> item.getName().equalsIgnoreCase("Coins"));
    }
}
