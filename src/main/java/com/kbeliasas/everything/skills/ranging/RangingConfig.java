package com.kbeliasas.everything.skills.ranging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;

import java.util.HashMap;
import java.util.Map;

public class RangingConfig {

    static Integer NATURE_RUNE = 561;
    static Integer KNIFE = 946;

    @Getter
    @RequiredArgsConstructor
    enum Mob {
        GOBLINS(new Area(3245, 3242, 3256, 3231), "Goblin", 526),
        BARBARIANS(new Area(3073, 3425, 3088, 3410), "Barbarian", 526),
        GIANT_FROGS(new Area(3180, 3195, 3207, 3178), "Giant frog", 532),
        MOSS_GIANT(new Area(3156, 9906, 3160, 9901), "Moss giant", 532);

        private final Area area;
        private final String name;
        private final Integer bonesId;
    }

    @Getter
    @RequiredArgsConstructor
    enum Food {
        LOBSTER(379),
        SWORDFISH(373);

        private final int id;

    }

    @RequiredArgsConstructor
    @Getter
    enum Equipment {
        LVL40(getEquipmentMapLVL40()),
        LVL30(getEquipmentMapLVL30()),
        LVL20(getEquipmentMapLVL20()),
        LVL5(getEquipmentMapLVL5()),
        LVL1(getEquipmentMapLVL1());

        private final Map<EquipmentSlot, Integer> equipmentMap;
    }

    private static Map<EquipmentSlot, Integer> getEquipmentMapLVL1() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1131);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1063);
        map.put(EquipmentSlot.HAT, 1167);
        map.put(EquipmentSlot.LEGS, 1095);
        map.put(EquipmentSlot.WEAPON, 841);
        map.put(EquipmentSlot.CAPE, 1019);
        map.put(EquipmentSlot.ARROWS, 884);
        return map;
    }

    private static Map<EquipmentSlot, Integer> getEquipmentMapLVL5() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1131);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1063);
        map.put(EquipmentSlot.HAT, 1167);
        map.put(EquipmentSlot.LEGS, 1095);
        map.put(EquipmentSlot.WEAPON, 843);
        map.put(EquipmentSlot.CAPE, 1019);
        map.put(EquipmentSlot.ARROWS, 886);
        return map;
    }

    private static Map<EquipmentSlot, Integer> getEquipmentMapLVL20() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1133);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1063);
        map.put(EquipmentSlot.HAT, 1169);
        map.put(EquipmentSlot.LEGS, 1097);
        map.put(EquipmentSlot.WEAPON, 849);
        map.put(EquipmentSlot.CAPE, 1019);
        map.put(EquipmentSlot.ARROWS, 888);
        return map;
    }

    private static Map<EquipmentSlot, Integer> getEquipmentMapLVL30() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1133);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1063);
        map.put(EquipmentSlot.HAT, 1169);
        map.put(EquipmentSlot.LEGS, 1097);
        map.put(EquipmentSlot.WEAPON, 853);
        map.put(EquipmentSlot.CAPE, 1019);
        map.put(EquipmentSlot.ARROWS, 890);
        return map;
    }

    private static Map<EquipmentSlot, Integer> getEquipmentMapLVL40() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1731);
        map.put(EquipmentSlot.CHEST, 1135);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1065);
        map.put(EquipmentSlot.HAT, 1169);
        map.put(EquipmentSlot.LEGS, 1099);
        map.put(EquipmentSlot.WEAPON, 853);
        map.put(EquipmentSlot.CAPE, 1019);
        map.put(EquipmentSlot.ARROWS, 890);
        return map;
    }
}
