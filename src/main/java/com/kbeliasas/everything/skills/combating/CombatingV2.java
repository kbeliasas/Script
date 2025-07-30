package com.kbeliasas.everything.skills.combating;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.*;

import static com.kbeliasas.everything.skills.combating.CombatingConfig.NATURE_RUNE;

@RequiredArgsConstructor
public class CombatingV2 implements GenericSkill {

    private final Main main;
    private final Skill skill;
    private final Integer FOOD_ID;
    private final int FOOD_MIN;
    private final int FOOD_AMOUNT;
    private final int HP_MIN;
    private final Area AREA;
    private final String MOB_NAME;
    private final int PRICE_MIN;
    private final int BONES_ID;
    private final Map<EquipmentSlot, Integer> EQUIPMENT_MAP;
    private State state;
    private ArrayList<GroundItem> loot = new ArrayList<>();
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case BANKING:
            case NO_FOOD:
                if (Banking.openBank()) {
                    var lootedItems = Inventory.all(item -> !FOOD_ID.equals(item.getID()));
                    lootedItems.forEach(item -> main.addLoot(item.getID(), item.getName(), item.getAmount()));
                    Bank.depositAllExcept(FOOD_ID);
                    var needMore = FOOD_AMOUNT - Inventory.count(FOOD_ID);
                    Bank.withdraw(FOOD_ID, needMore);
                    if (Bank.count(FOOD_ID) <= FOOD_AMOUNT) {
                        Logger.log("NO MORE FOOD");
                        main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                }
                var bone = Inventory.get(BONES_ID);
                if (bone != null) {
                    bone.interact();
                }
                break;
            case TRAVELING:
                Walking.walk(AREA.getRandomTile());
                break;
            case FINISHED:
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Bank.depositAllEquipment();
                    Logger.log("Target xp reached!");
                    main.showResults();
                    main.printResults();
                    ScriptManager.getScriptManager().stop();
                }
                break;
            case IN_COMBAT:
                if (Skills.getBoostedLevel(Skill.HITPOINTS) <= HP_MIN) {
                    Inventory.interact(FOOD_ID);
                }
                break;
            case LOW_HP:
                Inventory.interact(FOOD_ID);
                break;
            case LOOTING:
                // Optional but recommended: Sort loot by distance to be more efficient.
                loot.sort(Comparator.comparingDouble(l -> Players.getLocal().distance(l)));

                loot.forEach(item -> {
                    // Make sure the item still exists before trying to take it.
                    if (item != null && item.exists()) {
                        // Get the inventory count of the item *before* picking it up.
                        int initialCount = Inventory.count(item.getId());

                        // Interact with the item.
                        if (item.interact("Take")) {
                            // Wait until the item is no longer on the ground OR our inventory count increases.
                            // The 10,000 ms timeout prevents the script from getting stuck.
                            Sleep.sleepUntil(() -> !item.exists() || Inventory.count(item.getId()) > initialCount, 10000);
                        }
                    }
                });
                break;
            case PRAYER:
                bone = Inventory.get(BONES_ID);
                if (bone != null) {
                    bone.interact();
                    main.addLoot(bone.getID(), bone.getName());
                }
                break;
            case LOOKING_FOR_BATTLE:
                var mob = getMob();
                if (mob != null) {
                    mob.interact("Attack");
                }
                break;
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
                        Bank.depositAllItems();
                        Bank.depositAllEquipment();
                        Arrays.stream(equipmentIds).forEach(item -> {
                            Bank.withdraw(item);
                            Sleep.sleep(Calculations.random(800, 1500));
                        });
                        Bank.close();
                        Sleep.sleep(Calculations.random(800, 1500));
                    }
                }
                if (skill.equals(Skill.ATTACK)) {
                    Combat.setCombatStyle(CombatStyle.ATTACK);
                }
                if (skill.equals(Skill.STRENGTH)) {
                    Combat.setCombatStyle(CombatStyle.STRENGTH);
                }
                if (skill.equals(Skill.DEFENCE)) {
                    Combat.setCombatStyle(CombatStyle.DEFENCE);
                }
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.showResults();
                main.printResults();
                ScriptManager.getScriptManager().stop();
                break;
        }
    }

    private enum State {
        NO_FOOD, BANKING, TRAVELING, FINISHED, IN_COMBAT, LOW_HP, LOOTING, PRAYER, LOOKING_FOR_BATTLE, PREP, FAILURE
    }

    private void setState() {
        if (Skills.getExperience(skill) >= main.getGoalXp()) {
            state = State.FINISHED;
            return;
        }
        if (!ready) {
            state = State.PREP;
            return;
        }
        if (Inventory.count(FOOD_ID) <= FOOD_MIN) {
            state = State.NO_FOOD;
            return;
        }
        if (Skills.getBoostedLevel(Skill.HITPOINTS) <= HP_MIN) {
            state = State.LOW_HP;
            return;
        }
        if (Inventory.count(BONES_ID) >= 1) {
            state = State.PRAYER;
            return;
        }
        if (Inventory.emptySlotCount() < 3) {
            state = State.BANKING;
            return;
        }
        if (!getLoot().isEmpty()) {
            state = State.LOOTING;
            return;
        }
        if (Players.getLocal().isInCombat()) {
            state = State.IN_COMBAT;
            return;
        }
        if (getMob() != null) {
            state = State.LOOKING_FOR_BATTLE;
            return;
        }
        state = State.TRAVELING;

    }

    private ArrayList<GroundItem> getLoot() {
        var itemsFiltered = new ArrayList<GroundItem>();
        var items = getGroundItems();
        items.forEach(item -> {
            var price = LivePrices.get(item.getID());
            if (item.getName().equalsIgnoreCase("Coins")
                    || item.getID() == BONES_ID
                    || item.getName().toLowerCase(Locale.ROOT).contains("rune")
                    || item.getName().equalsIgnoreCase("Cowhide")
                    || item.getName().toLowerCase(Locale.ROOT).contains("clue")
                    || item.getName().toLowerCase(Locale.ROOT).contains("key")) {
                itemsFiltered.add(item);
                Logger.info("Looted: " + item.getName() + ". Manually included from List");
            } else if (price > PRICE_MIN) {
                itemsFiltered.add(item);
                Logger.info("Looted: " + item.getName() + " for :" + price);
            } else if (item.getItem().getHighAlchValue() - LivePrices.get(NATURE_RUNE) > price) {
                itemsFiltered.add(item);
                Logger.info("Looted: " + item.getName() + " for HA profit");
            } else {
                Logger.info("ignored: " + item.getName() + " for :" + price);
                Util.addIgnored(item.getName());
            }
        });
        loot = itemsFiltered;
        return itemsFiltered;
    }

    private List<GroundItem> getGroundItems() {
        return GroundItems.all(item -> item.distance() < 5 && item.canReach());
    }

    private NPC getMob() {
        return NPCs.closest(npc -> npc.getName().equalsIgnoreCase(MOB_NAME)
                && npc.canReach()
                && !npc.isInCombat()
                && npc.distance() < 10);
    }
}
