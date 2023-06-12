package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;

public class Woodcutting {

    static Area OAK = new Area(3167, 3426, 3172, 3416);

    public static void cut() {
        if (!Players.getLocal().isAnimating()) {

            if (Skills.getRealLevel(Skill.WOODCUTTING) >= 30) {
                Logger.log("Target level reached!");
                ScriptManager.getScriptManager().stop();
            }

            if (Inventory.isFull()) {
                Banking.putAllItems();
            }

            if (Main.state.equals(States.IDLE)) {
                Walking.walk(OAK.getRandomTile());
                var tree = getCloset();
                if (tree != null) {
                    Main.state = States.WOODCUTTING;
                    tree.interact("Chop Down");
                }
            }

            if (Main.state.equals(States.WOODCUTTING)) {
                var tree = getCloset();
                if (tree != null) {
                    tree.interact("Chop Down");
                }
            }
        }
    }

    private static GameObject getCloset() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase("oak")
                        && object.canReach()
                        && object.hasAction("Chop Down")
                        && object.distance() <= 20);
    }
}
