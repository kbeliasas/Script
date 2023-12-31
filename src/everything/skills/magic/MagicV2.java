package everything.skills.magic;

import everything.Main;
import everything.skills.Banking;
import everything.skills.GenericSkill;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
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

import java.util.Locale;

@RequiredArgsConstructor
public class MagicV2 implements GenericSkill {
    private final Main main;
    private final Area area;
    private State state;
    private static final int MIND_RUNE = 558;

    @Override
    public void execute() {
        setState();
        main.setStateString(state.name());
        switch (state) {
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
                    Main.printResults();
                    ScriptManager.getScriptManager().stop();
                }
                break;
            case FAILURE:
                Logger.error("ERROR State failed to set state;");
                main.printResults();
                ScriptManager.getScriptManager().stop();
        }
    }

    private enum State {
        MAGIC, TRAVELING, FINISH, FAILURE
    }

    private void setState() {
        if (!Inventory.contains(MIND_RUNE) || Skills.getExperience(Skill.MAGIC) > main.getGoalXp()) {
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
