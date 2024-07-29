package everything.skills.smithing;

import everything.Main;
import everything.States;
import everything.Util;
import everything.skills.Banking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dreambot.api.input.event.impl.keyboard.awt.Key;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.bank.Bank;
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

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class Smeting implements SmithingGeneric {
    private final Main main;
    private final Util util;
    private final List<OreInfo> oreInfos;
    private final Area furnacePlace;
    private State state;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case SMELTING:
                var smithingLevel = Skills.getRealLevel(Skill.SMITHING);
                if (furnace().interact("Smelt")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, Calculations.random(5000, 6000));
                    Keyboard.typeKey(Key.SPACE);
                    var oreId = oreInfos.stream().findFirst().map(OreInfo::getOreID);
                    Sleep.sleepUntil(() -> !Inventory.contains(oreId.orElseThrow()) || Skills.getRealLevel(Skill.SMITHING) > smithingLevel,
                            Calculations.random(80000, 90000));
                }
                break;
            case BANKING:
                if (Banking.openBank()) {
                    var bar = Inventory.get(item -> item.getName().toLowerCase(Locale.ROOT).contains("bar"));
                    if (bar != null) {
                        var amount = Inventory.count(bar.getID());
                        Util.addLoot(bar.getName(), amount);
                    }
                    Bank.depositAllItems();
                    Sleep.sleep(Calculations.random(500, 800));
                    var oreId = oreInfos.stream().findFirst().map(OreInfo::getOreID);
                    var oresPerBar = oreInfos.stream().findFirst().map(OreInfo::getCount);
                    if (!Bank.contains(ore -> ore.getID() == oreId.orElseThrow())) {
                        Logger.log("Goal reached");
                        ScriptManager.getScriptManager().stop();
                    }
                    if (oreInfos.size() > 1) {
                        var ore1 = oreInfos.get(0).getOreID();
                        var count1 = oreInfos.get(0).getCount();
                        var ore2 = oreInfos.get(1).getOreID();
                        var count2 = oreInfos.get(1).getCount();
                        if (count1 == 1 && count2 == 2) {
                            Bank.withdraw(ore1, 9);
                            Sleep.sleep(Calculations.random(500, 800));
                            Bank.withdraw(ore2, 18);
                        }
                        if (count1 == 2 && count2 == 1) {
                            Bank.withdraw(ore1, 18);
                            Sleep.sleep(Calculations.random(500, 800));
                            Bank.withdraw(ore2, 9);
                        }
                    } else {
                        Bank.withdrawAll(oreInfos.get(0).getOreID());
                    }
//                    Bank.withdraw(ore -> ore.getName().toLowerCase(Locale.ROOT).contains(ORE), 14);
                    Sleep.sleep(Calculations.random(500, 1000));
                    Main.goal = Bank.count(ore -> ore.getID() == oreId.orElseThrow()) / oresPerBar.orElseThrow();
                    Main.state = States.IDLE;
                }
                break;
            case TRAVELING:
                Walking.walk(furnacePlace.getRandomTile());
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private enum State {
        SMELTING, BANKING, TRAVELING, FAILURE
    }

    void setState() {
        var oreId = oreInfos.stream().findFirst().map(OreInfo::getOreID).orElseThrow();
        if (furnace() != null && Inventory.contains(oreId)) {
            state = State.SMELTING;
            return;
        }

        if (furnace() == null && Inventory.contains(oreId)) {
            state = State.TRAVELING;
            return;
        }

        if (!Inventory.contains(oreId)) {
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
