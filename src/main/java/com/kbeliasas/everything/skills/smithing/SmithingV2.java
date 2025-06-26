package com.kbeliasas.everything.skills.smithing;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

import static org.dreambot.api.methods.widget.helpers.Smithing.isOpen;
import static org.dreambot.api.methods.widget.helpers.Smithing.makeAll;

@RequiredArgsConstructor
public class SmithingV2 implements GenericSkill {

    private static final String PRODUCT = "platebody";
    private static final int HAMMER_ID = 2347;
    private static final Area ANVIL_PLACE = new Area(3185, 3427, 3190, 3420);
    private final Main main;
    private final int barId;

    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Inventory.contains(HAMMER_ID)) {
                    ready = true;
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Bank.withdraw(HAMMER_ID);
                    }
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var product = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains(PRODUCT));
                    if (product != null) {
                        var amount = Inventory.count(product.getID());
                        main.addLoot(product.getID(), product.getName(), amount);
                    }
                    Bank.depositAllExcept(hammer -> hammer.getID() == HAMMER_ID);
                    Sleep.sleep(Calculations.random(500, 800));
                    if (Bank.count(barId) < 5) {
                        Logger.log("Goal reached");
                        main.showResults();
                        main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    main.setGoal(Bank.count(bar -> bar.getID() == barId) / 5);
                    Bank.withdraw(bar -> bar.getID() == barId, 25);

                }
                break;
            case SMITHING:
                var smithingLevel = Skills.getRealLevel(Skill.SMITHING);
                anvil().interact("Smith");
                Sleep.sleep(Calculations.random(4000, 5000));
                if (isOpen()) {
                    makeAll(item -> item.getName().toLowerCase(Locale.ROOT).contains(PRODUCT));
                    Sleep.sleepUntil(() -> Inventory.count(barId) < 5 || Skills.getRealLevel(Skill.SMITHING) > smithingLevel,
                            Calculations.random(30000, 40000));
                }
                break;
            case TRAVELING:
                Walking.walk(ANVIL_PLACE.getRandomTile());
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.showResults();
                main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private void setState() {
        if (!ready) {
            state = State.PREP;
            return;
        }

        if (anvil() != null && Inventory.count(barId) >= 5) {
            state = State.SMITHING;
            return;
        }

        if (anvil() == null && Inventory.count(barId) >= 5) {
            state = State.TRAVELING;
            return;
        }

        if (Inventory.count(barId) < 5) {
            state = State.BANKING;
            return;
        }

        state = State.FAILURE;
    }

    private enum State {
        PREP, BANKING, SMITHING, TRAVELING, FAILURE
    }

    private GameObject anvil() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase("Anvil")
                        && object.hasAction("Smith")
                        && object.distance() <= 5
                        && object.canReach()
        );
    }
}
