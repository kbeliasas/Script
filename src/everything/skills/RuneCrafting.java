package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class RuneCrafting {

    static Area AIR_ALTAR = new Area(2982, 3296, 2986, 3293);
    static Area MIND_ALTAR = new Area(2978, 3516, 2986, 3511);

    public static void craft() {
        if (Main.state.equals(States.IDLE)) {
            Walking.walk(MIND_ALTAR.getRandomTile());
            var ruins = GameObjects.closest("Mysterious ruins");
            if (ruins != null && ruins.canReach() && ruins.distance() < 10) {
                ruins.interact("Enter");
                Sleep.sleep(Calculations.random(1000, 2000));
                Main.state = States.RUNECRAFTING;
            } else {
                Logger.error("Something wrong ruins: " + ruins);
            }
        }

        if (Main.state.equals(States.BANKING)) {
            var portal = GameObjects.closest("Portal");

            if (portal != null && portal.canReach()) {
                Sleep.sleep(Calculations.random(600, 900));
                portal.interact("Use");
            }

        }

        if (Main.state.equals(States.RUNECRAFTING)) {
            var altar = GameObjects.closest("Altar");
            if (altar != null && altar.canReach()) {
                altar.interact("Craft-rune");
//                try {
//                    Thread.sleep(Calculations.random(1900, 2200));
//                } catch (InterruptedException e) {
//                    Logger.error("Interupted", e);
//                }
                Sleep.sleep(Calculations.random(5000, 6000));
                Main.state = States.BANKING;
            }
        }
    }
}
