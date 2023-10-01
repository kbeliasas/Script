package everything.skills.runecrafting;

import org.dreambot.api.methods.map.Area;

public class RuneCraftingDto {
    private final int runesPerEssence;
    private final Area ruins;
    private final int tiara;

    public RuneCraftingDto(int runesPerEssence, Area ruins, int tiara) {
        this.runesPerEssence = runesPerEssence;
        this.ruins = ruins;
        this.tiara = tiara;
    }

    public int getRunesPerEssence() {
        return runesPerEssence;
    }

    public Area getRuins() {
        return ruins;
    }

    public int getTiara() {
        return tiara;
    }
}
