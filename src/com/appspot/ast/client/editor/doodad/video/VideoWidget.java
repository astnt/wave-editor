package com.appspot.ast.client.editor.doodad.video;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/7/11
 * Time: 7:37 PM
 */
public class VideoWidget extends Composite {
  @UiField IFrameElement iframe;

  public void setVideoSrc(String src) {
    iframe.setAttribute("src", src);
  }

  interface Binder extends UiBinder<HTMLPanel, VideoWidget> { Binder BINDER = GWT.create(Binder.class); }

  public VideoWidget() {
    initWidget(Binder.BINDER.createAndBindUi(this));
//    iframe.setAttribute("href", "http://www.youtube.com/embed/5ZxRuxSZfzw");
  }

  public Element getContainer() {
    return iframe;
  }
}
