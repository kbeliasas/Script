package everything.skills.cooking;

import org.dreambot.api.methods.map.Area;

public class CookingConfig {

    private static final Area FALADOR_COOKING = new Area(3037, 3364, 3040, 3362);
    private static final Area EDGEVILLE_COOKING = new Area(3077, 3495, 3080, 3489);
    private static final int LOBSTER = 379;
    private static final int LOBSTER_RAW = 377;

    public CookingDto getCookingConfig(Fish fish, Location location) {
        var builder = new CookingDto.CookingDtoBuilder();
        switch (fish) {
            case LOBSTER:
                builder.setFish(LOBSTER);
                builder.setRawFish(LOBSTER_RAW);
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
        LOBSTER
    }

    public enum Location {
        EDGEVILLE, FALADOR
    }
}
