package com.appspot.ast.client.editor.toolbar;

import org.waveprotocol.wave.client.editor.EditorContext;
import org.waveprotocol.wave.client.editor.EditorUpdateEvent;
import org.waveprotocol.wave.model.document.util.Range;

/**
 * Created by IntelliJ IDEA.
 * User: ast
 * Date: 8/6/11
 * Time: 1:15 PM
 */
public class ToolbarUpdateListener implements EditorUpdateEvent.EditorUpdateListener {
  private EditorContext editor;
  private Range selectionRange;

  public ToolbarUpdateListener(EditorContext editor) {
    this.editor = editor;
  }

  @Override
  public void onUpdate(EditorUpdateEvent editorUpdateEvent) {
    selectionRange = editor.getSelectionHelper().getOrderedSelectionRange();
  }

  public Range getSelectionRange() {
    return selectionRange;
  }
}
