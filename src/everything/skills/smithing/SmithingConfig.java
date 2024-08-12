package everything.skills.smithing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

import java.util.List;

public class SmithingConfig {

    private static final int SILVER_ORE = 442;
    private static final int IRON_ORE = 440;
    private static final int COAL_ORE = 453;
    private static final int STEEL_BAR = 2353;
    private static final Area FURNACE = new Area(3105, 3501, 3109, 3496);

    public int getSmithingConfig(Bar bar) {
        switch (bar) {
            case SILVER:
                throw new RuntimeException("NOT POSSIBLE");
            case STEEL:
                return STEEL_BAR;
        }
        return 0;
    }

    public enum Type {
        SMITHING, SMELTING
    }

    @RequiredArgsConstructor
    @Getter
    public enum Bar {
        SILVER(List.of(new OreInfo(SILVER_ORE, 28, 1)),16),
        STEEL(List.of(new OreInfo(IRON_ORE, 9, 1), new OreInfo(COAL_ORE, 18, 2)), 17);

        private final List<OreInfo> oreInfos;
        private final Integer widgetId;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Platebody {
        STEEL(2353);

        private final Integer barId;
    }
}
