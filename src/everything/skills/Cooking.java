package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;

public class Cooking {

    private static final Area FALADOR_COOKING = new Area(3037, 3364, 3040, 3362);

    public static void cook() {
        if (Main.state.equals(States.BANKING)) {
            return;
        }
        if (Main.state.equals(States.WALKING) || Main.state.equals(States.COOKING)) {
            var cookingRange = GameObjects.closest("Range");
            if (cookingRange != null && cookingRange.canReach()) {
                cookingRange.interact("Cook");
                Sleep.sleep(Calculations.random(1500, 1800));
                Keyboard.typeSpecialKey(32);
                Main.state = States.COOKING;
            }
        }
        if (Main.state.equals(States.IDLE) || Main.state.equals(States.WALKING)) {
            Main.state = States.WALKING;
            Walking.walk(FALADOR_COOKING.getRandomTile());
        }

    }
}
