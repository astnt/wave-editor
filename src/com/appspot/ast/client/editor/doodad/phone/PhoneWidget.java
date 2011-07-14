package com.appspot.ast.client.editor.doodad.phone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 06.07.11
 * Time: 12:09
 */
public class PhoneWidget extends Composite {
  interface Binder extends UiBinder<HTMLPanel, PhoneWidget> {}
  private static final Binder BINDER = GWT.create(Binder.class);
  @UiField SpanElement container;

  public PhoneWidget() {
    initWidget(BINDER.createAndBindUi(this));
  }

  public SpanElement getContainer() {
    return container;
  }
}
