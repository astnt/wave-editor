package com.appspot.ast.client.layout;

import com.appspot.ast.client.editor.Toolbar;
import com.appspot.ast.client.editor.doodad.blockquote.BlockQuoteDoodad;
import com.appspot.ast.client.editor.doodad.blockquote.BlockQuoteWidget;
import com.appspot.ast.client.editor.doodad.paragraph.ParagraphWidget;
import com.appspot.ast.client.editor.doodad.phone.PhoneWidget;
import com.appspot.ast.client.editor.harness.GenericHarness;
import com.appspot.ast.client.editor.toolbar.ToolbarUpdateListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.waveprotocol.wave.client.editor.content.CMutableDocument;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.model.document.util.Point;
import org.waveprotocol.wave.model.document.util.Range;
import org.waveprotocol.wave.model.document.util.XmlStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:20
 */
public class WaveEditor extends Composite {
  private GenericHarness harness;
  private ToolbarUpdateListener toolbarUpdateListener;

  interface Binder extends UiBinder<Widget, WaveEditor> { Binder BINDER = GWT.create(Binder.class); }
  interface MyStyle extends CssResource {
    String item();
    String original();
    String imageContainer();
    String log();
    String tabs();
    String source();
    String wysiwyg();
    String sourceTab();
    String wysiwygTab();
    String editor();
    String stylePanel();
    String logTab();
    String videoDoodad();
  }

  @UiField MyStyle style;
  @UiField Toolbar toolbar;
  @UiField(provided = true) Widget richEditorLayout;
  @UiField HTML log;
  @UiField TextArea sourceAdopted;

  @UiField RadioButton wysiwygTab;
  @UiField RadioButton sourceTab;
  @UiField CheckBox logTab;
  @UiField DivElement richEditorContainer;

  @UiField PhoneWidget phoneDoodad;
  @UiField BlockQuoteWidget blockQuoteDoodad;
  @UiField ParagraphWidget paragraphDoodad;
  @UiField DivElement videoYoutube;

  public WaveEditor() {
    harness = new GenericHarness();
    richEditorLayout = harness.getRichEditorWidget();
    initWidget(Binder.BINDER.createAndBindUi(this));
    toolbar.createButtons(harness.getEditor(), harness.getRichEditor(), harness.getUpdater());
    harness.addUpdateListener(sourceAdopted);
    harness.enableLog(log);
    handleClickLogTab(null);

    initStylePanel();

    sinkEvents(Event.ONCLICK);
    harness.getEditor().addUpdateListener(toolbarUpdateListener = new ToolbarUpdateListener(harness.getEditor()));

    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable e) {
        Window.alert(e.getLocalizedMessage());
      }
    });
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
//    Window.alert("paragraph");
    Range range = toolbarUpdateListener.getSelectionRange();
    if (range == null) {
      Window.alert("Select place in text to insert paragraph.");
      return;
    }
    insertXml(range, "<p>paragraph</p>");
  }

  private void handleBlockQuoteClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
//    Window.alert("blockQuote range=" + range);
    if (range == null) {
      Window.alert("Select place in text to insert blockquote.");
      return;
    }
//    harness.getRichEditor().getContent().getNodeManager().
//    harness.getRichEditor().getContent().getContext().document().deleteRange()
//    harness.getRichEditor().getContent().getContext().document().getData()
//    harness.getRichEditor().getSelectionHelper().getOrderedSelectionPoints().
    final CMutableDocument document = harness.getEditor().getDocument();
    final Point<ContentNode> point = document.locate(range.getStart());
//    document.insertXml(point, XmlStringBuilder.
//        createFromXmlString("<blockqoute>blockqoute</blockqoute>"));
    insertXml(range, "<" + BlockQuoteDoodad.TAGNAME + ">blockquote</" + BlockQuoteDoodad.TAGNAME + ">");
  }

  private void handlePhoneDoodadClick() {
    Range range = toolbarUpdateListener.getSelectionRange();
//    Window.alert("phoneDoodad range=" + range);
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

  private void initStylePanel() {
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

  public void setText(String text) {
    if (!harness.setText(text)) {
      Window.alert("Исходный код неправильный! В логе подробности");
    }
  }

  @UiHandler("sourceTab")
  void handleClickSourceTab(ClickEvent event) {
    removeStyleName(style.wysiwyg());
    addStyleName(style.source());
    harness.outputEditorState();
    updateView();
  }

  @UiHandler("wysiwygTab")
  public void handleClickWysiwygTab(ClickEvent event) {
    removeStyleName(style.source());
    addStyleName(style.wysiwyg());
    setText("<doc><body>" + sourceAdopted.getText() + "</body></doc>");
    updateView();
  }

  @UiHandler("logTab")
  void handleClickLogTab(ClickEvent event) {
    updateView();
  }

  private void updateView() {
    richEditorContainer.getStyle().setDisplay(isDisplay(wysiwygTab.getValue()));
    sourceAdopted.getElement().getStyle().setDisplay(isDisplay(sourceTab.getValue()));
    log.getElement().getStyle().setDisplay(isDisplay(logTab.getValue()));
  }

  private Style.Display isDisplay(boolean isDisplay) {
    return isDisplay ? Style.Display.BLOCK : Style.Display.NONE;
  }

//  @UiHandler("fromSource")
  void handleClickFromSource(ClickEvent event) {
    setText(sourceAdopted.getText());
  }
}
