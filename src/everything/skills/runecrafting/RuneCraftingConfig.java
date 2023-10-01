package everything.skills.runecrafting;

import everything.Constatnts;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class RuneCraftingConfig {

    private static final Area BODY_RUINS = new Area(3050, 3446, 3054, 3439);

    public RuneCraftingDto getRuneCraftingConfig(Runes runes) {
        switch (runes) {
            case BODY:
                return new RuneCraftingDto(getBodyRunesCount(), BODY_RUINS, Constatnts.BODY_TIARA);
        }
        throw new IllegalStateException("Unknown runes");
    }
    public enum Runes {
        BODY
    }

    private int getBodyRunesCount() {
        var level = Skills.getRealLevel(Skill.RUNECRAFTING);
        if (level < 46) {
            return 1;
        }
        if (level < 92) {
            return 2;
        }
        return 3;
    }
}
