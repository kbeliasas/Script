package everything;

import everything.skills.cooking.CookingGUI;
import everything.skills.mining.MiningGUI;
import everything.skills.runecrafting.RuneCraftingGUI;
import org.dreambot.api.methods.skills.Skill;

public class GUIFactory {

    private final Main main;

    public GUIFactory(Main main) {
        this.main = main;
    }

    public SkillGUI createSkillGui(Skill skill) {

        switch (skill) {
            case ATTACK:
                break;
            case DEFENCE:
                break;
            case STRENGTH:
                break;
            case HITPOINTS:
                break;
            case RANGED:
                break;
            case PRAYER:
                break;
            case MAGIC:
                break;
            case COOKING:
                return new CookingGUI(main);
            case WOODCUTTING:
                break;
            case FLETCHING:
                break;
            case FISHING:
                break;
            case FIREMAKING:
                break;
            case CRAFTING:
                break;
            case SMITHING:
                break;
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
