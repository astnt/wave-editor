package com.appspot.ast.client.editor;

import com.google.gwt.core.client.GWT;
import org.waveprotocol.wave.client.doodad.link.Link;
import org.waveprotocol.wave.client.editor.content.misc.StyleAnnotationHandler;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMap;
import org.waveprotocol.wave.model.document.operation.AnnotationBoundaryMapBuilder;
import org.waveprotocol.wave.model.document.operation.Attributes;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocInitializationCursor;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.impl.AttributesImpl;
import org.waveprotocol.wave.model.document.operation.impl.DocOpBuilder;
import org.waveprotocol.wave.model.document.operation.impl.DocOpUtil;
import org.waveprotocol.wave.model.document.parser.AnnotationParser;
import org.waveprotocol.wave.model.document.parser.XmlParseException;
import org.waveprotocol.wave.model.document.parser.XmlPullParser;
import org.waveprotocol.wave.model.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 22.06.11
 * Time: 13:29
 */
public class EditorDocOpUtil {
  private static final String PI_TARGET = "a";
  static final String ANNOTATION = "s";
  private static String prev = "";
  private final static String SKIP_TAGS = "|doc|text|";

  public static DocInitialization docInitializationFromXml(String text, XmlPullParser p) throws XmlParseException {
    Stack<String> tags = new Stack<String>();
    HashMap<String, List<String>> currentAnnotations = new HashMap<String, List<String>>();
    DocOpBuilder builder = new DocOpBuilder();
    boolean inText = false;
    while (p.hasNext()) {
      switch (p.next()) {
        case START_ELEMENT:
          inText = isInline(p);
          if (isContains(SKIP_TAGS, p.getTagName())) { continue; } // skip the <doc/> element
          tags.push(p.getTagName());
          final String key = getTagKey(tags);
          GWT.log("key=" + key + "; prev=" + prev + "; not eq=" + !key.equals(prev));
          if (!key.equals(prev)) {
            // значит произошло изменение
            closeByPrev(currentAnnotations, builder);
            if (currentAnnotations.containsKey(key) && currentAnnotations.get(key) != null) {
              GWT.log("clear stored for key=" + key);
              currentAnnotations.get(key).clear();
            }
          }
          GWT.log("write key=" + key);
          prev = key;

          if (isInstruction(p.getTagName())) {
            processingInstructionTag(p, builder, tags, currentAnnotations);
            continue;
          }
          builder.elementStart(p.getTagName(), AttributesImpl.fromStringMap(p.getAttributes()));
          GWT.log("start tag name=" + p.getTagName()); // body line
          if ("line".equals(p.getTagName())) {
            builder.elementEnd();
            GWT.log("force line close=" + p.getTagName()); //
            pop(tags);
          }
          continue;
        case END_ELEMENT:
          if (isInline(p)) { inText = false; }
          if (isContains("|" + ANNOTATION + "|", p.getTagName())) {
            pop(tags);
            continue;
          }
          if (isInstruction(p.getTagName())) { continue; }
          if (isContains(SKIP_TAGS, p.getTagName())) { continue; } // пропускаем полностью
          if (isContains("|line|", p.getTagName())) {
//            pop(tags, currentAnnotations);
            // фактически билдеру это не видно, для него <line> закрыта, но в случае когда конец документа, нужно закрыть
            // аннотации
            closeByPrev(currentAnnotations, builder);
            continue;
          }
          builder.elementEnd();
          GWT.log("end tag name=" + p.getTagName()); //
          pop(tags);
          continue;
        case TEXT:
//          final String chars = p.getText().trim();
          if (inText) {
            final String chars = p.getText().replaceAll("[\\r\\n\\t]", " ");
            if (chars.length() > 0) {
              builder.characters(chars);
              GWT.log(inText + " text={" + chars + "} prev=" + prev); //
            }
          }
          continue;
        case PROCESSING_INSTRUCTION:
          processingInstruction(p, builder);
      }
    }
    DocOp op = builder.build();
    return DocOpUtil.asInitialization(op);
  }

  private static boolean isInline(XmlPullParser p) {
    return "text".equals(p.getTagName()) || PrettyWithAttributes.isInline(p.getTagName());
  }

  private static void closeByPrev(HashMap<String, List<String>> currentAnnotations, DocOpBuilder builder) {
    final AnnotationBoundaryMapBuilder anBuilder = new AnnotationBoundaryMapBuilder();
    if (closeIfNeed(anBuilder, currentAnnotations, null, prev)) { // закрываем по предыдущему
      builder.annotationBoundary(anBuilder.build());
    }
  }

  private static void pop(Stack<String> tags) {
    if (!tags.isEmpty()) {
      tags.pop();
    }
  }

  private static void processingInstructionTag(XmlPullParser p, DocOpBuilder builder, Stack<String> tags, HashMap<String, List<String>> currentAnnotations) throws XmlParseException {
    GWT.log("processingInstructionTag()");
//    String name = p.getProcessingInstructionName();
//    GWT.log("processing instruction name=" + name); // a

    String tagKey = getTagKey(tags);
    AnnotationBoundaryMapBuilder anBuilder = new AnnotationBoundaryMapBuilder();
    HashMap<String, String> changedAnnotations = new HashMap<String, String>();
    for (Map.Entry<String, String> entry : AttributesImpl.fromStringMap(p.getAttributes()).entrySet()) {
      final String key;
      if ("link".equals(entry.getKey())) {
        key = Link.MANUAL_KEY;
      } else {
        key = StyleAnnotationHandler.key(entry.getKey());
      }

      final String oldValue = null;
      final String newValue = entry.getValue();
      // key - аннотация; value - значение

      if (newValue == null) {
        anBuilder.end(key);
      } else {
        if (!currentAnnotations.containsKey(tagKey)) {
          currentAnnotations.put(tagKey, new ArrayList<String>());
        }
        GWT.log("contains tagKey=" + tagKey + "; key=" + key + "; value=" + currentAnnotations.get(tagKey).contains(key));

        changedAnnotations.put(key, newValue); // в любом случае вносим, так как не закрыто

        if (currentAnnotations.get(tagKey).contains(key)) {
          GWT.log("annotation skip=" + key);
          continue;
        }

        GWT.log("annotation change=" + key + "; newValue=" + newValue);
        anBuilder.change(key, oldValue, newValue);

        GWT.log("annotation put=" + tagKey + "; newValue=" + key);
        currentAnnotations.get(tagKey).add(key);
      }
    }

    closeIfNeed(anBuilder, currentAnnotations, changedAnnotations, tagKey);

    builder.annotationBoundary(anBuilder.build());
  }

  private static boolean closeIfNeed(AnnotationBoundaryMapBuilder anBuilder,
                                  HashMap<String, List<String>> currentAnnotations, HashMap<String, String> change, String tagKey) {
    boolean changed = false;
    if (currentAnnotations.containsKey(tagKey) && currentAnnotations.get(tagKey) != null) {
      List<String> toRemove = new ArrayList<String>();
      for (String annotation : currentAnnotations.get(tagKey)) {
        GWT.log("annotation check=" + annotation);
        if (change != null && !change.containsKey(annotation)) { // если не содержит аннотацию, то закрываем
          anBuilder.end(annotation);
          GWT.log("annotation end=" + annotation);
          toRemove.add(annotation);
          changed = true;
        } else if (change == null) {
          anBuilder.end(annotation);
          GWT.log("final annotation end=" + annotation);
          toRemove.add(annotation);
          changed = true;
        }
      }
      currentAnnotations.get(tagKey).removeAll(toRemove);
    }
    return changed;
  }

  private static String getTagKey(Stack<String> tags) {
    String tagKey = "";
    for (String tag : tags) {
      tagKey += "/" + tag;
    }
    return tagKey;
  }

  private static void processingInstruction(XmlPullParser p, DocOpBuilder builder) throws XmlParseException {
    GWT.log("processingInstruction()");
    String name = p.getProcessingInstructionName();
    GWT.log("processing instruction name=" + name); // a
    AnnotationBoundaryMapBuilder anBuilder = new AnnotationBoundaryMapBuilder();
    if (isInstruction(name)) {
      final String processingInstructionValue = p.getProcessingInstructionValue();
      GWT.log("p.getProcessingInstructionValue()=" + processingInstructionValue);
      List<Pair<String, String>> parseAnnotations =
          AnnotationParser.parseAnnotations(processingInstructionValue);
      for (Pair<String, String> ann : parseAnnotations) {
        GWT.log("key=" + ann.first + "; newValue=" + ann.second);
        final String key = ann.first;
        final String oldValue = null;
        final String newValue = ann.second;
        if (newValue == null) {
          anBuilder.end(key);
        } else {
          anBuilder.change(key, oldValue, newValue);
        }
      }
      builder.annotationBoundary(anBuilder.build());
    }
  }

  private static boolean isInstruction(String tag) {
    return isContains("|" + ANNOTATION + "|a|", tag);
  }

  /**
   * @deprecated
   * @param op
   * @param indent
   * @param b
   */
  public static void buildXmlString(DocInitialization op, final int indent,
                                    final StringBuilder b) {

    try {
      op.apply(new DocInitializationCursor() {
        Map<String, String> currentAnnotations = new HashMap<String, String>();
        TreeMap<String, String> changes = new TreeMap<String, String>();
        ArrayList<String> tags = new ArrayList<String>();

        String elementPart;
        public int indent = 0;

        @Override
        public void annotationBoundary(AnnotationBoundaryMap map) {

          changes.clear();
          for (int i = 0; i < map.changeSize(); i++) {
            String key = map.getChangeKey(i);
            String value = map.getNewValue(i);
            if (!equal(currentAnnotations.get(key), value)) {
              // removal not necessary if null, get will return the same in either case.
              currentAnnotations.put(key, value);
              changes.put(key, value);
            }
          }
          for (int i = 0; i < map.endSize(); i++) {
            String key = map.getEndKey(i);
            if (currentAnnotations.get(key) != null) {
              currentAnnotations.remove(key);
//              b.append(addIndent(true) + "</a>" + newLine());
              this.indent -= 1;
              b.append(addIndent() + "</a>" + newLine());
//              changes.put(key, null);
            }
          }

          if (changes.isEmpty()) {
            return;
          }

          if (elementPart != null) {
            b.append(elementPart + ">" + newLine());
            elementPart = null;
          }
          b.append(addIndent(true) + "<" + PI_TARGET);
          for (Map.Entry<String, String> entry : changes.entrySet()) {
            if (entry.getValue() != null) {
              b.append(" \"" + DocOpUtil.xmlTextEscape(DocOpUtil.annotationEscape(entry.getKey())) + "\"");
              b.append("=");
              b.append("\"" + DocOpUtil.xmlTextEscape(DocOpUtil.annotationEscape(entry.getValue())) + "\"");
            } else {
              // This code renders ending annotations and annotations that are
              // changed to null the same way, which is OK since we are
              // only concerned with DocIntializations.  (It's, in fact, the
              // only correct solution since our test cases use this code for
              // equality comparison of documents.)
              b.append(" \"" + DocOpUtil.xmlTextEscape(DocOpUtil.annotationEscape(entry.getKey())) + "\"");
            }
          }
          b.append(">" + newLine());
        }

        @Override
        public void characters(String chars) {
          boolean add = false;
          if (elementPart != null) {
            b.append(elementPart + ">" + newLine());
            elementPart = null;
            add = true;
          }
          b.append(addIndent(add) + DocOpUtil.xmlTextEscape(chars) + newLine());
        }

        @Override
        public void elementStart(String type, Attributes attrs) {
          if (elementPart != null) {
            b.append(elementPart + ">" + newLine() + addIndent(true));
            elementPart = null;
          }
          elementPart = addIndent() + "<" + type + (attrs.isEmpty() ? "" : " " + DocOpUtil.attributeString(attrs));
          tags.add(type);
        }

        @Override
        public void elementEnd() {
          if (elementPart != null) {
            b.append(elementPart + "/>" + newLine());
            elementPart = null;
            assert tags.size() > 0;
            tags.remove(tags.size() - 1);
          } else {
            String tag;
            tag = tags.remove(tags.size() - 1);
            b.append("</" + tag + ">" + newLine(true));
          }
        }

        private boolean equal(String a, String b) {
          return a == null ? b == null : a.equals(b);
        }

        private String addIndent() {
          return addIndent(false);
        }
        private String addIndent(boolean incIndent) {
          String result = "";
          for (int i = 0; i < this.indent; i++) {
            result += "  ";
          }
          if (incIndent) this.indent += 1;
          return result;
        }

        private String newLine() {
          return newLine(false);
        }
        private String newLine(boolean decIndent) {
          if (decIndent) this.indent -= 1;
          GWT.log("indent=" + this.indent);
          return "\n";
        }

      });
    } catch (RuntimeException e) {
      throw new RuntimeException("toXmlString: DocInitialization was probably ill-formed", e);
    }
  }

  static boolean isContains(String tags, String tag) {
    return tags.contains("|" + tag + "|");
  }
}
