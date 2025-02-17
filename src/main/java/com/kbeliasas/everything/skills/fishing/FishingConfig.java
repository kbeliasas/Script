package com.kbeliasas.everything.skills.fishing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

public class FishingConfig {

    private static final int LOBSTER_ID = 377;
    private static final int SWORDFISH_ID = 371;
    private static final int LOBSTER_POT = 301;
    private static final int HARPOON = 311;
    private static final String CAGE = "Cage";
    private static final String HARPOON_ACTION = "Harpoon";
    private static final Area FISHING_SPOT_LUMBRIDGE = new Area(3238, 3152, 3239, 3144);
    private static final Area FISHING_SPOT_BARBARIAN_VILLAGE = new Area(3100, 3427, 3103, 3424);
    private static final Area MUSA_POINT = new Area(2923, 3180, 2925, 3175);

    @RequiredArgsConstructor
    @Getter
    enum Fish {
        LOBSTER(MUSA_POINT, LOBSTER_ID, CAGE, LOBSTER_POT),
        SWORFISH(MUSA_POINT, SWORDFISH_ID, HARPOON_ACTION, HARPOON);

        private final Area area;
        private final Integer fishId;
        private final String action;
        private final Integer equipmentId;
    }
}
