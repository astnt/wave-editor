package com.appspot.ast.client.editor.doodad.blockquote;

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
 * Date: 8/6/11
 * Time: 1:48 PM
 */
public class BlockQuoteDoodad {
  public static String TAGNAME = "blockquote";

  public static void register(ElementHandlerRegistry registry) {
    BlockQuoteRenderer renderer = new BlockQuoteRenderer();

    registry.registerRenderingMutationHandler(TAGNAME, renderer);

    registry.registerEventHandler(TAGNAME, new BlockQuoteEventHanler(renderer));

    registry.registerEventHandler(TAGNAME, new CodeEventHandler());
  }

  static class BlockQuoteRenderer extends GwtRenderingMutationHandler {
    BlockQuoteRenderer() {
      super(Flow.BLOCK);
    }

    @Override
    protected Widget createGwtWidget(Renderable renderable) {
      return new BlockQuoteWidget();
    }

    @Override
    protected Element getContainerNodelet(Widget w) {
      return ((BlockQuoteWidget) w).getContainer();
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

    /** Convenience getter */
    BlockQuoteWidget getWidget(ContentElement e) {
      return ((BlockQuoteWidget) getGwtWidget(e));
    }
  }

  static class BlockQuoteEventHanler extends ChunkyElementHandler {
    private final BlockQuoteRenderer renderer;

    public BlockQuoteEventHanler(BlockQuoteRenderer renderer) {
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
