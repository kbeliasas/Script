package com.kbeliasas.everything.skills.cooking;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.input.event.impl.keyboard.awt.Key;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
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
public class CookingV2 implements GenericSkill {

    private final Main main;
    private final int rawFish;
    private final int fishId;
    private final Area cookingLocation;
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Equipment.count(item -> true) > 0) {
                    Equipment.unequip(item -> true);
                } else {
                    ready = true;
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var fishItem = Inventory.get(fishId);
                    if (fishItem != null) {
                        var amount = Inventory.count(fishId);
                        Util.addLoot(fishItem.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    if (Bank.contains(rawFish)) {
                        Bank.withdrawAll(rawFish);
                    } else {
                        Logger.info("No more fish");
                        main.showResults();
                        main.printResults();
                        Bank.close();
                        ScriptManager.getScriptManager().stop();
                    }
                    main.setGoal( Bank.count(rawFish));
                }
                break;
            case TRAVELING:
                Walking.walk(cookingLocation.getRandomTile());
                break;
            case COOKING:
                var cookingLevel = Skills.getRealLevel(Skill.COOKING);
                cookingRange().interact("Cook");
                Sleep.sleepUntil(() -> Players.getLocal().isStandingStill() && Dialogues.inDialogue(),
                        Calculations.random(30000, 40000),
                        Calculations.random(500, 1000));
                Keyboard.typeKey(Key.SPACE);
                Sleep.sleepUntil(() -> !Inventory.contains(rawFish) || Skills.getRealLevel(Skill.COOKING) > cookingLevel
                        , Calculations.random(70000, 80000));
                break;
            case DROPPING:
                Inventory.dropAll(item -> item.getName().toLowerCase(Locale.ROOT).contains("burnt"));
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
        PREP, BANKING, TRAVELING, COOKING, DROPPING, FAILURE
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
