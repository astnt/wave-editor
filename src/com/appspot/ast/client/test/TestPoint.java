package com.appspot.ast.client.test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 12:49
 */
public class TestPoint implements EntryPoint {
  public void onModuleLoad() {
    RootPanel.get().add(new WaveEditorLayout());
  }
}
