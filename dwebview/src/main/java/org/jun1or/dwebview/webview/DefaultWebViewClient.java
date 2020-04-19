package org.jun1or.dwebview.webview;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jun1or.dwebview.callback.UrlOverrideListener;
import org.jun1or.dwebview.callback.WebViewActionHappenListener;

public class DefaultWebViewClient extends WebViewClient {

    private WebViewClient mProxyWebViewClient;
    private WebViewActionHappenListener mWebViewActionHappenListener;

    private UrlOverrideListener mUrlOverrideListener;

    public void setProxyWebViewClient(WebViewClient webViewClient) {
        this.mProxyWebViewClient = webViewClient;
    }

    public void setWebViewActionHappenListener(WebViewActionHappenListener webViewActionHappenListener) {
        this.mWebViewActionHappenListener = webViewActionHappenListener;
    }

    public void setUrlOverrideListener(UrlOverrideListener urlOverrideListener) {
        this.mUrlOverrideListener = urlOverrideListener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mUrlOverrideListener != null)
            mUrlOverrideListener.shouldOverrideUrlLoadingHappened(url);
        if (mProxyWebViewClient != null)
            return mProxyWebViewClient.shouldOverrideUrlLoading(view, url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        Log.e("TAG", "onPageStarted======" + url);
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onPageStartedHappened(url, favicon);
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onPageStarted(view, url, favicon);
            return;
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
//        Log.e("TAG", "onPageFinished========" + url);
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onPageFinishedHappened(url);
        if (mUrlOverrideListener != null)
            mUrlOverrideListener.onPageFinishedHappened(url);
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onPageFinished(view, url);
            return;
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onLoadResource(view, url);
            return;
        }
        super.onLoadResource(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPageCommitVisible(WebView view, String url) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onPageCommitVisible(view, url);
            return;
        }
        super.onPageCommitVisible(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        Log.e("TAG", "before 21===========" + url);
        if (mProxyWebViewClient != null) {
            return mProxyWebViewClient.shouldInterceptRequest(view, url);
        }
        return super.shouldInterceptRequest(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        Log.e("TAG", "after 21===========" + request);
        if (mProxyWebViewClient != null) {
            return mProxyWebViewClient.shouldInterceptRequest(view, request);
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
            return;
        }
        super.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onReceivedErrorHappened(errorCode, description, failingUrl);
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
            return;
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onReceivedErrorHappened(request, error);
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedError(view, request, error);
            return;
        }
        super.onReceivedError(view, request, error);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onReceivedHttpErrorHappened(request, errorResponse);
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedHttpError(view, request, errorResponse);
            return;
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onFormResubmission(view, dontResend, resend);
            return;
        }
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.doUpdateVisitedHistory(view, url, isReload);
            return;
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedSslError(view, handler, error);
            return;
        }
        super.onReceivedSslError(view, handler, error);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedClientCertRequest(view, request);
            return;
        }
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
            return;
        }
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (mProxyWebViewClient != null) {
            return mProxyWebViewClient.shouldOverrideKeyEvent(view, event);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onUnhandledKeyEvent(view, event);
            return;
        }
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onScaleChanged(view, oldScale, newScale);
            return;
        }
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
        if (mProxyWebViewClient != null) {
            mProxyWebViewClient.onReceivedLoginRequest(view, realm, account, args);
            return;
        }
        super.onReceivedLoginRequest(view, realm, account, args);
    }
}
