package everything.skills.cooking;

import org.dreambot.api.methods.map.Area;

public class CookingConfig {

    private static final Area FALADOR_COOKING = new Area(3037, 3364, 3040, 3362);
    private static final Area EDGEVILLE_COOKING = new Area(3077, 3495, 3080, 3489);
    private static final int LOBSTER = 379;
    private static final int LOBSTER_RAW = 377;
    private static final int TUNA = 361;
    private static final int TUNA_RAW = 359;
    private static final int SWORDFISH = 373;
    private static final int SWORDFISH_RAW = 371;

    public CookingDto getCookingConfig(Fish fish, Location location) {
        var builder = new CookingDto.CookingDtoBuilder();
        switch (fish) {
            case LOBSTER:
                builder.setFish(LOBSTER);
                builder.setRawFish(LOBSTER_RAW);
                break;
            case TUNA:
                builder.setFish(TUNA);
                builder.setRawFish(TUNA_RAW);
                break;
            case SWORDFISH:
                builder.setFish(SWORDFISH);
                builder.setRawFish(SWORDFISH_RAW);
                break;
        }
        switch (location) {
            case EDGEVILLE:
                builder.setCookingLocation(EDGEVILLE_COOKING);
                break;
            case FALADOR:
                builder.setCookingLocation(FALADOR_COOKING);
                break;
        }
        return builder.build();
    }

    public enum Fish {
        LOBSTER, TUNA, SWORDFISH
    }

    public enum Location {
        EDGEVILLE, FALADOR
    }
}
