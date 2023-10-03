package everything.skills.mining;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.api.methods.map.Area;

@Getter
@Builder
@RequiredArgsConstructor
public class MiningDto {
    private final String rocksName;
    private final int oreID;
    private final Area mine;
    private final int pickaxe;
    private final int distance;
}
