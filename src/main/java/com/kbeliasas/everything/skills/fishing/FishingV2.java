package com.kbeliasas.everything.skills.fishing;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Locale;

@RequiredArgsConstructor
public class FishingV2 implements GenericSkill {

    private final Main main;
    private final Util util;
    private final Area area;
    private final String action;
    private final int fishId;
    private final int equipmentID;
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Inventory.contains(equipmentID) && Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"))) {
                    ready = true;
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        if (!Inventory.contains(equipmentID)) {
                            Bank.withdraw(equipmentID);
                        }
                        if (!Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"))) {
                            Bank.withdraw(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"), 10000);
                        }
                    }
                }
                break;
            case FISHING:
                var spot = spot();
                var fishingLevel = Skills.getRealLevel(Skill.FISHING);
                if (spot != null) {
                    spot.interact(action);
                    Sleep.sleep(Calculations.random(1000, 3000));
                    Sleep.sleepUntil(() -> sameSpot(spot) == null ||
                                    Skills.getRealLevel(Skill.FISHING) > fishingLevel ||
                                    Inventory.isFull() ||
                            !Players.getLocal().isAnimating(),
                            Calculations.random(100000, 120000),
                            Calculations.random(3000, 4000));
                }
                break;
            case TRAVELING:
                Walking.walk(area.getRandomTile());
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var fishes = Inventory.all(item -> item.getID() != equipmentID && !item.getName().toLowerCase(Locale.ROOT).contains("coins"));
                    fishes.forEach(fish -> {
                        var amount = Inventory.count(fish.getID());
                        main.addLoot(fish.getID(), fish.getName(), amount);
                    });
                    Bank.depositAllExcept(item -> item.getID() == equipmentID
                            || item.getName().toLowerCase(Locale.ROOT).contains("coins"));
                    Sleep.sleep(Calculations.random(500, 1000));
                    main.setBankedAmount(Bank.count(item -> item.getID() == fishId));
                    return;
                }
                break;
            case FINISHING:
                if (Banking.openBank()) {
                    var fishes = Inventory.all(item -> item.getID() != equipmentID && !item.getName().toLowerCase(Locale.ROOT).contains("coins"));
                    fishes.forEach(fish -> {
                        var amount = Inventory.count(fish.getID());
                        main.addLoot(fish.getID(), fish.getName(), amount);
                    });
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    main.showResults();
                    main.printResults();
                    ScriptManager.getScriptManager().stop();
                    return;
                }
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.showResults();
                main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private enum State {
        PREP, FISHING, TRAVELING, BANKING, FINISHING, FAILURE
    }

    private void setState() {
        if (Skills.getExperience(Skill.FISHING) >= main.getGoalXp()) {
            state = State.FINISHING;
            return;
        }

        if (!ready) {
            state = State.PREP;
            return;
        }

        if (Inventory.isFull()) {
            state = State.BANKING;
            return;
        }

        if (area.contains(Players.getLocal())) {
            state = State.FISHING;
            return;
        }

        if (!area.contains(Players.getLocal())) {
            state = State.TRAVELING;
            return;
        }

        state = State.FAILURE;
    }

    private NPC spot() {
        return NPCs.closest(spot ->
                spot.getName().toLowerCase(Locale.ROOT).contains("fishing spot")
                        && spot.distance() <= 15
                        && spot.hasAction(action)
        );
    }

    private NPC sameSpot(NPC oldSpot) {
        return NPCs.closest(spot ->
                spot.getCenterPoint().equals(oldSpot.getCenterPoint()) &&
                        spot.hasAction(action));
    }
}
