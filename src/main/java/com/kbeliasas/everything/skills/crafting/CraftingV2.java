package com.kbeliasas.everything.skills.crafting;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.Util;
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
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.List;

@RequiredArgsConstructor
public class CraftingV2 implements GenericSkill {
    private final Main main;
    private final List<CraftingConfig.Resource> resources;
    private final int[] widgetIds;
    private final int mould;
    private final int productID;
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
                    var widget = Widgets.get(widgetIds);
                    if (widget != null) {
                        widget.interact();
                        Sleep.sleepUntil(() -> resources.stream().map(CraftingConfig.Resource::getId).noneMatch(Inventory::contains) || Skills.getRealLevel(Skill.CRAFTING) > craftingLevel,
                                Calculations.random(70000, 80000));
                    }
                    break;
                case BANKING:
                    if (Banking.openBank()) {
                        var products = Inventory.get(productID);
                        if (products != null) {
                            var amount = Inventory.count(products.getID());
                            Util.addLoot(products.getName(), amount);
                        }
                        Bank.depositAllExcept(mould);
                        Sleep.sleep(Calculations.random(500, 800));
                        if (resources.stream().map(CraftingConfig.Resource::getId).noneMatch(Bank::contains)) {
                            Logger.log("Goal reached");
                            main.showResults();
                            Main.printResults();
                            ScriptManager.getScriptManager().stop();
                        }
                        Main.goal = Bank.count(resources.stream().findFirst().get().getId());
                        resources.forEach(resource -> {
                            Bank.withdraw(resource.getId(), resource.getAmount());
                            Sleep.sleep(Calculations.random(500, 800));
                        });
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
            if (Inventory.contains(mould)) {
                ready = true;
            } else {
                if (Banking.openBank()) {
                    Bank.withdraw(mould);
                }
            }
        }
    }

    private enum State {
        CRAFTING, BANKING, TRAVELING, FAILURE
    }

    private void setState() {
        if (furnace() != null && resources.stream().map(CraftingConfig.Resource::getId).allMatch(Inventory::contains)) {
            state = State.CRAFTING;
            return;
        }

        if (furnace() == null && resources.stream().map(CraftingConfig.Resource::getId).allMatch(Inventory::contains)) {
            state = State.TRAVELING;
            return;
        }

        if (resources.stream().map(CraftingConfig.Resource::getId).noneMatch(Inventory::contains)) {
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
