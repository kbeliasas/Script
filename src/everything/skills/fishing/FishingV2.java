package everything.skills.fishing;

import everything.Main;
import everything.States;
import everything.Util;
import everything.skills.Banking;
import everything.skills.GenericSkill;
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
        if (Skills.getExperience(Skill.FISHING) >= main.getGoalXp()) {
            if (Banking.openBank()) {
                var fish = Inventory.get(item -> item.getID() == fishId);
                var amount = Inventory.count(fishId);
                Util.addLoot(fish.getName(), amount);
                Bank.depositAllItems();
                Sleep.sleep(Calculations.random(800, 1200));
                Bank.close();
                Sleep.sleep(Calculations.random(800, 1200));
                Main.printResults();
                ScriptManager.getScriptManager().stop();
                return;
            }
        }
        if (ready) {
            setState();
            main.setStateString(state.name());
            switch (state) {
                case FISHING:
                    var spot = spot();
                    var fishingLevel = Skills.getRealLevel(Skill.FISHING);
                    if (spot != null) {
                        spot.interact(action);
                        Sleep.sleepUntil(() -> sameSpot(spot) == null ||
                                        Skills.getRealLevel(Skill.FISHING) > fishingLevel ||
                                        Inventory.isFull(),
                                Calculations.random(50000, 60000),
                                Calculations.random(300, 600));
                    }
                    break;
                case TRAVELING:
                    Walking.walk(area.getRandomTile());
                    break;
                case BANKING:
                    if (Banking.openBank()) {
                        var fish = Inventory.get(item -> item.getID() == fishId);
                        var amount = Inventory.count(fish.getID());
                        Util.addLoot(fish.getName(), amount);
                        Bank.depositAllExcept(item -> item.getID() == equipmentID
                                || item.getName().toLowerCase(Locale.ROOT).contains("coins"));
                        Sleep.sleep(Calculations.random(500, 1000));
                        Main.bankedAmount = Bank.count(item -> item.getID() == fishId);
                        Main.state = States.IDLE;
                        return;
                    }
                    break;
                case FAILURE:
                    Logger.error("ERROR State failed to set state;");
                    main.printResults();
                    ScriptManager.getScriptManager().stop();
            }
        } else {
            if (Inventory.contains(equipmentID) && Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"))) {
                ready = true;
            } else {
                if (Banking.openBank()) {
                    if (!Inventory.contains(equipmentID)) {
                        Bank.withdraw(equipmentID);
                    }
                    if (!Inventory.contains(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"))) {
                        Bank.withdraw(item -> item.getName().toLowerCase(Locale.ROOT).contains("coins"), 10000);
                    }
                }
            }
        }
    }

    private enum State {
        FISHING, TRAVELING, BANKING, FAILURE
    }

    private void setState() {
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
