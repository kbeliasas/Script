package com.kbeliasas.everything.skills.crafting;

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

    public enum JewelleryProducts {
        TIARA
    }
}
