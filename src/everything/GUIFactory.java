package everything;

import everything.skills.runecrafting.RuneCraftingGUI;
import org.dreambot.api.methods.skills.Skill;

public class GUIFactory {

    private final Main main;

    public GUIFactory(Main main) {
        this.main = main;
    }

    public SkillGUI createSkillGui(Skill skill) {
        if (skill.equals(Skill.RUNECRAFTING)) {
            main.setConfig(new Config(true, false, false));
            return new RuneCraftingGUI(main);
        }

        throw new IllegalStateException(String.format("Skill is still:%S not implemented ", skill.getName()));
    }
}
