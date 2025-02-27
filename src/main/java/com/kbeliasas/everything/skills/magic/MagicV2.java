package com.kbeliasas.everything.skills.magic;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
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

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class MagicV2 implements MagicGeneric {
    private final Main main;
    private final Area area;
    private final Map<EquipmentSlot, Integer> EQUIPMENT_MAP;
    private State state;
    private static final int MIND_RUNE = 558;
    private static final int AIR_RUNE = 556;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                var equipmentIds = EQUIPMENT_MAP.values().stream().mapToInt(Integer::intValue).toArray();
                if (Equipment.onlyContains(equipmentIds)) {
                    ready = true;
                } else if (Inventory.containsAll(equipmentIds)) {
                    EQUIPMENT_MAP.keySet().forEach(equipmentSlot -> {
                        Equipment.equip(equipmentSlot, EQUIPMENT_MAP.get(equipmentSlot));
                        Sleep.sleep(Calculations.random(800, 1500));
                    });
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllExcept(MIND_RUNE, AIR_RUNE);
                        Bank.depositAllEquipment();
                        Arrays.stream(equipmentIds).forEach(item -> {
                            Bank.withdraw(item);
                            Sleep.sleep(Calculations.random(800, 1500));
                        });
                        Bank.close();
                        Sleep.sleep(Calculations.random(800, 1500));
                    }
                }
                break;
            case MAGIC:
                if (!Players.getLocal().isInCombat() || Dialogues.inDialogue()) {
                    var mob = mob();
                    if (mob != null) {
                        mob.interact("Attack");
                    }
                }
                break;
            case TRAVELING:
                Walking.walk(area.getRandomTile());
                break;
            case FINISH:
                if (Banking.openBank()) {
                    Logger.log("Target level reached!");
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(800, 1200));
                    Bank.close();
                    Sleep.sleep(Calculations.random(800, 1200));
                    main.printResults();
                    main.showResults();
                    ScriptManager.getScriptManager().stop();
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
        PREP, MAGIC, TRAVELING, FINISH, FAILURE
    }

    private void setState() {
        if (!ready) {
            state = State.PREP;
            return;
        }


        if (!Inventory.contains(MIND_RUNE) || Skills.getExperience(Skill.MAGIC) > main.getGoalXp()) {
            Logger.info("Inventory contains: " + !Inventory.contains(MIND_RUNE));
            Logger.info("Skill experience: " + Skills.getExperience(Skill.MAGIC));
            state = State.FINISH;
            return;
        }

        if (area.contains(Players.getLocal())) {
            state = State.MAGIC;
            return;
        }

        if (!area.contains(Players.getLocal())) {
            state = State.TRAVELING;
            return;
        }

        state = State.FAILURE;
    }

    private NPC mob() {
        return NPCs.closest(npc -> npc.getName().toLowerCase(Locale.ROOT).contains("demon")
                && npc.distance() < 10);
    }
}
