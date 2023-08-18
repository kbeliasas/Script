package everything.skills;

import everything.Main;
import everything.States;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;

import java.util.Locale;

public class Prayering {

    public static void buryBones() {
        if (Inventory.contains(bones -> bones.getName().toLowerCase(Locale.ROOT).contains("bones"))) {
            var bone = Inventory.get(bones -> bones.getName().toLowerCase(Locale.ROOT).contains("bones"));
            bone.interact();
        } else {
            Logger.log("No more bones");
            Main.state = States.IDLE;
        }
    }
}
