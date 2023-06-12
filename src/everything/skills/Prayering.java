package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;

public class Prayering {

    public static void buryBones() {
        if (Inventory.contains("Bones")) {
            var bone = Inventory.getRandom("Bones");
            bone.interact();
        } else {
            Logger.log("No more bones");
            Main.state = States.IDLE;
        }
    }
}
