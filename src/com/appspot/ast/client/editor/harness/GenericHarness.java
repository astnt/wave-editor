package com.appspot.ast.client.editor.harness;

import com.appspot.ast.client.editor.EditorDocOpUtil;
import com.appspot.ast.client.editor.EditorStreamingXmlParser;
import com.appspot.ast.client.editor.PrettyWithAttributes;
import com.appspot.ast.client.editor.doodad.blockquote.BlockQuoteDoodad;
import com.appspot.ast.client.editor.doodad.my.MyDoodad;
import com.appspot.ast.client.editor.doodad.paragraph.ParagraphDoodad;
import com.appspot.ast.client.editor.doodad.phone.PhoneDoodad;
import com.appspot.ast.client.editor.doodad.video.VideoDoodad;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
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
 * Date: 15.07.11
 * Time: 8:42
 */
public class GenericHarness implements KeySignalListener {

  LoggerBundle logger = new DomLogger("test");

  private static final String TOPLEVEL_CONTAINER_TAGNAME = "body";
  static {
    LineContainers.setTopLevelContainerTagname(TOPLEVEL_CONTAINER_TAGNAME);
    Editors.initRootRegistries();
  }

  private final EditorContextAdapter editor = new EditorContextAdapter(null);
  private final ButtonUpdater updater = new ButtonUpdater(editor);
  private ContentDocument document;
  private Editor richEditor;
  private TextArea sourceAdopted;
  private FakeAttachmentsManager attachmentManager;

  public GenericHarness() {
    EditorStaticDeps.setPopupProvider(Popup.LIGHTWEIGHT_POPUP_PROVIDER);

    DocInitialization op = createDocInitialization("<body><line/></body>");
    validate(op);

    registerDoodads(Editor.ROOT_REGISTRIES);

    richEditor = createEditor(op, Editor.ROOT_REGISTRIES);
  }

  public void addUpdateListener(TextArea sourceAdopted) {
    if (sourceAdopted != null) {
      this.sourceAdopted = sourceAdopted;
      GWT.log("update listener added");
      richEditor.addUpdateListener(new EditorUpdateEvent.EditorUpdateListener() {
        @Override
        public void onUpdate(EditorUpdateEvent editorUpdateEvent) {
//          outputEditorState(richEditor);
        }
      });
    }
  }

  public DocInitialization createDocInitialization(String content) {
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

  private boolean validate(DocInitialization op) {
    DocOpAutomaton.ViolationCollector vc = new DocOpAutomaton.ViolationCollector();
    if (!DocOpValidator.validate(vc, getSchema(), op).isValid()) {
      GWT.log("That content does not conform to the schema: " + vc.firstDescription());
      logger.error().log("That content does not conform to the schema", vc);
      return false;
    }
    return true;
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

  private DocumentSchema getSchema() {
//    return ConversationSchemas.BLIP_SCHEMA_CONSTRAINTS;
    return new ConversationSchemas.DefaultDocumentSchema() {
      {
        // Permit our doodad to appear inside a <body> element
        addChildren("body", MyDoodad.TAGNAME);
        addChildren("body", PhoneDoodad.TAGNAME);
        addChildren("body", BlockQuoteDoodad.TAGNAME);
        addChildren("body", ParagraphDoodad.TAGNAME);
        addChildren("body", VideoDoodad.TAGNAME);
        containsBlipText(BlockQuoteDoodad.TAGNAME);
        containsBlipText(ParagraphDoodad.TAGNAME);
        addChildren(BlockQuoteDoodad.TAGNAME, PhoneDoodad.TAGNAME);
        addChildren(BlockQuoteDoodad.TAGNAME, ParagraphDoodad.TAGNAME);
        addChildren(BlockQuoteDoodad.TAGNAME, VideoDoodad.TAGNAME);
        addChildren(ParagraphDoodad.TAGNAME, VideoDoodad.TAGNAME);

        // Permit a 'ref' attribute on the <mydoodad> element.
        // e.g. permit content like <mydoodad ref='pics/wave.gif'/>
        addAttrs(MyDoodad.TAGNAME, MyDoodad.REF_ATTR);
        addAttrs(VideoDoodad.TAGNAME, VideoDoodad.REF_ATTR);

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

  private void outputEditorState(final Editor richEditor) {
    Runnable printer = new Runnable() {
      public void run() {
        final ContentDocument content = richEditor.getContent();
        if (content != null) {
          sourceAdopted.setText(new PrettyWithAttributes<ContentNode>().print(content.getRenderedView()));
        }
      }
    };
    if (richEditor.getContent().flush(printer)) {
      printer.run(); // note that if true is returned, the command isn't run inside flush.
    }
  }

  public void enableLog(HTML log) {
    DomLogger.enable(log.getElement());
    DomLogger.enableAllModules();
    DomLogger.enableModule("test", true);
    DomLogger.enableModule("editor", true);
    DomLogger.enableModule("editor-node", true);
    DomLogger.enableModule("operator", true);
    DomLogger.enableModule("dragdrop", true);
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
    BlockQuoteDoodad.register(registries.getElementHandlerRegistry());
    ParagraphDoodad.register(registries.getElementHandlerRegistry());
    VideoDoodad.register(registries.getElementHandlerRegistry());

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

    FormDoodads.register(registries.getElementHandlerRegistry());

    // We'll need an attachment manager
    attachmentManager = new FakeAttachmentsManager();
    // Create a few attachments
//    attachmentManager.createFakeAttachment("/pics/Snow.jpg", 120, 80);
//    attachmentManager.createFakeAttachment("/pics/yosemite.jpg", 120, 80);
//    attachmentManager.createFakeAttachment("/pics/hills.jpg", 120, 74);
//    attachmentManager.createFakeAttachment("/pics/Beautiful+View.jpg", 120, 74);

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

  public Widget getRichEditorWidget() {
    return richEditor.getWidget();
  }

  public Editor getRichEditor() {
    return richEditor;
  }

  public EditorContextAdapter getEditor() {
    return editor;
  }

  public ButtonUpdater getUpdater() {
    return updater;
  }

  @Override
  public boolean onKeySignal(Widget widget, SignalEvent signalEvent) {
    return false;
  }

  public LoggerBundle getLogger() {
    return logger;
  }

  public boolean setText(String text) {
    boolean result = false;
    final DocInitialization docInitialization;
    try {
      docInitialization = EditorDocOpUtil.docInitializationFromXml(text, new EditorStreamingXmlParser(text));
      if (validate(docInitialization)) {
        richEditor.setContent(document = new ContentDocument(Editor.ROOT_REGISTRIES, docInitialization,
          getSchema()));
        result = true;
      }
    } catch (XmlParseException e) {
      logger.error().log(e);
    }
    return result;
  }

  public void outputEditorState() {
    outputEditorState(richEditor);
  }

  public String getText() {
    return new PrettyWithAttributes<ContentNode>().print(richEditor.getContent().getRenderedView());
  }

  public FakeAttachmentsManager getAttachmentManager() {
    return attachmentManager;
  }
}
