package everything.skills.cooking;

import everything.Main;
import everything.Util;
import everything.skills.Banking;
import everything.skills.GenericSkill;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

public class CookingV2 implements GenericSkill {

    private final Main main;
    private final int rawFish;
    private final int fish;
    private final Area cookingLocation;
    private State state;
    private boolean ready = false;

    public CookingV2(Main main, int rawFish, int fish, Area cookingLocation) {
        this.main = main;
        this.rawFish = rawFish;
        this.fish = fish;
        this.cookingLocation = cookingLocation;
    }

    @Override
    public void execute() {
        if (ready) {
            setState();
            main.setStateString(state.name());
            switch (state) {
                case BANKING:
                    if (Banking.openBank()) {
                        var fishItem = Inventory.get(item -> item.getID() == fish);
                        if (fishItem != null) {
                            var amount = Inventory.count(fish);
                            Util.addLoot(fishItem.getName(), amount);
                        }
                        Bank.depositAllItems();
                        Sleep.sleep(Calculations.random(500, 1000));
                        if (Bank.contains(item -> item.getID() == rawFish)) {
                            Bank.withdrawAll(item -> item.getID() == rawFish);
                        } else {
                            main.printResults();
                            ScriptManager.getScriptManager().stop();
                        }
                        Main.goal = Bank.count(item -> item.getID() == rawFish);
                    }
                    break;
                case TRAVELING:
                    Walking.walk(cookingLocation.getRandomTile());
                    break;
                case COOKING:
                    cookingRange().interact("Cook");
                    Sleep.sleepUntil(() -> !Players.getLocal().isStandingStill(), Calculations.random(5000, 6000));
                    Keyboard.typeSpecialKey(32);
                    break;
                case DROPPING:
                    Inventory.dropAll(item -> item.getName().toLowerCase(Locale.ROOT).contains("burnt"));
                    break;
                case FAILURE:
                    Logger.error("ERROR State failed to set state;");
                    main.printResults();
                    ScriptManager.getScriptManager().stop();
            }
        } else {
            if (Equipment.count(item -> true) > 0) {
                Equipment.unequip(item -> true);
            } else {
                ready = true;
            }
        }
    }

    private void setState() {
        if (cookingRange() != null && haveRawFish()) {
            state = State.COOKING;
            return;
        }

        if (!haveRawFish() && haveBurntFish()) {
            state = State.DROPPING;
            return;
        }

        if (haveRawFish()) {
            state = State.TRAVELING;
            return;
        }

        if (!haveRawFish()) {
            state = State.BANKING;
            return;
        }

        state = State.FAILURE;
    }

    private enum State {
        BANKING, TRAVELING, COOKING, DROPPING, FAILURE
    }

    private GameObject cookingRange() {
        return GameObjects.closest(range ->
                range.getName().toLowerCase(Locale.ROOT).contains("stove") &&
                        range.canReach() && range.distance() <= 10);
    }

    private boolean haveRawFish() {
        return Inventory.contains(rawFish);
    }

    private boolean haveBurntFish() {
        return Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains("burnt"));
    }
}
