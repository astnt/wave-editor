package com.appspot.ast.client.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:20
 */
public class WaveEditorLayout extends Composite {
  interface Binder extends UiBinder<Widget, WaveEditorLayout> { Binder BINDER = GWT.create(Binder.class); }
  interface MyStyle extends CssResource {
    String sourceContainer();
    String source();
    String item();
    String original();
    String imageContainer();
    String log();
  }

  @UiField MyStyle style;

  public WaveEditorLayout() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }
}
