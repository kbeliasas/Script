package everything.skills.mining;

import org.dreambot.api.methods.map.Area;

public class MiningConfig {

    private static Area AL_KHARID_MINE = new Area(3307, 3278, 3291, 3319);
    private static Area SOUTH_EAST_VARROCK_MINE = new Area(3285, 3372, 3289, 3369);
    private static Area FALADOR_MINING_GUILD = new Area(3032, 9743, 3050, 9735);
    private static int SILVER_ORE = 442;
    private static String SILVER_ROCKS = "Silver rocks";
    private static String IRON_ROCKS = "Iron rocks";
    private static int IRON_ORE = 440;
    private static String COAL_ROCKS = "Coal rocks";
    private static int COAL_ORE = 453;
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
            case IRON:
                builder.rocksName(IRON_ROCKS);
                builder.oreID(IRON_ORE);
                builder.mine(SOUTH_EAST_VARROCK_MINE);
                builder.distance(10);
                break;
            case COAL:
                builder.rocksName(COAL_ROCKS);
                builder.oreID(COAL_ORE);
                builder.mine(FALADOR_MINING_GUILD);
                builder.distance(10);
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
        SILVER, IRON, COAL
    }

    public enum Pickaxe {
        RUNE
    }
}
