package com.appspot.ast.client.editor.doodad.video;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/7/11
 * Time: 7:37 PM
 */
public class VideoWidget extends Composite {
  interface Binder extends UiBinder<HTMLPanel, VideoWidget> { Binder BINDER = GWT.create(Binder.class); }

  @UiField IFrameElement iframe;
  @UiField Button changeUrl;

  public void setVideoSrc(String src) {
    iframe.setAttribute("src", src);
  }

  public VideoWidget() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }

  public Element getContainer() {
    return iframe;
  }

  @UiHandler("changeUrl")
  public void handleClickChangeUrl(ClickEvent event) {
    String updatedUrl = Window.prompt("URL:", "http://www.youtube.com/embed/Ot2NYFFO-2Q");
    setVideoSrc(updatedUrl);
  }
}
