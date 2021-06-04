package com.radaee.reactnative;

import android.graphics.Canvas;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Page;
import com.radaee.reader.PDFLayoutView;
import com.radaee.util.PDFAssetStream;
import com.radaee.util.PDFHttpStream;
import com.radaee.view.ILayoutView;

import java.util.Map;


public class RadaeepdfViewManager extends SimpleViewManager<PDFLayoutView> implements LifecycleEventListener, ILayoutView.PDFLayoutListener {
    private static final String REACT_CLASS = "RadaeepdfView";
    private ThemedReactContext context;

    private PDFAssetStream m_asset_stream = null;
    private PDFHttpStream m_http_stream = null;
    private Page.Annotation m_annotation = null;

    private Document m_doc;

    private PDFLayoutView m_view = null;

    private static final int COMMAND_SET_SELECT = 1;
    private static final int COMMAND_SET_SEL_MARKUP = 2;
    private static final int COMMAND_UNDO = 3;
    private static final int COMMAND_REDO = 4;
    private static final int COMMAND_SAVE = 5;
    private static final int COMMAND_REMOVE_ANNOT = 6;

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public PDFLayoutView createViewInstance(ThemedReactContext context) {
        Global.Init(context);
        Global.navigationMode = 0; //thumbnail navigation mode

        m_view = new PDFLayoutView(context);

        this.context = context;
        return m_view;
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "setSelect", COMMAND_SET_SELECT,
                "setSelMarkup", COMMAND_SET_SEL_MARKUP,
                "undo", COMMAND_UNDO,
                "redo", COMMAND_REDO,
                "save", COMMAND_SAVE,
                "removeAnnot", COMMAND_REMOVE_ANNOT
        );
    }

    @Override
    public void receiveCommand(PDFLayoutView view, int commandType, @Nullable ReadableArray args) {
        Assertions.assertNotNull(view);
        Assertions.assertNotNull(args);
        switch (commandType) {
            case COMMAND_SET_SELECT: {
                view.PDFSetSelect();
                return;
            }
            case COMMAND_SET_SEL_MARKUP: {
                if (!args.isNull(0)) {
                    view.PDFSetSelMarkup(args.getInt(0));
                }
                return;
            }
            case COMMAND_UNDO: {
                view.PDFUndo();
                return;
            }
            case COMMAND_REDO: {
                view.PDFRedo();
                return;
            }
            case COMMAND_SAVE: {
                if (view.PDFCanSave()) {
                    view.PDFGetDoc().Save();
                    onCommandResult(commandType, null);
                }

                return;
            }
            case COMMAND_REMOVE_ANNOT: {
                if (m_annotation != null) {
                    if (m_annotation.RemoveFromPage()) {
                        m_annotation = null;
                        onCommandResult(commandType, null);
                    }
                }
            }
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported command %d received by %s.",
                        commandType,
                        getClass().getSimpleName()));
        }
    }

    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "pageChange",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPageChange")))
                .put(
                        "pageModified",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPageModified")))
                .put("blankTap",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onBlankTap")))
                .put("doubleTap",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onDoubleTap")))
                .put("longPress",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onLongPress")))
                .put("selectEnd",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onSelectEnd")))
                .put("error",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onError")))
                .put("commandResult",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onCommandResult")))
                .put("annotTap",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onAnnotTap")))
                .build();
    }

    @ReactProp(name = "source")
    public void setSource(PDFLayoutView view, @Nullable ReadableMap map) {
        Close();
        if (map != null) {
            String uri = map.hasKey("uri") ? map.getString("uri") : null;
            String password = map.hasKey("password") ? map.getString("password") : null;
            int page = map.hasKey("page") ? map.getInt("page") : 0;

            if (!TextUtils.isEmpty(uri)) {
                m_doc = new Document();
                int ret;

                if (URLUtil.isHttpUrl(uri) || URLUtil.isHttpsUrl(uri)) {
                    m_http_stream = new PDFHttpStream();
                    boolean ok = m_http_stream.open(uri);
                    if (!ok) {
                        WritableMap event = Arguments.createMap();
                        event.putString("message", "Open stream error");
                        sendEvent("error", event);
                        return;
                    }
                    ret = m_doc.OpenStream(m_http_stream, password);
                } else if (URLUtil.isFileUrl(uri)) {
                    String prefix = "file://";
                    assert uri != null;
                    uri = uri.substring(uri.indexOf(prefix) + prefix.length());
                    ret = m_doc.Open(uri, password);
                } else {
                    m_asset_stream = new PDFAssetStream();
                    boolean ok = m_asset_stream.open(this.context.getAssets(), uri);
                    if (!ok) {
                        WritableMap event = Arguments.createMap();
                        event.putString("message", "Open stream error");
                        sendEvent("error", event);
                        return;
                    }
                    ret = m_doc.OpenStream(m_asset_stream, password);
                }

                if (ret != 0) {
                    m_doc.Close();
                    m_doc = null;
                    WritableMap event = Arguments.createMap();
                    event.putString("message", "Open doc error");
                    sendEvent("error", event);
                    return;
                }

                view.PDFOpen(m_doc, this);
                view.PDFGotoPage(page);
            } else {
                if (m_doc != null) {
                    m_doc.Close();
                    m_doc = null;
                }
            }
        }

    }

    @ReactProp(name = "mode")
    public void setMode(PDFLayoutView view, int mode) {
        view.PDFSetView(mode);
    }


    @ReactProp(name = "debug")
    public void setDebig(PDFLayoutView view, boolean debug) {
        Global.debug_mode = debug;
    }

    @Override
    public void onHostResume() {
        if (m_doc == null)
            m_doc = m_view.PDFGetDoc();
    }

    @Override
    public void onHostPause() {
        if (m_doc != null) {
            m_doc.Close();
            m_doc = null;
        }
    }

    @Override
    public void onHostDestroy() {
        Close();
        m_view = null;
        context = null;
        Global.RemoveTmp();
    }

    private void Close() {
        if (m_doc != null) {
            m_view.PDFClose();
            m_doc.Close();
            m_doc = null;
        }

        if (m_asset_stream != null) {
            m_asset_stream.close();
            m_asset_stream = null;
        }

        if (m_http_stream != null) {
            m_http_stream.close();
            m_http_stream = null;
        }

        if (m_annotation != null) {
            m_annotation = null;
        }
    }

    private void sendEvent(String name, WritableMap event) {
        context.getJSModule(RCTEventEmitter.class).receiveEvent(
                m_view.getId(),
                name,
                event);
    }

    private void onCommandResult(int commandType, @Nullable WritableMap event) {
        if (event == null) {
            event = Arguments.createMap();
        }
        event.putInt("type", commandType);
        sendEvent("commandResult", event);
    }

    @Override
    public void OnPDFPageModified(int pageno) {
        WritableMap event = Arguments.createMap();
        event.putInt("page", pageno);
        sendEvent("pageModified", event);
    }

    @Override
    public void OnPDFPageChanged(int pageno) {
        WritableMap event = Arguments.createMap();
        event.putInt("page", pageno);
        sendEvent("pageChange", event);
    }

    @Override
    public void OnPDFAnnotTapped(int pno, Page.Annotation annot) {
        WritableMap event = Arguments.createMap();
        m_annotation = annot;
        if (pno != -1 && m_annotation != null) {
            float[] rect = m_annotation.GetRect();
            event.putDouble("x", rect[0]);
            event.putDouble("y", rect[1]);
            event.putBoolean("selected", true);
        } else {
            event.putDouble("x", 0);
            event.putDouble("y", 0);
            event.putBoolean("selected", false);
        }
        sendEvent("annotTap", event);

    }

    @Override
    public void OnPDFBlankTapped() {
        sendEvent("blankTap", null);
    }

    @Override
    public void OnPDFSelectEnd(String text, float x, float y) {
        WritableMap event = Arguments.createMap();
        event.putDouble("x", x);
        event.putDouble("y", y);
        event.putString("text", text);
        sendEvent("selectEnd", event);
    }

    @Override
    public void OnPDFOpenURI(String uri) {

    }

    @Override
    public void OnPDFOpenJS(String js) {

    }

    @Override
    public void OnPDFOpenMovie(String path) {

    }

    @Override
    public void OnPDFOpenSound(int[] paras, String path) {

    }

    @Override
    public void OnPDFOpenAttachment(String path) {

    }

    @Override
    public void OnPDFOpen3D(String path) {

    }

    @Override
    public void OnPDFZoomStart() {

    }

    @Override
    public void OnPDFZoomEnd() {

    }

    @Override
    public boolean OnPDFDoubleTapped(float x, float y) {
        WritableMap event = Arguments.createMap();
        event.putDouble("x", x);
        event.putDouble("y", y);
        sendEvent("doubleTap", event);
        return false;
    }

    @Override
    public void OnPDFLongPressed(float x, float y) {
        WritableMap event = Arguments.createMap();
        event.putDouble("x", x);
        event.putDouble("y", y);
        sendEvent("longPress", event);
    }

    @Override
    public void OnPDFSearchFinished(boolean found) {

    }

    @Override
    public void OnPDFPageDisplayed(Canvas canvas, ILayoutView.IVPage vpage) {

    }

    @Override
    public void OnPDFPageRendered(ILayoutView.IVPage vpage) {

    }
}
