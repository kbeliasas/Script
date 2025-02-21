package com.kbeliasas.everything.skills.mining;

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
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

@RequiredArgsConstructor
public class MiningV2 implements GenericSkill {
    private final Main main;
    private final String rocksName;
    private final int oreID;
    private final Area mine;
    private final int pickaxe;
    private final int distance;
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Equipment.onlyContains(pickaxe)) {
                    ready = true;
                    return;
                } else if (Inventory.contains(pickaxe)) {
                    Equipment.equip(EquipmentSlot.WEAPON, pickaxe);
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Bank.withdraw(pickaxe);
                    }
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var ore = Inventory.get(item -> item.getID() == oreID);
                    if (ore != null) {
                        var amount = Inventory.count(oreID);
                        Util.addLoot(ore.getName(), amount);
                    }
                    var gems = Inventory.get(gem ->
                            gem.getName().toLowerCase(Locale.ROOT).contains("uncut")
                    );
                    if (gems != null) {
                        var amount = Inventory.count(gems.getID());
                        Util.addLoot(gems.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    var bankedAmount = Bank.count(item -> item.getID() == oreID);
                    Main.bankedAmount = bankedAmount;
                    if (bankedAmount >= Main.goal) {
                        Logger.info("Goal reached");
                        Bank.depositAllEquipment();
                        Main.printResults();
                        main.showResults();
                        Bank.close();
                        ScriptManager.getScriptManager().stop();
                    }
                }
                break;
            case TRAVELING:
                Walking.walk(mine.getRandomTile());
                break;
            case MINING:
                var rocks = rocks();
                if (rocks.interact("Mine")) {
                    Sleep.sleepUntil(() -> sameRock(rocks) == null,
                            Calculations.random(8000, 12000),
                            Calculations.random(300, 600));
                }
                break;
            case WAITING:
                if (!Walking.isRunEnabled()) {
                    if (Walking.getRunEnergy() >= 35) {
                        Walking.toggleRun();
                    }
                }
                if (Calculations.random(0, 50) == 5) {
                    Walking.walk(mine.getRandomTile());
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
        PREP, BANKING, TRAVELING, MINING, WAITING, FAILURE
    }

    private void setState() {
        if (!ready) {
            state = State.PREP;
            return;
        }

        if (Inventory.isFull()) {
            state = State.BANKING;
            return;
        }

        if (rocks() != null) {
            state = State.MINING;
            return;
        }

        if (mine.contains(Players.getLocal())) {
            state = State.WAITING;
            return;
        }

        if (!mine.contains(Players.getLocal())) {
            state = State.TRAVELING;
            return;
        }

        state = State.FAILURE;
    }

    private GameObject rocks() {
        return GameObjects.closest(object ->
                object.getName().equalsIgnoreCase(rocksName)
                        && object.hasAction("Mine")
                        && object.distance() <= distance
                        && object.getModelColors() != null
        );
    }

    private GameObject sameRock(GameObject oldRock) {
        return GameObjects.closest(rock ->
                rock.getCenterPoint().equals(oldRock.getCenterPoint()) &&
                        rock.getModelColors() != null);
    }

}
