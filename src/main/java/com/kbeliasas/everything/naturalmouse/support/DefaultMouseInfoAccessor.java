package com.kbeliasas.everything.naturalmouse.support;

import com.kbeliasas.everything.naturalmouse.api.MouseInfoAccessor;

import java.awt.*;

public class DefaultMouseInfoAccessor implements MouseInfoAccessor {

  @Override
  public Point getMousePosition() {
    return MouseInfo.getPointerInfo().getLocation();
  }
}
