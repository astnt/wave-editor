package com.appspot.ast.client.editor.annotation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import org.waveprotocol.wave.client.common.util.DomHelper;
import org.waveprotocol.wave.client.editor.RenderingMutationHandler;
import org.waveprotocol.wave.client.editor.content.AnnotationPainter;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.PainterRegistry;
import org.waveprotocol.wave.client.editor.content.Registries;
import org.waveprotocol.wave.client.editor.content.Renderer;
import org.waveprotocol.wave.model.document.AnnotationBehaviour;
import org.waveprotocol.wave.model.document.AnnotationMutationHandler;
import org.waveprotocol.wave.model.document.util.AnnotationRegistry;
import org.waveprotocol.wave.model.document.util.DocumentContext;
import org.waveprotocol.wave.model.util.Box;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.StringMap;

import java.util.Collections;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:34
 */
public class PhoneAnnotationHandler implements AnnotationMutationHandler {
  private AnnotationPainter painter;
  public final static String PREFIX = "phone";
  public final static String KEY = "element/phone";

  public PhoneAnnotationHandler(AnnotationPainter painter) {
    this.painter = painter;
  }

  public interface PhoneAttributeAugmenter {

    Map<String,String> augment(Map<String,Object> from, boolean editing, Map<String,String> ret);
  }

  public static void register(Registries registries, PhoneAttributeAugmenter augmenter) {
    PainterRegistry painterRegistry = registries.getPaintRegistry();
    PhoneAnnotationHandler handler = new PhoneAnnotationHandler(painterRegistry.getPainter());

    AnnotationRegistry annotationRegistry = registries.getAnnotationHandlerRegistry();
    annotationRegistry.registerHandler(PREFIX, handler);

    registerBehaviour(annotationRegistry, KEY);

    painterRegistry.registerPaintFunction(CollectionUtils.newStringSet(KEY), new RenderFunc(augmenter));

    registries.getElementHandlerRegistry().registerRenderingMutationHandler("phone", new SimpleRenderer());
  }

  private static void registerBehaviour(AnnotationRegistry registry, String prefix) {
    registry.registerBehaviour(prefix,
        new AnnotationBehaviour.DefaultAnnotationBehaviour(AnnotationBehaviour.AnnotationFamily.CONTENT) {
      @Override
      public BiasDirection getBias(final StringMap<Object> left, final StringMap<Object> right,
          CursorDirection cursor) {
        final Box<BiasDirection> ret = Box.create(BiasDirection.NEITHER);
        if (left.get(KEY) != null) {
          ret.boxed = BiasDirection.RIGHT;
        } else if (right.get(KEY) != null) {
          ret.boxed = BiasDirection.LEFT;
        }
        return ret.boxed;
      }
      @Override
      public double getPriority() {
        return 10.0; // higher than elements.
      }
    });
  }

  private static class RenderFunc implements AnnotationPainter.PaintFunction {
    private final PhoneAttributeAugmenter augmenter;

    public RenderFunc(PhoneAttributeAugmenter augmenter) {
      this.augmenter = augmenter;
    }

    public Map<String, String> apply(Map<String, Object> from, boolean isEditing) {
      Map<String, String> ret;
      String content = (String) from.get(KEY);
      if (content != null) {
        ret = Collections.singletonMap(PREFIX, content);
      } else {
        ret = Collections.emptyMap();
      }

      return augmenter.augment(from, isEditing, ret);
    }
  }

  @Override
  public <N, E extends N, T extends N> void handleAnnotationChange(DocumentContext<N, E, T> bundle,
      int start, int end, String key, Object newValue) {
    painter.scheduleRepaint(bundle, start, end);
  }

  /**
   * A trivial renderer that keeps the image's src attribute up-to-date with the
   * model's ref attribute.
   */
  static class SimpleRenderer extends RenderingMutationHandler {

    @Override
    public Element createDomImpl(Renderer.Renderable element) {
      Element imgTag = Document.get().createImageElement();
      DomHelper.setContentEditable(imgTag, false, false);
      return imgTag;
    }

    @Override
    public void onActivatedSubtree(ContentElement element) {
      fanoutAttrs(element);
    }

    @Override
    public void onAttributeModified(
        ContentElement element, String name, String oldValue, String newValue) {
      if ("phone".equals(name)) {
        element.getImplNodelet().setAttribute("class", newValue);
      }
    }
  }
}
