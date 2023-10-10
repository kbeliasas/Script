package everything.skills.fishing;

import lombok.Builder;
import lombok.Getter;
import org.dreambot.api.methods.map.Area;

@Getter
@Builder
public class FishingDto {
    private final Area area;
    private final String action;
    private final int fishId;
    private final int equipmentID;
}
