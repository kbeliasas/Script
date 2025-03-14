package com.kbeliasas.everything.skills.crafting;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.States;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

@RequiredArgsConstructor
public class CraftingTiara implements CraftingGeneric {

    private final Main main;
    private final int barId;
    private final static int tiaraMould = 5523;
    private final static int tiaraID = 5525;
    private final static Area furnacePlace = new Area(3105, 3501, 3109, 3496);
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        if (ready) {
            setState();
            main.setStateString(state.name());
            switch (state) {
                case CRAFTING:
                    var craftingLevel = Skills.getRealLevel(Skill.CRAFTING);
                    furnace().interact("Smelt");
                    Sleep.sleepUntil(Widgets::isOpen, Calculations.random(5000,6000));
                    var widget = Widgets.get(6, 28, 3);
                    if (widget != null) {
                        widget.interact();
                        Sleep.sleepUntil(() -> !Inventory.contains(barId) || Skills.getRealLevel(Skill.CRAFTING) > craftingLevel,
                                Calculations.random(70000, 80000));
                    }
                    break;
                case BANKING:
                    if (Banking.openBank()) {
                        var tiaras = Inventory.get(tiara -> tiara.getID() == tiaraID);
                        if (tiaras != null) {
                            var amount = Inventory.count(tiaras.getID());
                            main.addLoot(tiaras.getID(), tiaras.getName(), amount);
                        }
                        Bank.depositAllExcept(tiaraMould);
                        Sleep.sleep(Calculations.random(500, 800));
                        if (!Bank.contains(barId)) {
                            Logger.log("Goal reached");
                            main.showResults();
                            main.printResults();
                            ScriptManager.getScriptManager().stop();
                        }
                        main.setGoal(Bank.count(bar -> bar.getID() == barId));
                        Bank.withdrawAll(barId);
                    }
                    break;
                case TRAVELING:
                    Walking.walk(furnacePlace.getRandomTile());
                    break;
                case FAILURE:
                    Logger.error("ERROR State failed to set state;");
                    main.showResults();
                    main.printResults();
                    ScriptManager.getScriptManager().stop();
            }
        } else {
            if (Inventory.contains(tiaraMould)) {
                ready = true;
            } else {
                if (Banking.openBank()) {
                    Bank.withdraw(tiaraMould);
                }
            }
        }
    }

    private enum State {
        CRAFTING, BANKING, TRAVELING, FAILURE
    }

    private void setState() {
        if (furnace() != null && Inventory.contains(barId)) {
            state = State.CRAFTING;
            return;
        }

        if (furnace() == null && Inventory.contains(barId)) {
            state = State.TRAVELING;
            return;
        }

        if (!Inventory.contains(barId)) {
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
