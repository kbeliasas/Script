package everything.skills.smithing;

import lombok.Builder;
import lombok.Getter;
import org.dreambot.api.methods.map.Area;

@Getter
@Builder
public class SmithingDto {
    private final int oreId;
    private final Area furnacePlace;
    private final int oresPerBar;
}
