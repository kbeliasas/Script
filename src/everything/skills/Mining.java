package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class Mining {

    static Area TIN_COPPER = new Area(3285, 3372, 3289, 3369);
    static Area SOUTH_WEST_VARROCK_MINE = new Area(3183, 3374, 3173, 3365);
    static Area AL_KHARID_MINE = new Area(3297, 3315, 3301, 3302);
    static String TIN_NAME = "Tin rocks";
    static String COPPER_NAME = "Copper rocks";
    static String SILVER_NAME = "Silver rocks";

    public static void mine() {

        if (!Players.getLocal().isAnimating()) {
            if (Inventory.isFull()) {
                Banking.putAllAndFinishWhen("Silver ore", Main.goal);
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(AL_KHARID_MINE.getRandomTile());
                var ore = getClosest();
                if (ore != null) {
                    Main.state = States.MINING;
                    if (ore.interact("Mine")) {
                        Sleep.sleep(Calculations.random(200, 300));
                    }
                }
            }

            if (Main.state.equals(States.MINING)) {
                var ore = getClosest();
                if (ore != null) {
                    if (ore.interact("Mine")) {
                        Sleep.sleep(Calculations.random(200, 300));
                    }
                }
            }
        }
    }

    private static GameObject getClosest() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase(SILVER_NAME)
                        && object.hasAction("Mine")
                        && object.distance() <= 20
                        && object.getModelColors() != null
        );
    }
}
