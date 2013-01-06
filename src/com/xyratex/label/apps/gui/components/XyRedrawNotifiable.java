package com.xyratex.label.apps.gui.components;

import javax.swing.JFrame;

public interface XyRedrawNotifiable
{
  public void redrawRequired( XyChildGui childGui );
  public JFrame getJFrame();
}
