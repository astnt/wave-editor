package com.appspot.ast.client.layout;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 12:49
 */
public class WaveEditorPoint implements EntryPoint {
  public void onModuleLoad() {
    final WaveEditor editor = new WaveEditor();
    editor.setText("<doc><body><line>\n" +
        "  <p>\n" +
        "    <text>Paragraph sample.</text>\n" +
        "  </p>\n" +
        "  <blockquote>\n" +
        "    <text>Phone:</text>\n" +
        "    <p>\n" +
        "      <text>Sample text for paragraph</text>\n" +
        "    </p>\n" +
        "    <p>\n" +
        "      <text>another para in blockquote</text>\n" +
        "    </p>\n" +
        "  </blockquote><text>Phone: </text>\n" +
        "  <phone>\n" +
        "    <code>465</code><number>746-45-16</number>\n" +
        "  </phone><text> text with </text><s fontWeight=\"bold\">bold and </s><s fontWeight=\"bold\" fontStyle=\"italic\">italic</s>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <text>text</text>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <p>\n" +
        "    <text>First para</text>\n" +
        "  </p>\n" +
        "  <p>\n" +
        "    <text>Second par</text>\n" +
        "  </p>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <text>Предложение первое.</text>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <text>Предложение еще одно.</text>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <image attachment=\"/pics/hills.jpg\">\n" +
        "    <caption>\n" +
        "      <text>Подпись к картинке</text>\n" +
        "    </caption>\n" +
        "  </image>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <text>Еще дополнительный текст</text>\n" +
        "</line></body></doc>");
    RootPanel.get().add(editor);
  }
}
