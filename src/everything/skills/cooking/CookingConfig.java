package everything.skills.cooking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

public class CookingConfig {

    private static final Area FALADOR_COOKING = new Area(3037, 3364, 3040, 3362);
    private static final Area EDGEVILLE_COOKING = new Area(3077, 3495, 3080, 3489);

    @RequiredArgsConstructor
    @Getter
    public enum Fish {
        LOBSTER(379, 377),
        TUNA(361, 359),
        SWORDFISH(373, 371);

        private final Integer fishId;
        private final Integer rawFishId;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Location {
        EDGEVILLE(EDGEVILLE_COOKING),
        FALADOR(FALADOR_COOKING);

        private final Area cookingLocation;
    }
}
