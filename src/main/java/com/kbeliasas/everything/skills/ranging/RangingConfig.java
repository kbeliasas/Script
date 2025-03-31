package com.kbeliasas.everything.skills.ranging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;

import java.util.HashMap;
import java.util.Map;

public class RangingConfig {

    static Integer NATURE_RUNE = 561;

    @Getter
    @RequiredArgsConstructor
    enum Mob {
        GOBLINS(new Area(3245, 3242, 3256, 3231), "Goblin", 526),
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
        LVL1(getRuneEquipmentMapLVL1());

        private final Map<EquipmentSlot, Integer> equipmentMap;
    }

    private static Map<EquipmentSlot, Integer> getRuneEquipmentMapLVL1() {
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

    @Getter
    @RequiredArgsConstructor
    enum Arrows {
        IRON(884);

        private final int arrowId;
    }
}
