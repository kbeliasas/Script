package com.kbeliasas.everything.skills.woodcutting;

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
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Locale;

@RequiredArgsConstructor
public class WoodcuttingV2 implements GenericSkill {
    private final Main main;
    private final Area AREA;
    private final Integer AXE_ID;
    private final Integer LOGS_ID;
    private final String TREE;
    private State state;
    private boolean ready = false;
    private final String action = "Chop Down";

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Equipment.onlyContains(AXE_ID)) {
                    ready = true;
                } else if (Inventory.contains(AXE_ID)) {
                    Equipment.equip(EquipmentSlot.WEAPON, AXE_ID);
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Bank.withdraw(AXE_ID);
                        Sleep.sleep(Calculations.random(500, 1000));
                        Bank.close();
                        Sleep.sleep(Calculations.random(500, 1000));
                    }
                }
                break;
            case WOODCUTTING:
                var tree = getTree();
                var woodcuttingLevel = Skills.getRealLevel(Skill.WOODCUTTING);
                if (tree != null) {
                    tree.interact(action);
                    Sleep.sleepUntil(() -> getSameTree(tree) == null ||
                                    Skills.getRealLevel(Skill.WOODCUTTING) > woodcuttingLevel ||
                                    Inventory.isFull(),
                            Calculations.random(50000, 60000),
                            Calculations.random(300, 600));
                }
                break;
            case TRAVELING:
                Walking.walk(AREA.getRandomTile());
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var logs = Inventory.get(LOGS_ID);
                    if (logs != null) {
                        var amount = Inventory.count(logs.getID());
                        Util.addLoot(logs.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    main.bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(TREE));
                }
                break;
            case FINISHED:
                if (Banking.openBank()) {
                    var logs = Inventory.get(LOGS_ID);
                    if (logs != null) {
                        var amount = Inventory.count(logs.getID());
                        Util.addLoot(logs.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 1000));
                    main.bankedAmount = Bank.count(item ->
                            item.getName().toLowerCase(Locale.ROOT).contains(TREE));
                    Logger.log("Target xp reached!");
                    main.printResults();
                    Bank.close();
                    ScriptManager.getScriptManager().stop();
                }
                break;
            case ERROR:
                Logger.error("ERROR State failed to set state;");
                main.printResults();
                ScriptManager.getScriptManager().stop();
                break;
        }
    }

    private GameObject getTree() {
        return GameObjects.closest(object ->
                object.getName().toLowerCase(Locale.ROOT).contains(TREE)
                        && object.canReach()
                        && object.hasAction(action)
                        && object.distance() <= 20);
    }

    private GameObject getSameTree(GameObject oldTree) {
        return GameObjects.closest(tree -> tree.getCenterPoint().equals(oldTree.getCenterPoint()) &&
                tree.hasAction(action));
    }

    private enum State {
        PREP, WOODCUTTING, TRAVELING, BANKING, FINISHED, ERROR
    }

    private void setState() {
        if (Skills.getExperience(Skill.WOODCUTTING) >= main.getGoalXp()) {
            state = State.FINISHED;
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

        if (AREA.contains(Players.getLocal())) {
            state = State.WOODCUTTING;
            return;
        }

        if (!AREA.contains(Players.getLocal())) {
            state = State.TRAVELING;
            return;
        }

        state = State.ERROR;
    }
}
