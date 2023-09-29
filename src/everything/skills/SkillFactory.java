package everything.skills;

import everything.Config;
import org.dreambot.api.methods.skills.Skill;

public class SkillFactory {

    public SkillEverything getSkill(Skill skill) {
        if (skill.equals(Skill.RUNECRAFTING)) {
            return new RuneCraftingV2();
        }

        throw new IllegalStateException(String.format("Skill is still:%S not implemented ", skill.getName()));
    }

    public Config setConfig(Skill skill) {
        if (skill.equals(Skill.RUNECRAFTING)) {
            return new Config(true, false, false);
        }

        throw new IllegalStateException(String.format("Skill is still:%S not implemented ", skill.getName()));
    }
}
