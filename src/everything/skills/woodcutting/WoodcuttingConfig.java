package everything.skills.woodcutting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

public class WoodcuttingConfig {
    @RequiredArgsConstructor
    @Getter
    enum Axe {
        RUNE(1359);

        private final Integer axeId;
    }

    @RequiredArgsConstructor
    @Getter
    enum Tree {
        WILLOW(new Area(3084, 3238, 3092, 3229), 1519, "willow");

        private final Area area;
        private final Integer logsId;
        private final String tree;
    }
}
