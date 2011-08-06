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
    editor.setText("<doc>\n" +
        "  <body>\n" +
        "    <line>\n" +
        "      <blockquote>Phone:</blockquote>\n" +
        "      <text>Phone: </text>\n" +
        "      <phone>\n" +
        "        <code>465</code><number>746-45-16</number>\n" +
        "      </phone><text> text with </text><s fontWeight=\"bold\">bold and </s><s fontWeight=\"bold\" fontStyle=\"italic\">italic</s>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <text>text</text>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <mydoodad ref=\"/pics/hills.jpg\"/>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <text>Предложение первое.</text>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <text>Предложение еще одно.</text>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <image attachment=\"/pics/hills.jpg\">\n" +
        "        <caption>\n" +
        "          <text>Подпись к картинке</text>\n" +
        "        </caption>\n" +
        "      </image>\n" +
        "    </line>\n" +
        "    <line>\n" +
        "      <text>Еще дополнительный текст</text>\n" +
        "    </line>\n" +
        "  </body>\n" +
        "</doc>");
    RootPanel.get().add(editor);
  }
}
