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
                Prayering.buryBones();
                if (Inventory.emptySlotCount() < 5) {
                    Main.state = States.BANKING;
                }
            }

            if (Main.state.equals(States.PRAYER) || Main.state.equals(States.BANKING)) {
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
                Logger.log("Finding cow");
                var cow = NPCs.closest("Cow");
                if (cow != null && cow.canReach() && !cow.isInCombat()) {
                    cow.interact("Attack");
                } else if (cow == null) {
                    Walking.walk(COWS.getRandomTile());
                } else {
                    Walking.walk(COWS.getRandomTile());
                    Logger.log("Something wrong: " + cow);
                }
            }
        }
    }
}
