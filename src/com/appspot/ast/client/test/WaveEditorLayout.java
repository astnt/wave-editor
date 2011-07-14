package com.appspot.ast.client.test;

import com.appspot.ast.client.editor.PrettyWithAttributes;
import com.appspot.ast.client.editor.Toolbar;
import com.appspot.ast.client.editor.doodad.my.MyDoodad;
import com.appspot.ast.client.editor.doodad.phone.PhoneDoodad;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.waveprotocol.wave.client.common.util.KeySignalListener;
import org.waveprotocol.wave.client.common.util.SignalEvent;
import org.waveprotocol.wave.client.debug.logger.DomLogger;
import org.waveprotocol.wave.client.doodad.attachment.ImageThumbnail;
import org.waveprotocol.wave.client.doodad.attachment.render.ImageThumbnailWrapper;
import org.waveprotocol.wave.client.doodad.attachment.testing.FakeAttachmentsManager;
import org.waveprotocol.wave.client.doodad.form.FormDoodads;
import org.waveprotocol.wave.client.doodad.link.LinkAnnotationHandler;
import org.waveprotocol.wave.client.editor.*;
import org.waveprotocol.wave.client.editor.content.ContentDocument;
import org.waveprotocol.wave.client.editor.content.ContentElement;
import org.waveprotocol.wave.client.editor.content.ContentNode;
import org.waveprotocol.wave.client.editor.content.Registries;
import org.waveprotocol.wave.client.editor.content.misc.StyleAnnotationHandler;
import org.waveprotocol.wave.client.editor.content.paragraph.LineRendering;
import org.waveprotocol.wave.client.editor.keys.KeyBindingRegistry;
import org.waveprotocol.wave.client.editor.toolbar.ButtonUpdater;
import org.waveprotocol.wave.client.editor.util.EditorDocFormatter;
import org.waveprotocol.wave.client.widget.popup.simple.Popup;
import org.waveprotocol.wave.common.logging.LoggerBundle;
import org.waveprotocol.wave.model.document.operation.DocInitialization;
import org.waveprotocol.wave.model.document.operation.DocOp;
import org.waveprotocol.wave.model.document.operation.automaton.DocOpAutomaton;
import org.waveprotocol.wave.model.document.operation.automaton.DocumentSchema;
import org.waveprotocol.wave.model.document.operation.impl.DocOpValidator;
import org.waveprotocol.wave.model.document.parser.XmlParseException;
import org.waveprotocol.wave.model.document.util.DocProviders;
import org.waveprotocol.wave.model.document.util.LineContainers;
import org.waveprotocol.wave.model.operation.SilentOperationSink;
import org.waveprotocol.wave.model.schema.conversation.ConversationSchemas;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.07.11
 * Time: 13:20
 */
public class WaveEditorLayout extends Composite implements KeySignalListener {
  private final EditorContextAdapter editor = new EditorContextAdapter(null);
  private final ButtonUpdater updater = new ButtonUpdater(editor);
  private ContentDocument document;

  @Override
  public boolean onKeySignal(Widget widget, SignalEvent signalEvent) {
    return false;
  }

  interface Binder extends UiBinder<Widget, WaveEditorLayout> { Binder BINDER = GWT.create(Binder.class); }
  interface MyStyle extends CssResource {
    String sourceContainer();
    String source();
    String item();
    String original();
    String imageContainer();
    String log();
  }

  @UiField MyStyle style;
  @UiField Toolbar toolbar;
  @UiField(provided = true) Widget richEditorLayout;
  @UiField HTML log;

  LoggerBundle logger = new DomLogger("test");

  private static final String TOPLEVEL_CONTAINER_TAGNAME = "body";
  static {
    LineContainers.setTopLevelContainerTagname(TOPLEVEL_CONTAINER_TAGNAME);
    Editors.initRootRegistries();
  }

  public WaveEditorLayout() {
    EditorStaticDeps.setPopupProvider(Popup.LIGHTWEIGHT_POPUP_PROVIDER);
    DocInitialization op = createDocInitialization("<body><line/>Phone: <phone><code>465</code><number>746-45-16</number></phone> text with <?a \"style/fontWeight\"=\"bold\"?>bold and <?a \"style/fontStyle\"=\"italic\"?>italic<?a \"style/fontStyle\" \"style/fontWeight\"?><line/>text<line/><mydoodad ref=\"/pics/hills.jpg\"/><line/>Предложение первое.<line/>Предложение еще одно.<line/><image attachment=\"/pics/hills.jpg\"><caption>Подпись к картинке</caption></image><line/>Еще дополнительный текст</body>");

    DocOpAutomaton.ViolationCollector vc = new DocOpAutomaton.ViolationCollector();
    if (!DocOpValidator.validate(vc, getSchema(), op).isValid()) {
      GWT.log("That content does not conform to the schema: " + vc.firstDescription());
      logger.error().log("That content does not conform to the schema", vc);
//      return;
    }

    registerDoodads(Editor.ROOT_REGISTRIES);

    final Editor richEditor = createEditor(op, Editor.ROOT_REGISTRIES);

    richEditor.addUpdateListener(new EditorUpdateEvent.EditorUpdateListener() {
      @Override
      public void onUpdate(EditorUpdateEvent editorUpdateEvent) {
        outputEditorState(richEditor, null);
      }
    });
    richEditorLayout = richEditor.getWidget();
    initWidget(Binder.BINDER.createAndBindUi(this));
    toolbar.createButtons(this.editor, richEditor, updater);

    DomLogger.enable(log.getElement());
    DomLogger.enableAllModules();
    DomLogger.enableModule("test", true);
    DomLogger.enableModule("editor", true);
    DomLogger.enableModule("editor-node", true);
    DomLogger.enableModule("operator", true);
    DomLogger.enableModule("dragdrop", true);
  }

  private DocInitialization createDocInitialization(String content) {
    DocInitialization op = null;
    try {
      op = DocProviders.POJO.parse(content).asOperation();
    } catch (IllegalArgumentException e) {
      if (e.getCause() instanceof XmlParseException) {
        logger.error().log("Ill-formed XML string ", e.getCause());
      } else {
        logger.error().log("Error", e);
      }
    }
    return op;
  }

  public void registerDoodads(Registries registries) {

//    ElementHandlerRegistry testHandlerRegistry =
//        testEditorRegistries.getElementHandlerRegistry();

    LineRendering.registerContainer(TOPLEVEL_CONTAINER_TAGNAME,
        registries.getElementHandlerRegistry());

    StyleAnnotationHandler.register(registries);
//    DiffAnnotationHandler.register(
//        registries.getAnnotationHandlerRegistry(),
//        registries.getPaintRegistry());

    MyDoodad.register(registries.getElementHandlerRegistry());
    PhoneDoodad.register(registries.getElementHandlerRegistry());

    LinkAnnotationHandler.register(registries, new LinkAnnotationHandler.LinkAttributeAugmenter() {
      @Override
      public Map<String, String> augment(Map<String, Object> annotations, boolean isEditing,
                                         Map<String, String> current) {
        return current;
      }
    });
//    PhoneAnnotationHandler.register(registries, new PhoneAnnotationHandler.PhoneAttributeAugmenter() {
//      @Override
//      public Map<String, String> augment(Map<String, Object> annotations, boolean isEditing,
//                                         Map<String, String> current) {
//        return current;
//      }
//    });

    extendedDoodadFromDefaultHarness(registries);
  }

  private void extendedDoodadFromDefaultHarness(Registries registries) {
    FormDoodads.register(registries.getElementHandlerRegistry());

    // We'll need an attachment manager
    FakeAttachmentsManager attachmentManager = new FakeAttachmentsManager();
    // Create a few attachments
    attachmentManager.createFakeAttachment("/pics/Snow.jpg", 120, 80);
    attachmentManager.createFakeAttachment("/pics/yosemite.jpg", 120, 80);
    attachmentManager.createFakeAttachment("/pics/hills.jpg", 120, 74);
    attachmentManager.createFakeAttachment("/pics/Beautiful+View.jpg", 120, 74);

    ImageThumbnail.register(registries.getElementHandlerRegistry(), attachmentManager,
        new ImageThumbnail.ThumbnailActionHandler() {
          @Override
          public boolean onClick(ImageThumbnailWrapper thumbnail) {
            ContentElement e = thumbnail.getElement();
            String newId = Window.prompt("New attachment id, or 'remove' to remove the attribute",
                e.getAttribute(ImageThumbnail.ATTACHMENT_ATTR));

            if (newId == null) {
              // They hit escape
              return true;
            }

            if ("remove".equals(newId)) {
              newId = null;
            }

            e.getMutableDoc().setElementAttribute(e, ImageThumbnail.ATTACHMENT_ATTR, newId);
            return true;
          }
        });
  }

  private DocumentSchema getSchema() {
//    return ConversationSchemas.BLIP_SCHEMA_CONSTRAINTS;
    return new ConversationSchemas.DefaultDocumentSchema() {
      {
        // Permit our doodad to appear inside a <body> element
        addChildren("body", MyDoodad.TAGNAME);
        addChildren("body", PhoneDoodad.TAGNAME);

        // Permit a 'ref' attribute on the <mydoodad> element.
        // e.g. permit content like <mydoodad ref='pics/wave.gif'/>
        addAttrs(MyDoodad.TAGNAME, MyDoodad.REF_ATTR);

        // Permit our caption element to appear inside our doodad's main
        // element, e.g.
        // <mydoodad>
        //   <mycaption>text permitted here</mycaption>
        // </mydoodad>
        addChildren(MyDoodad.TAGNAME, MyDoodad.CAPTION_TAGNAME);
        containsBlipText(MyDoodad.CAPTION_TAGNAME);

        addChildren(PhoneDoodad.TAGNAME, PhoneDoodad.CODE_TAGNAME);
        addChildren(PhoneDoodad.TAGNAME, PhoneDoodad.NUMBER_TAGNAME);
        containsBlipText(PhoneDoodad.CODE_TAGNAME);
        containsBlipText(PhoneDoodad.NUMBER_TAGNAME);
      }
    };
  }

  private Editor createEditor(DocInitialization op, Registries editorRegistries) {
    final Editor editor = Editors.create();
    editor.addKeySignalListener(this);
    final DocumentSchema schema = getSchema();
    editor.setContent(document = new ContentDocument(editorRegistries, op, schema));
    editor.setOutputSink(new SilentOperationSink<DocOp>() {
      @Override
      public void consume(DocOp docOp) {
        //To change body of implemented methods use File | Settings | File Templates.
      }
    });
    editor.init(editorRegistries, new KeyBindingRegistry(), EditorSettings.DEFAULT);
    editor.setEditing(true);
    if (editor.isEditing()) {
      editor.focus(true);
    }
    return editor;
  }

  private void outputEditorState(final Editor richEditor, final Editor sourceEditor) {
    Runnable printer = new Runnable() {
      public void run() {
        String content1 = EditorDocFormatter.formatContentDomString(richEditor);
        String dom1 = EditorDocFormatter.formatPersistentDomString(richEditor);
        if (content1 != null) {
//          source.setText(dom1);
//          sourceAdopted.setText(new PrettyWithAttributes<ContentNode>().print(richEditor.getContent().getRenderedView()));
        }
      }
    };
    if (richEditor.getContent().flush(printer)) {
      printer.run(); // note that if true is returned, the command isn't run inside flush.
    }
  }
}
