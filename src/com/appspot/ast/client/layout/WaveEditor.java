package com.appspot.ast.client.layout;

import com.appspot.ast.client.editor.harness.GenericHarness;
import com.appspot.ast.client.editor.toolbar.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:20
 */
public class WaveEditor extends Composite implements HasValue<String> {
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

  @UiFactory
  public StylesToolbar makeStylesToolbar() {
    return new StylesToolbar(harness, toolbarUpdateListener);
  }

  public WaveEditor() {
    harness = new GenericHarness();
    richEditorLayout = harness.getRichEditorWidget();
    initWidget(Binder.BINDER.createAndBindUi(this));
    toolbar.createButtons(harness.getEditor(), harness.getRichEditor(), harness.getUpdater(),
        Arrays.asList((ToolbarBuilder) new WaveToolbarBuilder()));
    harness.addUpdateListener(sourceAdopted);
    harness.enableLog(log);
    handleClickLogTab(null);

    sinkEvents(Event.ONCLICK);
    harness.getEditor().addUpdateListener(toolbarUpdateListener = new ToolbarUpdateListener(harness.getEditor()));

    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable e) {
        Window.alert(e.getLocalizedMessage());
      }
    });
  }

  public void setValue(String value) {
    if (!harness.setText("<doc><body>" + value + "</body></doc>")) {
      Window.alert("Исходный код неправильный! В логе подробности");
    }
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    setValue(value);
  }

  public String getValue() {
    return harness.getText();
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    setValue(sourceAdopted.getText());
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
}
