package com.kbeliasas.everything.skills.smithing;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.States;
import com.kbeliasas.everything.Util;
import com.kbeliasas.everything.skills.Banking;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
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
import java.util.Locale;

@RequiredArgsConstructor
public class Smeting implements SmithingGeneric {
    private final Main main;
    private final List<OreInfo> oreInfos;
    private final Integer widgetId;
    private final Area furnacePlace = new Area(3105, 3501, 3109, 3496);;
    private State state;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case SMELTING:
                var smithingLevel = Skills.getRealLevel(Skill.SMITHING);
                if (furnace().interact("Smelt")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, Calculations.random(5000, 6000), Calculations.random(300, 500));
                    Widgets.get(270, widgetId).interact();
//                    Keyboard.typeKey(Key.SPACE);
                    Sleep.sleepUntil(() -> !containsOres() || Skills.getRealLevel(Skill.SMITHING) > smithingLevel,
                            Calculations.random(80000, 90000));
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var bar = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains("bar"));
                    if (bar != null) {
                        var amount = Inventory.count(bar.getID());
                        main.addLoot(bar.getID(), bar.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 800));
                    var oreId = oreInfos.stream().findFirst().map(OreInfo::getOreID);
                    var oresPerBar = oreInfos.stream().findFirst().map(OreInfo::getOresPerBar);
                    oreInfos.forEach(oreInfo -> {
                        if (Bank.count(oreInfo.getOreID()) < oreInfo.getOresPerBar()) {
                            Logger.log("Goal reached");
                            main.showResults();
                            ScriptManager.getScriptManager().stop();
                        }
                        Bank.withdraw(oreInfo.getOreID(), oreInfo.getCount());
                        Sleep.sleep(500, 800);
                    });
//                    Bank.withdraw(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE), 14);
                    Sleep.sleep(Calculations.random(500, 1000));
                    main.setGoal(Bank.count(ore -> ore.getID() == oreId.orElseThrow()) / oresPerBar.orElseThrow());
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
    }

    private enum State {
        SMELTING, BANKING, TRAVELING, FAILURE
    }

    void setState() {
        if (furnace() != null && containsOres()) {
            state = State.SMELTING;
            return;
        }

        if (furnace() == null && containsOres()) {
            state = State.TRAVELING;
            return;
        }

        if (!containsOres()) {
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

    private boolean containsOres() {
        return oreInfos.stream()
                .allMatch(oreInfo -> Inventory.count(oreInfo.getOreID()) >= oreInfo.getOresPerBar());
    }
}
