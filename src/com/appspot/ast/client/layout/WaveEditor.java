package com.appspot.ast.client.layout;

import com.appspot.ast.client.editor.Toolbar;
import com.appspot.ast.client.editor.harness.GenericHarness;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
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
    String sourceContainer();
    String source();
    String item();
    String original();
    String imageContainer();
    String log();
    String tabs();
  }

  @UiField MyStyle style;
  @UiField Toolbar toolbar;
  @UiField(provided = true) Widget richEditorLayout;
  @UiField CheckBox sourceTab;
  @UiField CheckBox logTab;
  @UiField HTML log;
  @UiField Button fromSource;
  @UiField TextArea sourceAdopted;
  @UiField DivElement sourceContainer;


  public WaveEditor() {
    harness = new GenericHarness();
    richEditorLayout = harness.getRichEditorWidget();
    initWidget(Binder.BINDER.createAndBindUi(this));
    toolbar.createButtons(harness.getEditor(), harness.getRichEditor(), harness.getUpdater());
    harness.addUpdateListener(sourceAdopted);
    harness.enableLog(log);
    handleClickLogTab(null);
  }

  public void setText(String text) {
    if (!harness.setText(text)) {
      Window.alert("Исходный код неправильный! В логе подробности");
    }
  }

  @UiHandler("sourceTab")
  void handleClickSourceTab(ClickEvent event) {
    sourceContainer.getStyle().setDisplay(sourceTab.getValue() ? Style.Display.BLOCK : Style.Display.NONE);
  }

  @UiHandler("logTab")
  void handleClickLogTab(ClickEvent event) {
    log.getElement().getStyle().setDisplay(logTab.getValue() ? Style.Display.BLOCK : Style.Display.NONE);
  }

  @UiHandler("fromSource")
  void handleClickFromSource(ClickEvent event) {
    setText(sourceAdopted.getText());
  }
}
