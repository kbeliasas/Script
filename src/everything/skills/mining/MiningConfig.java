package everything.skills.mining;

import org.dreambot.api.methods.map.Area;

public class MiningConfig {

    private static Area AL_KHARID_MINE = new Area(3307, 3278, 3291, 3319);
    private static int SILVER_ORE = 442;
    private static String SILVER_ROCKS = "Silver rocks";
    private static int RUNE_PICKAXE = 1275;

    public MiningDto getMiningConfig(Ore ore, Pickaxe pickaxe) {
        var builder = new MiningDto.MiningDtoBuilder();
        switch (ore) {
            case SILVER:
                builder.rocksName(SILVER_ROCKS);
                builder.oreID(SILVER_ORE);
                builder.mine(AL_KHARID_MINE);
                builder.distance(20);
                break;
        }
        switch (pickaxe) {
            case RUNE:
                builder.pickaxe(RUNE_PICKAXE);
                break;
        }
        return builder.build();
    }

    public enum Ore {
        SILVER
    }

    public enum Pickaxe {
        RUNE
    }
}
