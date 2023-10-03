package everything.skills.smithing;

import everything.Main;
import everything.States;
import everything.Util;
import everything.skills.Banking;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.input.event.impl.keyboard.awt.Key;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

@RequiredArgsConstructor
public class Smeting implements SmithingGeneric {
    private final Main main;
    private final Util util;
    private final int oreId;
    private final Area furnacePlace;
    private final int oresPerBar;
    private State state;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case SMELTING:
                var smithingLevel = Skills.getRealLevel(Skill.SMITHING);
                if (furnace().interact("Smelt")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, Calculations.random(5000, 6000));
                    Keyboard.typeKey(Key.SPACE);
                    Sleep.sleepUntil(() -> !Inventory.contains(oreId) || Skills.getRealLevel(Skill.SMITHING) > smithingLevel,
                            Calculations.random(70000, 80000));
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var bar = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains("bar"));
                    if (bar != null) {
                        var amount = Inventory.count(bar.getID());
                        Util.addLoot(bar.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 800));
                    if (!Bank.contains(ore -> ore.getID() == oreId)) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    Bank.withdrawAll(oreId);
//                    Bank.withdraw(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE), 14);
                    Sleep.sleep(Calculations.random(500, 1000));
                    Main.goal = Bank.count(ore -> ore.getID() == oreId) / oresPerBar;
                    Main.state = States.IDLE;
                }
                break;
            case TRAVELING:
                Walking.walk(furnacePlace.getRandomTile());
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private enum State {
        SMELTING, BANKING, TRAVELING, FAILURE
    }

    void setState() {
        if (furnace() != null && Inventory.contains(oreId)) {
            state = State.SMELTING;
            return;
        }

        if (furnace() == null && Inventory.contains(oreId)) {
            state = State.TRAVELING;
            return;
        }

        if (!Inventory.contains(oreId)) {
            state = State.BANKING;
            return;
        }

        state = State.FAILURE;

    }

    private GameObject furnace() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase("Furnace")
                        && object.hasAction("Smelt")
                        && object.distance() <= 10
                        && object.canReach()
        );
    }
}
