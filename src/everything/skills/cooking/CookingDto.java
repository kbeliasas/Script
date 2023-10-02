package everything.skills.cooking;

import org.dreambot.api.methods.map.Area;

public class CookingDto {
    private final int rawFish;
    private final int fish;
    private final Area cookingLocation;

    public int getRawFish() {
        return rawFish;
    }

    public int getFish() {
        return fish;
    }

    public Area getCookingLocation() {
        return cookingLocation;
    }

    private CookingDto(CookingDtoBuilder builder) {
        this.rawFish = builder.rawFish;
        this.fish = builder.fish;
        this.cookingLocation = builder.cookingLocation;
    }

    public static class CookingDtoBuilder {
        private int rawFish;
        private int fish;
        private Area cookingLocation;

        public CookingDtoBuilder setRawFish(int rawFish) {
            this.rawFish = rawFish;
            return this;
        }

        public CookingDtoBuilder setFish(int fish) {
            this.fish = fish;
            return this;
        }

        public CookingDtoBuilder setCookingLocation(Area cookingLocation) {
            this.cookingLocation = cookingLocation;
            return this;
        }

        public CookingDto build() {
            return new CookingDto(this);
        }

    }
}
