package everything.naturalmouse.support;

import everything.naturalmouse.api.MouseInfoAccessor;
import org.dreambot.api.input.Mouse;

import java.awt.*;

public class RsMouseInfoAccessor implements MouseInfoAccessor {
    @Override
    public Point getMousePosition() {
        return Mouse.getPosition();
    }
}
