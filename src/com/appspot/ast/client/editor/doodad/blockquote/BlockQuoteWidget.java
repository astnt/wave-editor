package com.appspot.ast.client.editor.doodad.blockquote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.QuoteElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/6/11
 * Time: 1:54 PM
 */
public class BlockQuoteWidget extends Composite {
  interface Binder extends UiBinder<HTMLPanel, BlockQuoteWidget> { Binder BINDER = GWT.create(Binder.class); }
  @UiField QuoteElement container;

  public BlockQuoteWidget() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }

  public Element getContainer() {
    return container;
  }
}
