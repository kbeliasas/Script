package com.kbeliasas.everything.skills.runecrafting;

import com.kbeliasas.everything.Constants;
import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

import java.util.Locale;

@RequiredArgsConstructor
public class RuneCraftingV2 implements GenericSkill {

    private final Main main;
    private final Area ruins;
    private final int tiara;
    private final int runesPerEssence;
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Equipment.onlyContains(tiara)) {
                    ready = true;
                    return;
                } else {
                    if (Equipment.count(item -> true) > 0) {
                        Equipment.unequip(item -> true);
                    }
                }
                if (Inventory.contains(tiara)) {
                    Equipment.equip(EquipmentSlot.HAT, tiara);
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Bank.withdraw(tiara);
                    }
                }
                break;
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
                    if (Bank.contains(this::pureEssence)) {
                        Bank.withdrawAll(this::pureEssence);
                    } else {
                        Main.printResults();
                        main.showResults();
                        ScriptManager.getScriptManager().stop();
                    }
                    Main.goal = Bank.count(this::pureEssence) * runesPerEssence;
                }
                break;
            case TRAVELING:
                Walking.walk(ruins.getRandomTile());
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
        PREP, BANKING, TRAVELING, NEAR_RUINS, ALTAR, EXIT_ALTAR, FAILURE
    }

    private boolean haveEssence() {
        return Inventory.contains(this::pureEssence);
    }

    private GameObject ruins() {
        return GameObjects.closest(ruinsObject -> ruinsObject.getName().toLowerCase(Locale.ROOT).contains("mysterious ruins")
                && ruinsObject.distance() < 8 && ruinsObject.canReach());
    }

    private GameObject altar() {
        return GameObjects.closest(altar -> altar.getName().toLowerCase(Locale.ROOT).contains("altar")
                && altar.canReach());
    }

    private GameObject portal() {
        return GameObjects.closest(portal -> portal.getName().toLowerCase(Locale.ROOT).contains("portal")
                && portal.canReach());
    }

    private boolean pureEssence(Item item) {
        return item.getID() == Constants.PURE_ESSENCE;
    }
}
