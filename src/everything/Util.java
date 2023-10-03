package everything;

import org.dreambot.api.methods.interactive.Players;

public class Util {
    private boolean wasAnimating = true;
    public static void addLoot(String key) {
        if (!Main.looted.containsKey(key)) {
            Main.looted.put(key, 1);
        } else {
            Main.looted.put(key, Main.looted.get(key) + 1);
        }
    }

    public static void addLoot(String key, int amount) {
        if (!Main.looted.containsKey(key)) {
            Main.looted.put(key, amount);
        } else {
            Main.looted.put(key, Main.looted.get(key) + amount);
        }
    }

    public static void addIgnored(String key) {
        if (!Main.ignored.containsKey(key)) {
            Main.ignored.put(key, 1);
        } else {
            Main.ignored.put(key, Main.ignored.get(key) + 1);
        }
    }

    public boolean isAnimating() {
        if (Players.getLocal().isAnimating()) {
            wasAnimating = true;
            return true;
        } else {
            if (wasAnimating) {
                wasAnimating = false;
                return true;
            } else {
                return false;
            }
        }
    }
}
