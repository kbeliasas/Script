package com.kbeliasas.everything.skills.combating;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;

import java.util.HashMap;
import java.util.Map;

public class CombatingConfig {

    static Integer NATURE_RUNE = 561;

    @Getter
    enum Mob {
        MOSS_GIANT(new Area(3156, 9906, 3160, 9901), "Moss giant", 532);

        private final Area area;
        private final String name;
        private final Integer bonesId;

        Mob(Area area, String name, Integer bonesId) {
            this.area = area;
            this.name = name;
            this.bonesId = bonesId;
        }
    }

    @Getter
    enum Food {
        LOBSTER(379),
        SWORDFISH(373);

        private final int id;

        Food(int id) {
            this.id = id;
        }
    }

    @RequiredArgsConstructor
    @Getter
    enum Equipment {
        RUNE(getRuneEquipmentMap());

        private final Map<EquipmentSlot, Integer> equipmentMap;
    }

    private static Map<EquipmentSlot, Integer> getRuneEquipmentMap() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1113);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1059);
        map.put(EquipmentSlot.HAT, 1163);
        map.put(EquipmentSlot.LEGS, 1079);
        map.put(EquipmentSlot.SHIELD, 1201);
        map.put(EquipmentSlot.WEAPON, 1333);
        return map;
    }
}
