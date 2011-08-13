package com.appspot.ast.client.editor.toolbar;

import com.appspot.ast.client.editor.doodad.blockquote.BlockQuoteDoodad;
import com.appspot.ast.client.editor.doodad.blockquote.BlockQuoteWidget;
import com.appspot.ast.client.editor.doodad.paragraph.ParagraphWidget;
import com.appspot.ast.client.editor.doodad.phone.PhoneWidget;
import com.appspot.ast.client.editor.harness.GenericHarness;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.editor.content.CMutableDocument;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.model.document.util.Point;
import org.waveprotocol.wave.model.document.util.Range;
import org.waveprotocol.wave.model.document.util.XmlStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/13/11
 * Time: 1:19 PM
 */
public class StylesToolbar extends Composite {
  interface Binder extends UiBinder<Widget, StylesToolbar> { Binder BINDER = GWT.create(Binder.class); }

  private GenericHarness harness;
  private ToolbarUpdateListener toolbarUpdateListener;

  @UiField PhoneWidget phoneDoodad;
  @UiField BlockQuoteWidget blockQuoteDoodad;
  @UiField ParagraphWidget paragraphDoodad;
  @UiField DivElement videoYoutube;

  public StylesToolbar(GenericHarness harness, ToolbarUpdateListener toolbarUpdateListener) {
    this.harness = harness;
    this.toolbarUpdateListener = toolbarUpdateListener;

    initWidget(Binder.BINDER.createAndBindUi(this));

    final Element code = DOM.createElement("span");
    code.setInnerText("code");
    final Element number = DOM.createElement("span");
    number.setInnerText("number");
    phoneDoodad.getContainer().appendChild(code);
    phoneDoodad.getContainer().appendChild(number);
    phoneDoodad.getElement().getStyle().setDisplay(Style.Display.BLOCK);

    blockQuoteDoodad.getContainer().setInnerText("blockquote");
    blockQuoteDoodad.getElement().getStyle().setDisplay(Style.Display.BLOCK);

    paragraphDoodad.getContainer().setInnerText("paragraph");
    paragraphDoodad.getElement().getStyle().setDisplay(Style.Display.BLOCK);
  }

  @Override
  public void onBrowserEvent(Event event) {
    Element target = event.getEventTarget().cast();
    if (target.equals(phoneDoodad.getElement())
        || target.getParentElement().equals(phoneDoodad.getElement())
        || target.getParentElement().getParentElement().equals(phoneDoodad.getElement())
        ) {
      handlePhoneDoodadClick();
    } else if (target.equals(blockQuoteDoodad.getElement()) || target.getParentElement().equals(blockQuoteDoodad.getElement())) {
      handleBlockQuoteClick();
    } else if (target.equals(paragraphDoodad.getElement())) {
      handleParagraphClick();
    } else if (target.equals(videoYoutube.<JavaScriptObject>cast())) {
      handleVideoClick();
    }
  }

  private void handleVideoClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
    if (range == null) {
      Window.alert("Select place in text to insert video.");
      return;
    }
    String youtubeSrc = Window.prompt("Youtube src (embedded links only)", "http://www.youtube.com/embed/FplWxtPzWY8");
    if (youtubeSrc != null) {
      insertXml(range, "<video src=\"" + youtubeSrc + "\"/>");
    }
  }

  private void handleParagraphClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
    if (range == null) {
      Window.alert("Select place in text to insert paragraph.");
      return;
    }
    insertXml(range, "<p>paragraph</p>");
  }

  private void handleBlockQuoteClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
    if (range == null) {
      Window.alert("Select place in text to insert blockquote.");
      return;
    }
    final CMutableDocument document = harness.getEditor().getDocument();
    final Point<ContentNode> point = document.locate(range.getStart());
    insertXml(range, "<" + BlockQuoteDoodad.TAGNAME + ">blockquote</" + BlockQuoteDoodad.TAGNAME + ">");
  }

  private void handlePhoneDoodadClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
    if (range == null) {
      Window.alert("Select place in text to insert phone.");
      return;
    }
    final CMutableDocument document = harness.getEditor().getDocument();
    final Point<ContentNode> point = document.locate(range.getStart());
    insertXml(range, "<phone><code>code</code><number>number</number></phone>");
  }

  private void insertXml(Range range, String xmlContent) {
    final CMutableDocument document = harness.getEditor().getDocument();
    final Point<ContentNode> point = document.locate(range.getStart());
    document.insertXml(point, XmlStringBuilder.
        createFromXmlString(xmlContent));
    harness.getEditor().focus(false);
  }
}
