package everything.skills.smithing;

import lombok.Builder;
import lombok.Getter;
import org.dreambot.api.methods.map.Area;

import java.util.List;

@Getter
@Builder
public class SmithingDto {
    private final List<OreInfo> oreInfos;
    private final Area furnacePlace;
}
