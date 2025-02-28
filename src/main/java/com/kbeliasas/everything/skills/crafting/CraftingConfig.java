package com.kbeliasas.everything.skills.crafting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class CraftingConfig {

    private static final int SILVER_BAR = 2355;

    public JewelleryDto getJewelleryConfig(JewelleryProducts product) {
        var builder = new JewelleryDto.JewelleryDtoBuilder();
        switch (product) {
            case TIARA:
                builder.barID(SILVER_BAR);
                break;
        }
        return builder.build();
    }

    public enum CraftingType {
        JEWELLERY
    }

    @RequiredArgsConstructor
    @Getter
    public enum JewelleryProducts {
        TIARA(5523, 5525, List.of(Resource.SILVER_BAR), new int[]{6, 28, 3}),
        DIAMOND_NECKLACE(1597, 1662, List.of(Resource.GOLD_BAR, Resource.DIAMOND),  new int[]{446, 27});

        private final Integer mouldId;
        private final Integer productId;
        private final List<Resource> resources;
        private final int[] widgetIds;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Resource {
        SILVER_BAR(2355, 27),
        GOLD_BAR(2357, 13),
        DIAMOND(1601, 13);

        private final Integer id;
        private final Integer amount;
    }
}
