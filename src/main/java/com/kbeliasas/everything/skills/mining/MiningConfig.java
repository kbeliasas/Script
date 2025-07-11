package com.kbeliasas.everything.skills.mining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

public class MiningConfig {

    private static Area AL_KHARID_MINE = new Area(3307, 3278, 3291, 3319);
    private static Area SOUTH_EAST_VARROCK_MINE = new Area(3285, 3372, 3289, 3369);
    private static Area FALADOR_MINING_GUILD = new Area(3054, 9732, 3024, 9754);
    private static Area FALADOR_MINING_GUILD_MITHRIL = new Area(3055, 9741, 3030, 9712);

    @RequiredArgsConstructor
    @Getter
    public enum Ore {
        SILVER("Silver rocks", 442, AL_KHARID_MINE, 20),
        IRON("Iron rocks", 440, SOUTH_EAST_VARROCK_MINE, 10),
        COAL("Coal rocks", 453, FALADOR_MINING_GUILD, 10),
        MITHRIL("Mithril rocks", 447, FALADOR_MINING_GUILD, 20);

        private final String rockName;
        private final Integer oreId;
        private final Area mine;
        private final Integer distance;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Pickaxe {
        RUNE(1275);

        private final Integer pickaxeId;
    }
}
