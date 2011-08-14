package com.appspot.ast.client.editor.toolbar;

import org.waveprotocol.wave.client.editor.EditorContextAdapter;
import org.waveprotocol.wave.client.editor.toolbar.ButtonUpdater;
import org.waveprotocol.wave.client.widget.toolbar.ToplevelToolbarWidget;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/13/11
 * Time: 8:16 PM
 */
public interface ToolbarBuilder {
  void create(ToplevelToolbarWidget toolbarUi, ButtonUpdater updater, EditorContextAdapter editorContextAdapter);
}
