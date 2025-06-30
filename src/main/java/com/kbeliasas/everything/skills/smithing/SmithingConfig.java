package com.kbeliasas.everything.skills.smithing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

import java.util.List;

public class SmithingConfig {

    private static final int SILVER_ORE = 442;
    private static final int IRON_ORE = 440;
    private static final int COAL_ORE = 453;
    private static final int MITHRIL_ORE = 447;
    private static final Area FURNACE = new Area(3105, 3501, 3109, 3496);

    public enum Type {
        SMITHING, SMELTING
    }

    @RequiredArgsConstructor
    @Getter
    public enum Bar {
        SILVER(List.of(new OreInfo(SILVER_ORE, 28, 1)),16),
        STEEL(List.of(new OreInfo(IRON_ORE, 9, 1), new OreInfo(COAL_ORE, 18, 2)), 17),
        MITHRIL(List.of(new OreInfo(MITHRIL_ORE, 5, 1), new OreInfo(COAL_ORE, 20, 4)), 19);

        private final List<OreInfo> oreInfos;
        private final Integer widgetId;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Platebody {
        STEEL(2353),
        MITHRIL(2359);

        private final Integer barId;
    }
}
