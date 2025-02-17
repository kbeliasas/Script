package com.kbeliasas.everything.naturalmouse.support;

import com.kbeliasas.everything.naturalmouse.api.SystemCalls;
import org.dreambot.api.input.Mouse;

import java.awt.*;

public class RsSystemCalls implements SystemCalls {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
    }

    @Override
    public Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    @Override
    public void setMousePosition(int x, int y) {
        Mouse.hop(x,y);
    }
}
