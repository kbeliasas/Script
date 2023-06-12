package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;

public class RuneEssence {

    static Area TELEPORT_LOCATION = new Area(3250, 3402, 3254, 3400);

    public static void mine() {

        if (Main.state.equals(States.MINING)) {
            var runeEssence = GameObjects.closest("Rune Essence");
            if (runeEssence != null && runeEssence.canReach()) {
                runeEssence.interact("Mine");
            } else {
                Logger.error("Something wrong runeEssence: " + runeEssence);
            }
        }

        if (Main.state.equals(States.IDLE)) {
            Walking.walk(TELEPORT_LOCATION.getRandomTile());
            var aubury = NPCs.closest("Aubury");
            if (aubury != null && aubury.canReach()) {
                aubury.interact("Teleport");
                Main.state = States.MINING;
            }
        }

    }
}
