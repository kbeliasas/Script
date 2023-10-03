package everything.skills.smithing;

import org.dreambot.api.methods.map.Area;

public class SmithingConfig {

    private static final int SILVER_ORE = 442;
    private static final Area FURNACE = new Area(3105, 3501, 3109, 3496);

    public SmithingDto getSmeltingConfig(Bar bar) {
        var builder = new SmithingDto.SmithingDtoBuilder();
        switch (bar) {
            case SILVER:
                builder.oreId(SILVER_ORE);
                builder.furnacePlace(FURNACE);
                builder.oresPerBar(1);
                break;
        }
        return builder.build();
    }
    public enum Type {
        SMITHING, SMELTING
    }

    public enum Bar {
        SILVER
    }
}
