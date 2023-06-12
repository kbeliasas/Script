package everything;

import org.dreambot.api.input.mouse.algorithm.MouseMovementAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;
import org.dreambot.api.utilities.Logger;

public class NaturalMouse implements MouseMovementAlgorithm {
    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        var destPoint = abstractMouseDestination.getSuitablePoint();
        try {
            Main.mouseMotionFactory.move(destPoint.x, destPoint.y);
        } catch (InterruptedException e) {
            Logger.error("mouseMotion was interrupted", e);
        }
        return true;
    }
}
