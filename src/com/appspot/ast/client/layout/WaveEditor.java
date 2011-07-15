package com.appspot.ast.client.layout;

import com.appspot.ast.client.editor.Toolbar;
import com.appspot.ast.client.editor.doodad.phone.PhoneWidget;
import com.appspot.ast.client.editor.harness.GenericHarness;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:20
 */
public class WaveEditor extends Composite {
  private GenericHarness harness;

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

  public WaveEditor() {
    harness = new GenericHarness();
    richEditorLayout = harness.getRichEditorWidget();
    initWidget(Binder.BINDER.createAndBindUi(this));
    toolbar.createButtons(harness.getEditor(), harness.getRichEditor(), harness.getUpdater());
    harness.addUpdateListener(sourceAdopted);
    harness.enableLog(log);
    handleClickLogTab(null);

    initStylePanel();
  }

  private void initStylePanel() {
    final Element code = DOM.createElement("span");
    code.setInnerText("code");
    final Element number = DOM.createElement("span");
    number.setInnerText("number");
    phoneDoodad.getContainer().appendChild(code);
    phoneDoodad.getContainer().appendChild(number);
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
    updateView();
  }

  @UiHandler("wysiwygTab")
  public void handleClickWysiwygTab(ClickEvent event) {
    removeStyleName(style.source());
    addStyleName(style.wysiwyg());
    setText(sourceAdopted.getText());
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
