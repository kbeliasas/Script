package everything.skills.magic;

import org.dreambot.api.methods.map.Area;

public class MagicConfig {
    private static final Area LESSER_DEMON = new Area(3108, 3163, 3111, 3159, 2);

    Area getMagicConfig(Mob mob) {
        switch (mob) {
            case LESSER_DEMON:
                return LESSER_DEMON;
        }
        return null;
    }

    enum Mob {
        LESSER_DEMON
    }
}
