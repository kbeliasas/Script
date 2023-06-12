package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;

public class Fishing {

    private static final Area FISHING_SPOT_LUMBRIDGE = new Area(3238, 3152, 3239, 3144);

    public static void fish() {
        if (Main.state.equals(States.BANKING)) {
            return;
        }
        var spot = NPCs.closest("everyting.Fishing spot");
        Logger.log("SPOT: " + spot);
        if (spot == null) {
            Main.state = States.WALKING;
            Walking.walk(FISHING_SPOT_LUMBRIDGE.getRandomTile());
            Logger.log("Walking");
        }
        if (spot != null && spot.canReach()) {
            Main.state = States.FISHING;
            spot.interact("Net");
            Logger.log("Cast Net");
        }
    }
}
