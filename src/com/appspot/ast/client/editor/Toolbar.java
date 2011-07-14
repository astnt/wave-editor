package com.appspot.ast.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.common.util.WaveRefConstants;
import org.waveprotocol.wave.client.doodad.link.Link;
import org.waveprotocol.wave.client.editor.Editor;
import org.waveprotocol.wave.client.editor.EditorContextAdapter;
import org.waveprotocol.wave.client.editor.content.CMutableDocument;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.client.editor.content.misc.StyleAnnotationHandler;
import org.waveprotocol.wave.client.editor.content.paragraph.Paragraph;
import org.waveprotocol.wave.client.editor.toolbar.ButtonUpdater;
import org.waveprotocol.wave.client.editor.toolbar.ParagraphApplicationController;
import org.waveprotocol.wave.client.editor.toolbar.ParagraphTraversalController;
import org.waveprotocol.wave.client.editor.toolbar.TextSelectionController;
import org.waveprotocol.wave.client.editor.util.EditorAnnotationUtil;
import org.waveprotocol.wave.client.widget.toolbar.SubmenuToolbarView;
import org.waveprotocol.wave.client.widget.toolbar.ToolbarButtonViewBuilder;
import org.waveprotocol.wave.client.widget.toolbar.ToolbarView;
import org.waveprotocol.wave.client.widget.toolbar.ToplevelToolbarWidget;
import org.waveprotocol.wave.client.widget.toolbar.buttons.ToolbarClickButton;
import org.waveprotocol.wave.client.widget.toolbar.buttons.ToolbarToggleButton;
import org.waveprotocol.wave.model.document.util.FocusedRange;
import org.waveprotocol.wave.model.document.util.Point;
import org.waveprotocol.wave.model.document.util.Range;
import org.waveprotocol.wave.model.document.util.XmlStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 17.06.11
 * Time: 8:40
 */
public class Toolbar extends Composite {
  @UiField ToplevelToolbarWidget toolbarUi;
  @UiField Toolbar.MyStyle css;
  public interface MyStyle extends CssResource {
    String item();
    String bold();
    String strikethrough();
    String superscript();
    String subscript();
    String underline();
    String italic();
    String unorderedlist();
    String alignJustify();
    String indent();
    String alignLeft();
    String alignCentre();
    String fontSize();
    String orderedlist();
    String alignDrop();
    String clearFormatting();
    String alignRight();
    String removeLink();
    String insertLink();
    String fontFamily();
    String outdent();
    String heading();
  }
  interface MyResources extends ClientBundle {
    @Source("images/edit/bold.png")
    ImageResource bold();
  }
  @UiField MyResources res = GWT.create(MyResources.class);
  private ButtonUpdater updater;
  private EditorContextAdapter editor;

  public void createButtons(EditorContextAdapter editorContextAdapter, Editor editor, ButtonUpdater updater) {
    this.updater = updater;
    this.editor = editorContextAdapter;
    ToolbarView group = toolbarUi.addGroup();
    createBoldButton(group);
    createItalicButton(group);
    createUnderlineButton(group);
    createStrikethroughButton(group);

    group = toolbarUi.addGroup();
    createSuperscriptButton(group);
    createSubscriptButton(group);

    group = toolbarUi.addGroup();
    createFontSizeButton(group);
    createFontFamilyButton(group);
    createHeadingButton(group);

    group = toolbarUi.addGroup();
    createIndentButton(group);
    createOutdentButton(group);

    group = toolbarUi.addGroup();
    createUnorderedListButton(group);
    createOrderedListButton(group);

    group = toolbarUi.addGroup();
    createAlignButtons(group);
    createClearFormattingButton(group);

    group = toolbarUi.addGroup();
    createInsertLinkButton(group);
    createRemoveLinkButton(group);
    createInsertImageButton(group);

    editor.addUpdateListener(updater);
    editorContextAdapter.switchEditor(editor);
    updater.updateButtonStates();
  }

  private void createInsertImageButton(ToolbarView toolbar) {
    new ToolbarButtonViewBuilder()
        .setText("Image")
        .applyTo(toolbar.addClickButton(), new ToolbarClickButton.Listener() {
          @Override public void onClicked() {
            Range range = editor.getSelectionHelper().getOrderedSelectionRange();
            if (range == null) {
              Window.alert("Select place in text to insert an image.");
              return;
            }
            final CMutableDocument document = editor.getDocument();
            final Point<ContentNode> point = document.locate(range.getStart());
            document.insertXml(point, XmlStringBuilder.
                createFromXmlString("<image attachment=\"/pics/hills.jpg\"><caption>some text</caption></image>"));
          }
        });
  }

  private void createBoldButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    ToolbarToggleButton.Listener listener = createTextSelectionController(b, "fontWeight", "bold");
    new ToolbarButtonViewBuilder()
        .setIcon(css.bold())
        .applyTo(b, listener);
  }

  private void createItalicButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.italic())
        .applyTo(b, createTextSelectionController(b, "fontStyle", "italic"));
  }

  private void createUnderlineButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.underline())
        .applyTo(b, createTextSelectionController(b, "textDecoration", "underline"));
  }

  private void createStrikethroughButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.strikethrough())
        .applyTo(b, createTextSelectionController(b, "textDecoration", "line-through"));
  }

  private void createSuperscriptButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.superscript())
        .applyTo(b, createTextSelectionController(b, "verticalAlign", "super"));
  }

  private void createSubscriptButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.subscript())
        .applyTo(b, createTextSelectionController(b, "verticalAlign", "sub"));
  }

  private ToolbarToggleButton.Listener createTextSelectionController(ToolbarToggleButton b,
      String styleName, String value) {
    return updater.add(new TextSelectionController(b, editor,
        StyleAnnotationHandler.key(styleName), value));
  }

  interface Binder extends UiBinder<Widget, Toolbar> { Binder BINDER = GWT.create(Binder.class); }

  public Toolbar() {
    initWidget(Binder.BINDER.createAndBindUi(this));
  }

  private void createFontSizeButton(ToolbarView toolbar) {
    SubmenuToolbarView submenu = toolbar.addSubmenu();
    new ToolbarButtonViewBuilder()
        .setIcon(css.fontSize())
        .applyTo(submenu, null);
    submenu.setShowDropdownArrow(false); // Icon already has dropdown arrow.
    // TODO(kalman): default text size option.
    ToolbarView group = submenu.addGroup();
    for (int size : asArray(8, 9, 10, 11, 12, 14, 16, 18, 21, 24, 28, 32, 36, 42, 48, 56, 64, 72)) {
      ToolbarToggleButton b = group.addToggleButton();
      double baseSize = 12.0;
      b.setVisualElement(createFontSizeElement(baseSize, size));
      b.setListener(createTextSelectionController(b, "fontSize", (size / baseSize) + "em"));
    }
  }

  private Element createFontSizeElement(double baseSize, double size) {
    Element e = Document.get().createSpanElement();
    e.getStyle().setFontSize(size / baseSize, Style.Unit.EM);
    e.setInnerText(((int) size) + "");
    return e;
  }

  private void createFontFamilyButton(ToolbarView toolbar) {
    SubmenuToolbarView submenu = toolbar.addSubmenu();
    new ToolbarButtonViewBuilder()
        .setIcon(css.fontFamily())
        .applyTo(submenu, null);
    submenu.setShowDropdownArrow(false); // Icon already has dropdown arrow.
    createFontFamilyGroup(submenu.addGroup(), new FontFamily("Default", null));
    createFontFamilyGroup(submenu.addGroup(),
        new FontFamily("Sans Serif", "sans-serif"),
        new FontFamily("Serif", "serif"),
        new FontFamily("Wide", "arial black,sans-serif"),
        new FontFamily("Narrow", "arial narrow,sans-serif"),
        new FontFamily("Fixed Width", "monospace"));
    createFontFamilyGroup(submenu.addGroup(),
        new FontFamily("Arial", "arial,helvetica,sans-serif"),
        new FontFamily("Comic Sans MS", "comic sans ms,sans-serif"),
        new FontFamily("Courier New", "courier new,monospace"),
        new FontFamily("Garamond", "garamond,serif"),
        new FontFamily("Georgia", "georgia,serif"),
        new FontFamily("Tahoma", "tahoma,sans-serif"),
        new FontFamily("Times New Roman", "times new roman,serif"),
        new FontFamily("Trebuchet MS", "trebuchet ms,sans-serif"),
        new FontFamily("Verdana", "verdana,sans-serif"));
  }

  private void createFontFamilyGroup(ToolbarView toolbar, FontFamily... families) {
    for (FontFamily family : families) {
      ToolbarToggleButton b = toolbar.addToggleButton();
      b.setVisualElement(createFontFamilyElement(family));
      b.setListener(createTextSelectionController(b, "fontFamily", family.style));
    }
  }

  private Element createFontFamilyElement(FontFamily family) {
    Element e = Document.get().createSpanElement();
    e.getStyle().setProperty("fontFamily", family.style);
    e.setInnerText(family.description);
    return e;
  }

  private void createClearFormattingButton(ToolbarView toolbar) {
    new ToolbarButtonViewBuilder()
        .setIcon(css.clearFormatting())
        .applyTo(toolbar.addClickButton(), new ToolbarClickButton.Listener() {
          @Override public void onClicked() {
            EditorAnnotationUtil.clearAnnotationsOverSelection(editor, asArray(
                StyleAnnotationHandler.key("backgroundColor"),
                StyleAnnotationHandler.key("color"),
                StyleAnnotationHandler.key("fontFamily"),
                StyleAnnotationHandler.key("fontSize"),
                StyleAnnotationHandler.key("fontStyle"),
                StyleAnnotationHandler.key("fontWeight"),
                StyleAnnotationHandler.key("textDecoration")
                // NOTE: add more as required.
            ));
            createClearHeadingsListener().onClicked();
          }
        });
  }

  private void createInsertLinkButton(ToolbarView toolbar) {
    // TODO (Yuri Z.) use createTextSelectionController when the full
    // link doodad is incorporated
    new ToolbarButtonViewBuilder()
        .setIcon(css.insertLink())
        .applyTo(toolbar.addClickButton(), new ToolbarClickButton.Listener() {
              @Override  public void onClicked() {
                FocusedRange range = editor.getSelectionHelper().getSelectionRange();
                if (range == null || range.isCollapsed()) {
                  Window.alert("Select some text to create a link.");
                  return;
                }
                String rawLinkValue =
                    Window.prompt("Enter link: URL or Wave ID.", WaveRefConstants.WAVE_URI_PREFIX);
                // user hit "ESC" or "cancel"
                if (rawLinkValue == null) {
                  return;
                }
                try {
                  String linkAnnotationValue = Link.normalizeLink(rawLinkValue);
                  EditorAnnotationUtil.setAnnotationOverSelection(editor, Link.MANUAL_KEY,
                      linkAnnotationValue);
                } catch (Link.InvalidLinkException e) {
                  Window.alert(e.getLocalizedMessage());
                }
              }
            });
  }

  private void createRemoveLinkButton(ToolbarView toolbar) {
    new ToolbarButtonViewBuilder()
        .setIcon(css.removeLink())
        .applyTo(toolbar.addClickButton(), new ToolbarClickButton.Listener() {
          @Override public void onClicked() {
            if (editor.getSelectionHelper().getSelectionRange() != null) {
              EditorAnnotationUtil.clearAnnotationsOverSelection(editor, Link.LINK_KEYS);
            }
          }
        });
  }

  private ToolbarClickButton.Listener createClearHeadingsListener() {
    return new ParagraphTraversalController(editor, new ContentElement.Action() {
        @Override public void execute(ContentElement e) {
          e.getMutableDoc().setElementAttribute(e, Paragraph.SUBTYPE_ATTR, null);
        }
      });
  }

  /**
   * Container for a font family.
   */
  private static final class FontFamily {
    public final String description;
    public final String style;
    public FontFamily(String description, String style) {
      this.description = description;
      this.style = style;
    }
  }

  /**
   * Container for an alignment.
   */
  private static final class Alignment {
    public final String description;
    public final String iconCss;
    public final Paragraph.LineStyle style;
    public Alignment(String description, String iconCss, Paragraph.LineStyle style) {
      this.description = description;
      this.iconCss = iconCss;
      this.style = style;
    }
  }

  private void createHeadingButton(ToolbarView toolbar) {
    SubmenuToolbarView submenu = toolbar.addSubmenu();
    new ToolbarButtonViewBuilder()
        .setIcon(css.heading())
        .applyTo(submenu, null);
    submenu.setShowDropdownArrow(false); // Icon already has dropdown arrow.
    ToolbarClickButton defaultButton = submenu.addClickButton();
    new ToolbarButtonViewBuilder()
        .setText("Default")
        .applyTo(defaultButton, createClearHeadingsListener());
    ToolbarView group = submenu.addGroup();
    for (int level : asArray(1, 2, 3, 4)) {
      ToolbarToggleButton b = group.addToggleButton();
      b.setVisualElement(createHeadingElement(level));
      b.setListener(createParagraphApplicationController(b, Paragraph.regularStyle("h" + level)));
    }
  }

  private Element createHeadingElement(int level) {
    Element e = Document.get().createElement("h" + level);
    e.getStyle().setMarginTop(2, Style.Unit.PX);
    e.getStyle().setMarginBottom(2, Style.Unit.PX);
    e.setInnerText("Heading " + level);
    return e;
  }

  private void createIndentButton(ToolbarView toolbar) {
    ToolbarClickButton b = toolbar.addClickButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.indent())
        .applyTo(b, new ParagraphTraversalController(editor, Paragraph.INDENTER));
  }

  private void createOutdentButton(ToolbarView toolbar) {
    ToolbarClickButton b = toolbar.addClickButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.outdent())
        .applyTo(b, new ParagraphTraversalController(editor, Paragraph.OUTDENTER));
  }

  private void createUnorderedListButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.unorderedlist())
        .applyTo(b, createParagraphApplicationController(b, Paragraph.listStyle(null)));
  }

  private void createOrderedListButton(ToolbarView toolbar) {
    ToolbarToggleButton b = toolbar.addToggleButton();
    new ToolbarButtonViewBuilder()
        .setIcon(css.orderedlist())
        .applyTo(b, createParagraphApplicationController(
            b, Paragraph.listStyle(Paragraph.LIST_STYLE_DECIMAL)));
  }

  private void createAlignButtons(ToolbarView toolbar) {
    SubmenuToolbarView submenu = toolbar.addSubmenu();
    new ToolbarButtonViewBuilder()
        .setIcon(css.alignDrop())
        .applyTo(submenu, null);
    submenu.setShowDropdownArrow(false); // Icon already has dropdown arrow.
    ToolbarView group = submenu.addGroup();
    for (Alignment alignment : asArray(
        new Alignment("Left", css.alignLeft(), Paragraph.Alignment.LEFT),
        new Alignment("Centre", css.alignCentre(), Paragraph.Alignment.CENTER),
        new Alignment("Right", css.alignRight(), Paragraph.Alignment.RIGHT))) {
      ToolbarToggleButton b = group.addToggleButton();
      new ToolbarButtonViewBuilder()
          .setText(alignment.description)
          .setIcon(alignment.iconCss)
          .applyTo(b, createParagraphApplicationController(b, alignment.style));
    }
  }

  private ToolbarToggleButton.Listener createParagraphApplicationController(ToolbarToggleButton b,
      Paragraph.LineStyle style) {
    return updater.add(new ParagraphApplicationController(b, editor, style));
  }

  private static <E> E[] asArray(E... elements) {
    return elements;
  }
}

