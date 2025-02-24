package com.kbeliasas.everything.skills.magic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

public class HAConfig {
    static Integer NATURE_RUNE = 561;
    static Integer STAFF_OF_FIRE = 1387;
    static Area GE = new Area(3159, 3496, 3170, 3484);
    
    @Getter
    enum Type {
        STANDARD,
        ITEM
    }

    @RequiredArgsConstructor
    @Getter
    enum ItemStandard {
        RUNE_PICKAXE(1275, 20, 40), //buy limit 40
        RUNE_KITESHIELD(1201, 14, 70), //buy limit 70
        RUNE_PLATELEGS(1079, 10, 70), //buy limit 70
        RUNE_PLATEBODY(1127, 10, 70), //buy limit 70
        RUNE_FULL_HELM(1163, 14, 70); //buy limit 70

        private final Integer id;
        private final Integer buyPerRound;
        private final Integer buyLimit;
    }
    
    @RequiredArgsConstructor
    @Getter
    enum Item {
        STEEL_PLATEBODY(1119);

        private final Integer id;
    }
}
