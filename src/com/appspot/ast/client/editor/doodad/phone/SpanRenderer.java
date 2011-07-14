package com.appspot.ast.client.editor.doodad.phone;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import org.waveprotocol.wave.client.editor.RenderingMutationHandler;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.Renderer;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 06.07.11
 * Time: 12:59
 */
public class SpanRenderer extends RenderingMutationHandler implements Renderer {
  @Override public Element createDomImpl(Renderable renderable) {
    return DOM.createSpan();
  }

  @Override public void onActivationStart(ContentElement element) {
    fanoutAttrs(element);
  }
}
