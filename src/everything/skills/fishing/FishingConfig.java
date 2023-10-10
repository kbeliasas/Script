package everything.skills.fishing;

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

    public FishingDto getFishingConfig(Fish fish) {
        var builder = FishingDto.builder();
        switch (fish) {
            case LOBSTER:
                builder.area(MUSA_POINT);
                builder.fishId(LOBSTER_ID);
                builder.action(CAGE);
                builder.equipmentID(LOBSTER_POT);
                break;
            case SWORFISH:
                builder.area(MUSA_POINT);
                builder.fishId(SWORDFISH_ID);
                builder.action(HARPOON_ACTION);
                builder.equipmentID(HARPOON);
                break;
        }
        return builder.build();
    }

    enum Fish {
        LOBSTER, SWORFISH
    }
}
