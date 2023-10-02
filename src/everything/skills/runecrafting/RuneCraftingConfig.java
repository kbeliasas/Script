package everything.skills.runecrafting;

import everything.Constants;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class RuneCraftingConfig {

    private static final Area BODY_RUINS = new Area(3050, 3446, 3054, 3439);
    private static final Area AIR_RUINS = new Area(2982, 3296, 2986, 3293);

    public RuneCraftingDto getRuneCraftingConfig(Runes runes) {
        switch (runes) {
            case BODY:
                return new RuneCraftingDto(getBodyRunesCount(), BODY_RUINS, Constants.BODY_TIARA);
            case AIR:
                return new RuneCraftingDto(getAirRunesCount(), AIR_RUINS, Constants.AIR_TIARA);
        }
        throw new IllegalStateException("Unknown runes");
    }
    public enum Runes {
        BODY, AIR
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

    private int getAirRunesCount() {
        var level = Skills.getRealLevel(Skill.RUNECRAFTING);
        if (level < 11) {
            return 1;
        }
        if (level < 22) {
            return 2;
        }
        if (level < 33) {
            return 3;
        }
        if (level < 44) {
            return 4;
        }
        if (level < 55) {
            return 5;
        }
        if (level < 66) {
            return 6;
        }
        if (level < 77) {
            return 7;
        }
        if (level < 88) {
            return 8;
        }
        if (level < 99) {
            return 9;
        }
        return 10;
    }
}
