package everything;

import everything.skills.combating.CombatingGUI;
import everything.skills.cooking.CookingGUI;
import everything.skills.crafting.CraftingGenericGUI;
import everything.skills.fishing.FishingGUI;
import everything.skills.magic.MagicGUI;
import everything.skills.mining.MiningGUI;
import everything.skills.runecrafting.RuneCraftingGUI;
import everything.skills.smithing.SmithingGenericGUI;
import everything.skills.woodcutting.WoodcuttingGUI;
import org.dreambot.api.methods.skills.Skill;

public class GUIFactory {

    private final Main main;

    public GUIFactory(Main main) {
        this.main = main;
    }

    public SkillGUI createSkillGui(Skill skill) {

        switch (skill) {
            case ATTACK:
            case STRENGTH:
            case DEFENCE:
                return new CombatingGUI(main, skill);
            case HITPOINTS:
                break;
            case RANGED:
                break;
            case PRAYER:
                break;
            case MAGIC:
                return new MagicGUI(main);
            case COOKING:
                return new CookingGUI(main);
            case WOODCUTTING:
                return new WoodcuttingGUI(main);
            case FLETCHING:
                break;
            case FISHING:
                return new FishingGUI(main);
            case FIREMAKING:
                break;
            case CRAFTING:
                return new CraftingGenericGUI(main);
            case SMITHING:
                return new SmithingGenericGUI(main);
            case MINING:
                return new MiningGUI(main);
            case HERBLORE:
                break;
            case AGILITY:
                break;
            case THIEVING:
                break;
            case SLAYER:
                break;
            case FARMING:
                break;
            case RUNECRAFTING:
                return new RuneCraftingGUI(main);
            case HUNTER:
                break;
            case CONSTRUCTION:
                break;
        }

        throw new IllegalStateException(String.format("%s is still not implemented ", skill.getName()));
    }
}
