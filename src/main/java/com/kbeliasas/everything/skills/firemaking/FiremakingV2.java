package com.kbeliasas.everything.skills.firemaking;

import com.kbeliasas.everything.Main;
import com.kbeliasas.everything.skills.Banking;
import com.kbeliasas.everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
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

import java.util.Locale;

@RequiredArgsConstructor
public class FiremakingV2 implements GenericSkill {
    private final Main main;
    private final Integer LOG_ID;
    private final Integer TINDERBOX_ID = 590;
    private final Area AREA = new Area(3167, 3443, 3178, 3428);
    private State state;
    private boolean ready = false;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
            case PREP:
                if (Inventory.contains(TINDERBOX_ID)) {
                    ready = true;
                } else {
                    if (Banking.openBank()) {
                        Bank.depositAllEquipment();
                        Bank.depositAllItems();
                        Bank.withdraw(TINDERBOX_ID);
                    }
                }
                break;
            case FIREMAKING:
                    var logs = Inventory.get(LOG_ID);
                    var tinderbox = Inventory.get(TINDERBOX_ID);
                    if (logs != null) {
                        Inventory.interact(tinderbox, "Use");
                        Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(2000, 3000));
                        Inventory.interact(logs, "Use");
                        Sleep.sleep(Calculations.random(200, 500));
                        Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(),
                                Calculations.random(10000, 20000),
                                Calculations.random(200, 500));
                    }

                    if (Dialogues.canContinue()) {
                        Dialogues.continueDialogue();
                    }
                break;
            case TRAVELING:
            case ON_FIRE:
                Walking.walk(AREA.getRandomTile());
                break;
            case BANKING:
                if (Banking.openBank()) {
                    if (Bank.contains(LOG_ID)) {
                        Bank.withdrawAll(LOG_ID);
                    } else {
                        Logger.error("NO MORE LOGS!");
                        main.printResults();
                        ScriptManager.getScriptManager().stop();
                    }
                }
                break;
            case FINISHING:
                if (Banking.openBank()) {
                    Bank.depositAllItems();
                    Logger.info("TARGET XP REACHED!");
                    main.printResults();
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

    private enum State {
        PREP, FIREMAKING, TRAVELING, BANKING, FINISHING, ON_FIRE, ERROR
    }

    private void setState() {
        if (Skills.getExperience(Skill.FIREMAKING) >= main.getGoalXp()) {
            state = State.FINISHING;
            return;
        }

        if (!ready) {
            state = State.PREP;
            return;
        }

        if (!Inventory.contains(LOG_ID)) {
            state = State.BANKING;
            return;
        }

        if (!AREA.contains(Players.getLocal())) {
            state = State.TRAVELING;
            return;
        }

        var fire = GameObjects.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("fire")
                && object.getTile().equals(Players.getLocal().getTile()));
        var daisy = GameObjects.closest(object -> object.getName().toLowerCase(Locale.ROOT).contains("daisies")
                && object.getTile().equals(Players.getLocal().getTile()));

        if (fire != null || daisy != null) {
            state = State.ON_FIRE;
            return;
        }

        if (AREA.contains(Players.getLocal())) {
            state = State.FIREMAKING;
            return;
        }

        state = State.ERROR;
    }
}
