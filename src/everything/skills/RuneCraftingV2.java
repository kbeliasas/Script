package everything.skills;

import everything.Constatnts;
import everything.Main;
import everything.Util;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

import java.util.Locale;

public class RuneCraftingV2 implements SkillEverything {

    private State state;

    static Area BODY_RUINS = new Area(3050, 3446, 3054, 3439);
    static Area BODY_ALTAR = new Area(0, 0, 0, 0);
    private static final int RUNES_PER_ESSENCE = 1;

    private static boolean pureEssence(Item item) {
        return item.getID() == Constatnts.PURE_ESSENCE;
    }

    @Override
    public void execute() {
        setState();
        switch (state) {
            case BANKING:
                if (Banking.openBank()) {
                    var rune = Inventory.get(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains("rune"));
                    if (rune != null) {
                        var amount = Inventory.count(rune.getID());
                        Util.addLoot(rune.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    if (Bank.contains(RuneCraftingV2::pureEssence)) {
                        Bank.withdrawAll(item -> pureEssence(item));
                    } else {
                        Main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.goal = Bank.count(item -> pureEssence(item)) * RUNES_PER_ESSENCE;
                }
                break;
            case TRAVELING:
                Walking.walk(BODY_RUINS.getRandomTile());
                break;
            case NEAR_RUINS:
                ruins().interact("Enter");
                Sleep.sleepUntil(() -> ruins() == null, Calculations.random(5000, 6000));
                break;
            case ALTAR:
                altar().interact("Craft-rune");
                Sleep.sleepUntil(() -> !haveEssence(), Calculations.random(5000, 6000));
                break;
            case EXIT_ALTAR:
                portal().interact("Use");
                Sleep.sleepUntil(() -> ruins() != null, Calculations.random(5000, 6000));
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                Main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private void setState() {
        if (ruins() != null && haveEssence()) {
            state = State.NEAR_RUINS;
            return;
        }

        if (altar() != null && haveEssence()) {
            state = State.ALTAR;
            return;
        }

        if (altar() != null && !haveEssence()) {
            state = State.EXIT_ALTAR;
            return;
        }

        if (!haveEssence()) {
            state = State.BANKING;
            return;
        }

        if (haveEssence()) {
            state = State.TRAVELING;
            return;
        }

        state = State.FAILURE;
    }

    private enum State {
        BANKING, TRAVELING, NEAR_RUINS, ALTAR, EXIT_ALTAR, FAILURE
    }

    private boolean haveEssence() {
        return Inventory.contains(item -> pureEssence(item));
    }

    private GameObject ruins() {
        return GameObjects.closest(ruins -> ruins.getName().toLowerCase(Locale.ROOT).contains("mysterious ruins")
                && ruins.distance() < 8 && ruins.canReach());
    }

    private GameObject altar() {
        return GameObjects.closest(altar -> altar.getName().toLowerCase(Locale.ROOT).contains("altar")
                && ruins().canReach());
    }

    private GameObject portal() {
        return GameObjects.closest(portal -> portal.getName().toLowerCase(Locale.ROOT).contains("portal")
                && portal().canReach());
    }
}
