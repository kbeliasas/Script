package everything;

public class Util {
    public static void addLoot(String key) {
        if (Main.looted.containsKey(key)) {
            Main.looted.put(key, 1);
        } else {
            Main.looted.put(key, Main.looted.get(key) + 1);
        }
    }

    public static void addIgnored(String key) {
        if (Main.ignored.containsKey(key)) {
            Main.ignored.put(key, 1);
        } else {
            Main.ignored.put(key, Main.ignored.get(key) + 1);
        }
    }
}
