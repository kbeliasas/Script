package everything;

public class Config {
    private final boolean paintTimeToGoalItems;
    private final boolean paintTimeToGoalItemsCollect;
    private final boolean paintTimeToGoalLevels;

    public Config(boolean paintTimeToGoalItems, boolean paintTimeToGoalItemsCollect, boolean paintTimeToGoalLevels) {
        this.paintTimeToGoalItems = paintTimeToGoalItems;
        this.paintTimeToGoalItemsCollect = paintTimeToGoalItemsCollect;
        this.paintTimeToGoalLevels = paintTimeToGoalLevels;
    }

    public boolean isPaintTimeToGoalItems() {
        return paintTimeToGoalItems;
    }

    public boolean isPaintTimeToGoalItemsCollect() {
        return paintTimeToGoalItemsCollect;
    }

    public boolean isPaintTimeToGoalLevels() {
        return paintTimeToGoalLevels;
    }
}
