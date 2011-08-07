package com.appspot.ast.client.editor.doodad.paragraph;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 8/7/11
 * Time: 11:38 AM
 */
public class ParagraphWidget extends Composite {
  interface Binder extends UiBinder<HTMLPanel, ParagraphWidget> { Binder BINDER = GWT.create(Binder.class); }

  public ParagraphWidget() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }

  public Element getContainer() {
    return getElement();
  }
}
