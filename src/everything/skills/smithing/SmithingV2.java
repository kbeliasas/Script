package everything.skills.smithing;

import everything.Main;
import everything.Util;
import everything.skills.GenericSkill;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

@RequiredArgsConstructor
public class SmithingV2 implements GenericSkill {

    private final Main main;
    private final Util util;
    private final SmithingConfig.Type type;
    private final int oreId;
    private final Area furnacePlace;
    private final int oresPerBar;
    private SmithingGeneric smithingGeneric;
    @Override
    public void execute() {
        if (smithingGeneric == null) {
            if (type == SmithingConfig.Type.SMELTING) {
                smithingGeneric = new Smeting(main, util, oreId, furnacePlace, oresPerBar);
            }
        }
    }
}
