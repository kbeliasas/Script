package everything.skills.smithing;

import org.dreambot.api.methods.map.Area;

import java.util.List;

public class SmithingConfig {

    private static final int SILVER_ORE = 442;
    private static final int IRON_ORE = 440;
    private static final int COAL_ORE = 453;
    private static final int STEEL_BAR = 2353;
    private static final Area FURNACE = new Area(3105, 3501, 3109, 3496);

    public SmithingDto getSmeltingConfig(Bar bar) {
        var builder = new SmithingDto.SmithingDtoBuilder();
        switch (bar) {
            case SILVER:
                var oreInfo = new OreInfo(SILVER_ORE, 1);
                builder.furnacePlace(FURNACE);
                builder.oreInfos(List.of(oreInfo));
                break;
            case STEEL:
                var oreInfo1 = new OreInfo(IRON_ORE, 1);
                var oreInfo2 = new OreInfo(COAL_ORE, 2);
                builder.furnacePlace(FURNACE);
                builder.oreInfos(List.of(oreInfo1, oreInfo2));
                break;
        }
        return builder.build();
    }

    public int getSmithingConfig(Bar bar) {
        switch (bar) {
            case SILVER:
                throw new RuntimeException("NOT POSIIBLE");
            case STEEL:
                return STEEL_BAR;
        }
        return 0;
    }
    public enum Type {
        SMITHING, SMELTING
    }

    public enum Bar {
        SILVER, STEEL
    }
}
