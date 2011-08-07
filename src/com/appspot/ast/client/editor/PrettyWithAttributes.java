/**
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.appspot.ast.client.editor;

import com.google.gwt.core.client.GWT;
import org.waveprotocol.wave.model.document.ReadableDocument;

import java.util.Map;

public class PrettyWithAttributes<N> {

  /**
   * A string builder for building up output
   */
  private StringBuilder builder = new StringBuilder();

  /**
   * Current indentation level
   */
  private int indent = 0;

  /**
   * Flag if selection is collapsed
   */
  private boolean collapsed = false;
  private boolean inline = false;

  /**
   * Constructor
   */
  public PrettyWithAttributes() {
  }

  /**
   * Set selection for future prints
   *
   * @return this
   */
  public PrettyWithAttributes<N> select() {
    collapsed = false;
    return this;
  }

  /**
   * Clear selection
   *
   * @return this
   */
  public PrettyWithAttributes<N> noselect() {
    return select();
  }

  /**
   * @param doc
   * @return pretty-print string describing doc + selection
   */
  public <E extends N, T extends N> String print(ReadableDocument<N, E, T> doc) {
    clear();
    appendDocument(doc);
    return builder.toString();
  }

  /**
   * @param doc
   * @param node
   * @return pretty-print string describing node doc + selection
   */
  public <E extends N, T extends N> String print(ReadableDocument<N, E, T> doc, N node) {
    clear();
    appendNode(doc, node);
    return builder.toString();
  }


  /**
   * Clears the builder
   */
  private void clear() {
    indent = 0;
    if (builder.length() > 0) {
      builder = new StringBuilder();
    }
  }

  /**
   * Appends a char sequence to builder
   *
   * @param sequence
   */
  private void append(CharSequence sequence) {
    builder.append(sequence);
  }

  /**
   * @param c
   */
  private void append(char c) {
    builder.append(c);
  }

  /**
   * Appends a newline and spaces to indent next line
   */
  private void appendNewLine() {
    append("\n");
    for (int i = 0; i < indent; ++i) {
      append("  ");
    }
  }

  /**
   * Appends a document to builder
   *
   * @param doc
   */
  private <E extends N, T extends N>
  void appendDocument(ReadableDocument<N, E, T> doc) {
    appendElement(doc, doc.getDocumentElement());
//    appendElement(doc, (E) doc.getFirstChild(doc.getDocumentElement()));
  }

  /**
   * @param doc
   * @param node
   * @return true if node prefers to be output inline
   */
  private <E extends N, T extends N> boolean isInline(
      ReadableDocument<N, E, T> doc, N node) {
    E element = doc.asElement(node);
    T text = doc.asText(node);
    boolean result = text != null || isInline(doc.getTagName(element).toLowerCase());
    if (element != null) {
//      GWT.log(("<" + doc.getTagName(element) + "/>") +" inline=" + inline);
      inline = result;
    }
    return result;
  }

  static boolean isInline(String tag) {
    return "|b|u|i|code|number|s|l:s|".contains("|" + tag + "|");
  }

  /**
   * Appends a node to builder
   *
   * @param doc
   * @param node
   */
  private <E extends N, T extends N> void appendNode(
      ReadableDocument<N, E, T> doc, N node) {
    E element = doc.asElement(node);
    if (element != null) {
      appendElement(doc, element);
      return;
    }
    T text = doc.asText(node);
    if (text != null) {
      appendText(doc, text);
      return;
    }
    assert (false);
  }

  /**
   * Appends element's tag name
   *
   * @param doc
   * @param element
   */
  private <E extends N, T extends N> void appendTagName(
      ReadableDocument<N, E, T> doc, E element) {
    append(doc.getTagName(element).toLowerCase()
        .replace("l:p", "line")
        .replace("l:s", EditorDocOpUtil.ANNOTATION)
    );
//    GWT.log("tagName=" + doc.getTagName(element).toLowerCase());
  }

  /**
   * Appends element's attributes
   *
   * @param doc
   * @param element
   */
  private <E extends N, T extends N> void appendAttributes(
      ReadableDocument<N, E, T> doc, E element) {
    try {
      Map<String, String> attributes = doc.getAttributes(element);
//      Map<String, String> attributes = CollectionUtils.newJavaMap(((ContentElement) element).getAttributes()); // HACK
      for (Map.Entry<String, String> attribute : attributes.entrySet()) {
//        append(" " + attribute.getKey() + "='" + attribute.getValue() + "'");
        append(" " + attribute.getKey() + "=\"" + attribute.getValue() + "\"");
      }
    } catch (Exception e) {
      GWT.log("append attributes error " + e.getMessage());
      // TODO(user): remove this when + if HtmlViewImpl implements getAttributes
      for (String name : new String[]{"class", "src", "id", "type", "name", "for", "href",
          "target", "fontWeight"}) {
        String value = doc.getAttribute(element, name);
        if (value != null && value.length() > 0) {
//          append(" " + name + "='" + value + "'");
          append(" " + name + "=\"" + value + "\"");
        }
      }
    }
  }

  /**
   * Appends an element start tag
   *
   * @param doc
   * @param element
   */
  private <E extends N, T extends N> void appendStartTag(
      ReadableDocument<N, E, T> doc, E element) {
    appendStartTag(doc, element, false);
  }

  /**
   * Appends a potentially self-closing element start tag
   *
   * @param doc
   * @param element
   * @param selfClosing
   */
  private <E extends N, T extends N> void appendStartTag(
      ReadableDocument<N, E, T> doc, E element, boolean selfClosing) {
//    GWT.log("tag=" + doc.getTagName(element));
    if (isShowTag(doc.getTagName(element))) {
      append("<");
      appendTagName(doc, element);
      appendAttributes(doc, element);
      append(selfClosing ? "/>" : ">");
    }
  }

  /**
   * Appends an element end tag
   *
   * @param doc
   * @param element
   */
  private <E extends N, T extends N> void appendEndTag(
      ReadableDocument<N, E, T> doc, E element) {
    if (isShowTag(doc.getTagName(element))) {
      append("</");
      appendTagName(doc, element);
      append(">");
    }
  }

  private boolean isShowTag(String tagName) {
    return !"|doc|body|".contains(tagName);
  }

  /**
   * Appends an element
   *
   * @param doc
   * @param element
   */
  private <E extends N, T extends N> void appendElement(
      ReadableDocument<N, E, T> doc, E element) {

    // First deal with childless elements
    N firstChild = doc.getFirstChild(element);
    if (firstChild == null) {
      appendStartTag(doc, element, true);
    } else {

      // Start tag
      appendStartTag(doc, element);

      // Children
      N child = firstChild;
      boolean first = true;
      boolean showTag = isShowTag(doc.getTagName(element));
      if (showTag) { ++indent; }
      while (child != null) {
        N next = doc.getNextSibling(child);
        if ((first && !isInline(doc, element)) || !isInline(doc, child)) {
          if (showTag) { appendNewLine(); }
        }
        appendNode(doc, child);
        first = false;
        child = next;
      }
      if (showTag) { --indent; }

      // End tag
      if ((!isInline(doc, element)) || !isInline(doc, firstChild)) {
        if (showTag) { appendNewLine(); }
      }
      appendEndTag(doc, element);
      if ("l:p".equals(doc.getTagName(element))) { appendNewLine(); }
    }
  }

  /**
   * Appends a text node
   *
   * @param doc
   * @param text
   */
  private <E extends N, T extends N> void appendText(
      ReadableDocument<N, E, T> doc, T text) {

    // The text value to append
//    String value = displayWhitespace(doc.getData(text));
    String value = doc.getData(text);

//    GWT.log("inline=" + inline + " text=" + value);
    // Append text and selection markers
    if (inline) {
      append(value);
    } else {
      append("<text>" + value + "</text>");
    }
  }

  /**
   * @param xml
   * @return XML-escaped string
   */
  public static String xmlEscape(String xml) {
    return xml
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
  }

  /**
   * Notice that this function only escape entity reference and not character reference.
   *
   * @param xml
   * @return the unescaped xml string.
   */
  public static String xmlUnescape(String xml) {
    return xml.replaceAll("&lt;", "<")
        .replaceAll("&gt;", ">")
        .replaceAll("&quot;", "\"")
        .replaceAll("&apos;", "'")
        .replaceAll("&amp;", "&");
  }

  /**
   * @param attrValue
   * @return The escaped xml attribute value
   */
  public static String attrEscape(String attrValue) {
    return xmlEscape(attrValue)
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&apos;");
  }

  /**
   * Debug method.
   *
   * @param string
   * @return The input string as an html string that correctly displays
   *         xml special characters, and spaces
   */
  public static String stringToHtml(String string) {
    return displayWhitespace(xmlEscape(string));
  }

  /**
   * Debug method.
   *
   * @param string
   * @return html string that displays white spaces;
   *         space -> a small square
   *         non-breaking space -> a small, solid square.
   *         TODO(user): other whitespace?
   */
  public static String displayWhitespace(String string) {
    return string.replaceAll("\u00A0", "\u25aa")
        .replaceAll(" ", "\u25ab");
  }
}


