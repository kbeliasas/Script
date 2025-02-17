package com.kbeliasas.everything.skills.magic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.map.Area;

import java.util.HashMap;
import java.util.Map;

public class MagicConfig {
    private static final Area LESSER_DEMON_AREA = new Area(3108, 3163, 3111, 3159, 2);

    @RequiredArgsConstructor
    @Getter
    enum Mob {
        LESSER_DEMON(LESSER_DEMON_AREA, getMagicEquipment());

        private final Area area;
        private final Map<EquipmentSlot, Integer> equipmentMap;
    }

    private static Map<EquipmentSlot, Integer> getMagicEquipment() {
        var map = new HashMap<EquipmentSlot, Integer>();
        map.put(EquipmentSlot.AMULET, 1727);
        map.put(EquipmentSlot.CHEST, 577);
        map.put(EquipmentSlot.FEET, 1061);
        map.put(EquipmentSlot.HANDS, 1059);
        map.put(EquipmentSlot.HAT, 579);
        map.put(EquipmentSlot.LEGS, 1033);
        map.put(EquipmentSlot.WEAPON, 1387);
        return map;
    }
}
