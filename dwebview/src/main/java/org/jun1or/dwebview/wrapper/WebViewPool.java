package org.jun1or.dwebview.wrapper;

import android.app.Application;
import android.webkit.WebView;


import org.jun1or.dwebview.webview.DWebView;

import java.util.ArrayList;
import java.util.List;

public class WebViewPool {

    private static List<WebView> sAvailable = new ArrayList<>();
    private static List<WebView> sInUse = new ArrayList<>();

    private static final byte[] sLock = new byte[]{};
    private static int sMaxSize = 3;
    private static Application sContext;

    private WebViewPool() {
        sAvailable = new ArrayList<>();
        sInUse = new ArrayList<>();
    }

    private static volatile WebViewPool instance = null;

    public static WebViewPool getInstance() {
        if (instance == null) {
            synchronized (WebViewPool.class) {
                if (instance == null) {
                    instance = new WebViewPool();
                }
            }
        }
        return instance;
    }

    /**
     * Webview 初始化
     */
    public void init(Application context) {
        sContext = context;
        for (int i = 0; i < sMaxSize; i++) {
            WebView webView = new DWebView(sContext);
            WebViewUtil.generalSetting(webView);
            sAvailable.add(webView);
        }
    }

    /**
     * 获取webview
     *
     * @return 当没有初始化的时候，返回null
     */
    public WebView getWebView() {
        synchronized (sLock) {
            WebView webView;
            if (sContext == null)
                return null;
            if (sAvailable.size() > 0) {
                webView = sAvailable.get(0);
                sAvailable.remove(0);
                sInUse.add(webView);
            } else {
                webView = new DWebView(sContext);
                sInUse.add(webView);
            }
            return webView;
        }
    }


    /**
     * 回收webview
     *
     * @param webView 需要被回收的webview
     */
    public void removeWebView(WebView webView) {
        webView.loadUrl("");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.clearHistory();
        synchronized (sLock) {
            sInUse.remove(webView);
            if (sAvailable.size() < sMaxSize) {
                sAvailable.add(webView);
            } else {
                webView = null;
            }
        }
    }

    /**
     * 设置webview池个数
     *
     * @param size webview池个数
     */
    public void setMaxPoolSize(int size) {
        synchronized (sLock) {
            sMaxSize = size;
        }
    }

}