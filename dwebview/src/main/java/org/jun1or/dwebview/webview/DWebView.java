package org.jun1or.dwebview.webview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import org.jun1or.dwebview.callback.JavascriptCloseWindowListener;
import org.jun1or.dwebview.callback.OnReturnValue;
import org.jun1or.dwebview.callback.OpenFileChooserCallback;
import org.jun1or.dwebview.callback.UrlOverrideListener;
import org.jun1or.dwebview.callback.WebViewActionHappenListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Junior
 */
public class DWebView extends WebView implements UrlOverrideListener, DownloadListener {


    private static final String BRIDGE_NAME = "_dsbridge";

    private DefaultWebViewClient mDefaultWebViewClient;
    private DefaultWebChromeClient mDefaultWebChromeClient;
    private DownloadListener mDownloadListener;

    private static boolean sIsDebug = false;
    private InnerJavascriptInterface mInnerJavascriptInterface = null;
    private InnerJavascriptInterface.DefaultJavascriptInterface mDefaultJavascriptInterface;

    private JavascriptCloseWindowListener mJavascriptCloseWindowListener;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private ArrayList<CallInfo> mCallInfoList;

    private Map<Integer, OnReturnValue> mHandleMap = new HashMap<>();

    private int mCallID = 0;


    public DWebView(Context context) {
        super(context);
        init();
    }

    public DWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mInnerJavascriptInterface = new InnerJavascriptInterface(this);
        mDefaultWebChromeClient = new DefaultWebChromeClient();
        mDefaultWebViewClient = new DefaultWebViewClient();
        mDefaultWebViewClient.setUrlOverrideListener(this);
        super.setWebChromeClient(mDefaultWebChromeClient);
        super.setWebViewClient(mDefaultWebViewClient);
        super.setDownloadListener(this);
        mDefaultJavascriptInterface = mInnerJavascriptInterface.new DefaultJavascriptInterface();
        mInnerJavascriptInterface.setDebug(sIsDebug);
        mInnerJavascriptInterface.addJavascriptObject(mDefaultJavascriptInterface, "_dsb");
        super.addJavascriptInterface(mInnerJavascriptInterface, BRIDGE_NAME);
        //移除有风险的WebView系统隐藏接口漏洞
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");

    }

    @Override
    public boolean canGoBack() {
        return super.canGoBack();
    }

    @Override
    public void goBack() {
        super.goBack();
    }

    @Override
    public void loadUrl(final String url) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (url != null && url.startsWith("javascript:")) {
                    DWebView.super.loadUrl(url);
                } else {
                    mCallInfoList = new ArrayList<>();
                    DWebView.super.loadUrl(url);
                }
            }
        });
    }

    public void setOpenFileChooserCallback(OpenFileChooserCallback openFileChooserCallback) {
        this.mDefaultWebChromeClient.setOpenFileChooserCallback(openFileChooserCallback);
    }

    @Override
    public void setWebViewClient(WebViewClient webViewClient) {
        mDefaultWebViewClient.setProxyWebViewClient(webViewClient);
    }

    @Override
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        mDefaultWebChromeClient.setWebChromeClient(webChromeClient);
    }

    public void setWebViewActionHappenListener(WebViewActionHappenListener webViewActionHappenListener) {
        mDefaultWebViewClient.setWebViewActionHappenListener(webViewActionHappenListener);
        mDefaultWebChromeClient.setWebViewActionHappenListener(webViewActionHappenListener);
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    public void setJavascriptCloseWindowListener(JavascriptCloseWindowListener javascriptCloseWindowListener) {
        this.mJavascriptCloseWindowListener = javascriptCloseWindowListener;
        mDefaultJavascriptInterface.setJavascriptCloseWindowListener(mJavascriptCloseWindowListener);
    }

    public static void setWebContentsDebuggingEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(enabled);
        }
        sIsDebug = enabled;
    }

    public void addJavascriptObject(Object object, String namespace) {
        mInnerJavascriptInterface.addJavascriptObject(object, namespace);
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    public void removeJavascriptObject(String namespace) {
        mInnerJavascriptInterface.removeJavascriptObject(namespace);
    }


    protected Map<Integer, OnReturnValue> getHandleMap() {
        return mHandleMap;
    }


    public void evaluateJavascript(final String script) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                _evaluateJavascript(script);
            }
        });
    }

    private void _evaluateJavascript(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.evaluateJavascript(script, null);
        } else {
            super.loadUrl("javascript:" + script);
        }
    }


    protected synchronized void dispatchStartupQueue() {
        if (mCallInfoList != null) {
            for (CallInfo info : mCallInfoList) {
                dispatchJavascriptCall(info);
            }
            mCallInfoList = null;
        }
    }

    private void dispatchJavascriptCall(CallInfo info) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()));
    }


    public synchronized <T> void callHandler(String method, Object[] args, final OnReturnValue<T> handler) {

        CallInfo callInfo = new CallInfo(method, mCallID, args);
        if (handler != null) {
            mHandleMap.put(mCallID++, handler);
        }
        if (mCallInfoList != null) {
            mCallInfoList.add(callInfo);
        } else {
            dispatchJavascriptCall(callInfo);
        }
    }

    public void callHandler(String method, Object[] args) {
        callHandler(method, args, null);
    }

    public <T> void callHandler(String method, OnReturnValue<T> handler) {
        callHandler(method, null, handler);
    }

    protected void runOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
            return;
        }
        mMainHandler.post(runnable);
    }

    public void hasJavascriptMethod(String handlerName, OnReturnValue<Boolean> existCallback) {
        callHandler("_hasJavascriptMethod", new Object[]{handlerName}, existCallback);
    }

    @Override
    public void shouldOverrideUrlLoadingHappened(String url) {
    }

    @Override
    public void onPageFinishedHappened(String url) {
//        evaluateJavascript("window._goBack = function(){\n" +
//                "             history.go(-1);\n" +
//                "             }");
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
        }
    }
}
