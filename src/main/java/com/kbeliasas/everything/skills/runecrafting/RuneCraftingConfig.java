package com.kbeliasas.everything.skills.runecrafting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class RuneCraftingConfig {

    private static final Area BODY_RUINS = new Area(3050, 3446, 3054, 3439);
    private static final Area AIR_RUINS = new Area(2982, 3296, 2986, 3293);

    @RequiredArgsConstructor
    @Getter
    public enum Runes {
        BODY(getBodyRunesCount(), BODY_RUINS, 5533),
        AIR(getAirRunesCount(), AIR_RUINS, 5527);

        private final Integer runesCount;
        private final Area ruins;
        private final Integer tiaraId;
    }

    private static int getBodyRunesCount() {
        var level = Skills.getRealLevel(Skill.RUNECRAFTING);
        if (level < 46) {
            return 1;
        }
        if (level < 92) {
            return 2;
        }
        return 3;
    }

    private static int getAirRunesCount() {
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
