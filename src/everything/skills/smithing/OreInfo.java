package everything.skills.smithing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OreInfo {
    private final int oreID;
    private final int count;
    private final int oresPerBar;
}
