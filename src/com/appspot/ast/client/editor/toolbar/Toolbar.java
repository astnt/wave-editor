package com.appspot.ast.client.editor.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.editor.Editor;
import org.waveprotocol.wave.client.editor.EditorContextAdapter;
import org.waveprotocol.wave.client.editor.toolbar.ButtonUpdater;
import org.waveprotocol.wave.client.widget.toolbar.ToplevelToolbarWidget;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 17.06.11
 * Time: 8:40
 */
public class Toolbar extends Composite {
  interface Binder extends UiBinder<Widget, Toolbar> { Binder BINDER = GWT.create(Binder.class); }
  @UiField ToplevelToolbarWidget ui;

  public Toolbar() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }

  public void createButtons(EditorContextAdapter editorContextAdapter, Editor editor,
                            ButtonUpdater updater, List<ToolbarBuilder> builders) {
    for (ToolbarBuilder builder : builders) {
      builder.create(ui, updater, editorContextAdapter);
    }

    editor.addUpdateListener(updater);
    editorContextAdapter.switchEditor(editor);
    updater.updateButtonStates();
  }
}

