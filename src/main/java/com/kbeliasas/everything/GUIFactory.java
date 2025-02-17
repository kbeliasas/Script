package com.kbeliasas.everything;

import com.kbeliasas.everything.skills.combating.CombatingGUI;
import com.kbeliasas.everything.skills.cooking.CookingGUI;
import com.kbeliasas.everything.skills.crafting.CraftingGenericGUI;
import com.kbeliasas.everything.skills.firemaking.FiremakingGUI;
import com.kbeliasas.everything.skills.fishing.FishingGUI;
import com.kbeliasas.everything.skills.magic.MagicGUI;
import com.kbeliasas.everything.skills.mining.MiningGUI;
import com.kbeliasas.everything.skills.runecrafting.RuneCraftingGUI;
import com.kbeliasas.everything.skills.smithing.SmithingGenericGUI;
import com.kbeliasas.everything.skills.woodcutting.WoodcuttingGUI;
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
                return new FiremakingGUI(main);
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
