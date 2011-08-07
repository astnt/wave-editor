package com.appspot.ast.client.editor.doodad.video;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.editor.ElementHandlerRegistry;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.client.editor.content.misc.ChunkyElementHandler;
import org.waveprotocol.wave.client.editor.content.misc.DisplayEditModeHandler;
import org.waveprotocol.wave.client.editor.content.misc.LinoTextEventHandler;
import org.waveprotocol.wave.client.editor.content.misc.UpdateContentEditable;
import org.waveprotocol.wave.client.editor.event.EditorEvent;
import org.waveprotocol.wave.client.editor.gwt.GwtRenderingMutationHandler;
import org.waveprotocol.wave.model.document.util.Point;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/7/11
 * Time: 7:36 PM
 */
public class VideoDoodad {
  public static String TAGNAME = "video";
  public static String REF_ATTR = "src";

  public static void register(ElementHandlerRegistry registry) {
    VideoRenderer renderer = new VideoRenderer();

    registry.registerRenderingMutationHandler(TAGNAME, renderer);

    registry.registerEventHandler(TAGNAME, new VideoEventHandler(renderer));

    registry.registerEventHandler(TAGNAME, new CodeEventHandler());
  }

  static class VideoRenderer extends GwtRenderingMutationHandler {
    VideoRenderer() {
      super(Flow.INLINE);
    }

    @Override
    protected Widget createGwtWidget(Renderable renderable) {
      return new VideoWidget();
    }

    @Override
    protected Element getContainerNodelet(Widget w) {
      return ((VideoWidget) w).getContainer();
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

      if (REF_ATTR.equals(name)) {
        getWidget(element).setVideoSrc(newValue);
      }
    }

    /** Convenience getter */
    VideoWidget getWidget(ContentElement e) {
      return ((VideoWidget) getGwtWidget(e));
    }
  }

  static class VideoEventHandler extends ChunkyElementHandler {
    private final VideoRenderer renderer;

    public VideoEventHandler(VideoRenderer renderer) {
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