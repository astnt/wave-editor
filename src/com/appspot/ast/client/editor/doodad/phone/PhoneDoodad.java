package com.appspot.ast.client.editor.doodad.phone;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.editor.ElementHandlerRegistry;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.client.editor.content.misc.ChunkyElementHandler;
import org.waveprotocol.wave.client.editor.content.misc.DisplayEditModeHandler;
import org.waveprotocol.wave.client.editor.content.misc.LinoTextEventHandler;
import org.waveprotocol.wave.client.editor.content.misc.UpdateContentEditable;
import org.waveprotocol.wave.client.editor.content.paragraph.ParagraphRenderer;
import org.waveprotocol.wave.client.editor.event.EditorEvent;
import org.waveprotocol.wave.client.editor.gwt.GwtRenderingMutationHandler;
import org.waveprotocol.wave.model.document.util.Point;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 06.07.11
 * Time: 12:05
 */
public class PhoneDoodad {
  public static String TAGNAME = "phone";
  public static String CODE_TAGNAME = "code";
  public static String NUMBER_TAGNAME = "number";

  public static void register(ElementHandlerRegistry registry) {
    PhoneRenderer renderer = new PhoneRenderer();

    registry.registerRenderingMutationHandler(TAGNAME, renderer);
//    registry.registerRenderingMutationHandler(CODE_TAGNAME, new SpanRenderer());
    registry.registerRenderingMutationHandler(CODE_TAGNAME, ParagraphRenderer.create("span"));
    registry.registerRenderingMutationHandler(NUMBER_TAGNAME, ParagraphRenderer.create("span"));

    registry.registerEventHandler(TAGNAME, new PhoneEventHanler(renderer));
    registry.registerEventHandler(CODE_TAGNAME, new CodeEventHandler());
    registry.registerEventHandler(NUMBER_TAGNAME, new CodeEventHandler());
  }

  static class PhoneRenderer extends GwtRenderingMutationHandler {
    PhoneRenderer() {
      super(Flow.INLINE);
    }

    @Override
    protected Widget createGwtWidget(Renderable renderable) {
      return new PhoneWidget();
    }

    @Override
    protected Element getContainerNodelet(Widget w) {
      return ((PhoneWidget) w).getContainer();
    }

    @Override
    public void onActivatedSubtree(ContentElement element) {
      super.onActivatedSubtree(element);
      fanoutAttrs(element);
    }

    @Override
    public void onAttributeModified(
        ContentElement element, String name, String oldValue, String newValue) {
      super.onAttributeModified(element, name, oldValue, newValue);
    }

//      if (REF_ATTR.equals(name)) {
//        getWidget(element).setImageSrc(newValue);
//      }

    /** Convenience getter */
    PhoneWidget getWidget(ContentElement e) {
      return ((PhoneWidget) getGwtWidget(e));
    }
  }

  static class PhoneEventHanler extends ChunkyElementHandler {
    private final PhoneRenderer renderer;

    public PhoneEventHanler(PhoneRenderer renderer) {
      this.renderer = renderer;
    }

    @Override
    public boolean handleLeftAfterNode(ContentElement element, EditorEvent event) {
      ContentElement caption = getCode(element);

      if (caption != null) {
        // If we have a caption, move the selection into the caption
        element.getSelectionHelper().setCaret(
            Point.<ContentNode> end(getCode(element)));
        return true;
      } else {
        // If we don't have a caption, use the default behavior
        return super.handleLeftAfterNode(element, event);
      }
    }

    @Override
    public boolean handleLeftAtBeginning(ContentElement element, EditorEvent event) {
      // NOTE: The use of location mapper will normalise into text nodes.
      element.getSelectionHelper().setCaret(element.getLocationMapper().getLocation(
          Point.before(element.getRenderedContentView(), element)));
      return true;
    }

    @Override
    public boolean handleRightAtEnd(ContentElement element, EditorEvent event) {
      // NOTE: The use of location mapper will normalise into text nodes.
      element.getSelectionHelper().setCaret(element.getLocationMapper().getLocation(
          Point.after(element.getRenderedContentView(), element)));
      return true;
    }

    private ContentElement getCode(ContentElement element) {
      return (ContentElement) element.getFirstChild();
    }
  }

  static class CodeEventHandler extends LinoTextEventHandler {
    @Override
    public void onActivated(ContentElement element) {
      super.onActivated(element);

      // Add a listener to edit mode changes.
      // We use an existing one that does exactly what we want: updates the editability of
      // our element's container as a result.
      DisplayEditModeHandler.setEditModeListener(element, UpdateContentEditable.get());
    }
  }
}
