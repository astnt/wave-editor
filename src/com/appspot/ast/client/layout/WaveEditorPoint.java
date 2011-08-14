package com.appspot.ast.client.layout;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
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
    editor.setValue("<line>\n" +
        "  <text>text </text>\n" +
        "  <phone>\n" +
        "    <code>465</code><number>746-45-16</number>\n" +
        "  </phone><text> text with </text><s fontWeight=\"bold\">bold and </s><s fontWeight=\"bold\" fontStyle=\"italic\">italic</s>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <text>text</text>\n" +
        "</line>\n" +
        "<line>\n" +
        "  <video src=\"http://www.youtube.com/embed/5ZxRuxSZfzw\"/>\n" +
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
        "  <p>\n" +
        "    <text>Paragraph sample.</text>\n" +
        "  </p>\n" +
        "  <blockquote>\n" +
        "    <text>Цитата:</text>\n" +
        "    <p>\n" +
        "      <text>В столкновениях с правительственными войсками в Сирии 7 августа погибли не менее 57 человек. Большая часть погибших - жители мятежного города Дейр-эз-Зор на востоке страны. В ходе штурма города с применением танков здесь были убиты 38 человек. Остальные стали жертвами столкновений в других городах.</text>\n" +
        "    </p>\n" +
        "    <p>\n" +
        "      <text>Россия не планирует пересматривать объем вложений резервного фонда в долларах в связи с понижением кредитного рейтинга США агентством. Об этом заявили замминистра финансов Сергей Сторчак и зампред ЦБ Сергей Швецов.</text>\n" +
        "    </p>\n" +
        "  </blockquote><text>Phone: </text>\n" +
        "</line>");
    RootPanel.get().add(editor);
    Button button = new Button("Get text");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.alert(editor.getValue());
      }
    });
    RootPanel.get().add(button);
  }
}
