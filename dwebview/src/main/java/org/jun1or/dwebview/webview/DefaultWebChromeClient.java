package org.jun1or.dwebview.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import org.jun1or.dwebview.callback.OpenFileChooserCallback;
import org.jun1or.dwebview.callback.WebViewActionHappenListener;

public class DefaultWebChromeClient extends WebChromeClient {

    private WebChromeClient mWebChromeClient;

    private OpenFileChooserCallback mOpenFileChooserCallback;

    private WebViewActionHappenListener mWebViewActionHappenListener;


    public void setWebChromeClient(WebChromeClient webChromeClient) {
        mWebChromeClient = webChromeClient;
    }

    public void setOpenFileChooserCallback(OpenFileChooserCallback openFileChooserCallback) {
        mOpenFileChooserCallback = openFileChooserCallback;
    }

    public void setWebViewActionHappenListener(WebViewActionHappenListener webViewActionHappenListener) {
        this.mWebViewActionHappenListener = webViewActionHappenListener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onProgressChanged(view, newProgress);
        } else {
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mWebViewActionHappenListener != null)
            mWebViewActionHappenListener.onReceivedTitleHappened(title);
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedTitle(view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedIcon(view, icon);
        } else {
            super.onReceivedIcon(view, icon);
        }
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
        } else {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onShowCustomView(view, callback);
        } else {
            super.onShowCustomView(view, callback);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onShowCustomView(View view, int requestedOrientation,
                                 CustomViewCallback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
        } else {
            super.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    @Override
    public void onHideCustomView() {
        if (mWebChromeClient != null) {
            mWebChromeClient.onHideCustomView();
        } else {
            super.onHideCustomView();
        }
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onCreateWindow(view, isDialog,
                    isUserGesture, resultMsg);
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onRequestFocus(view);
        } else {
            super.onRequestFocus(view);
        }
    }

    @Override
    public void onCloseWindow(WebView window) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onCloseWindow(window);
        } else {
            super.onCloseWindow(window);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
        result.confirm();
        if (mWebChromeClient != null) {
            if (mWebChromeClient.onJsAlert(view, url, message, result)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               final JsResult result) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onJsConfirm(view, url, message, result);
        }
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, final String message,
                              String defaultValue, final JsPromptResult result) {

        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsBeforeUnload(view, url, message, result);
        } else
            return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onJsBeforeUnload(view, url, message, result);
        }
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                        long estimatedDatabaseSize,
                                        long totalQuota,
                                        WebStorage.QuotaUpdater quotaUpdater) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                    estimatedDatabaseSize, totalQuota, quotaUpdater);
        } else {
            super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                    estimatedDatabaseSize, totalQuota, quotaUpdater);
        }
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        } else {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mWebChromeClient != null) {
            mWebChromeClient.onGeolocationPermissionsHidePrompt();
        } else {
            super.onGeolocationPermissionsHidePrompt();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequest(PermissionRequest request) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onPermissionRequest(request);
        } else {
            super.onPermissionRequest(request);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (mWebChromeClient != null) {
            mWebChromeClient.onPermissionRequestCanceled(request);
        } else {
            super.onPermissionRequestCanceled(request);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (mWebChromeClient != null) {
            return mWebChromeClient.onConsoleMessage(consoleMessage);
        }
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mWebChromeClient != null) {
            return mWebChromeClient.getDefaultVideoPoster();
        }
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (mWebChromeClient != null) {
            return mWebChromeClient.getVideoLoadingProgressView();
        }
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.getVisitedHistory(callback);
        } else {
            super.getVisitedHistory(callback);
        }
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if (mOpenFileChooserCallback != null)
            mOpenFileChooserCallback.openFileChooserCallBack(uploadMsg, acceptType, capture);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        if (mOpenFileChooserCallback != null)
            mOpenFileChooserCallback.openFileChooserCallBack(filePathCallback, fileChooserParams);
        return true;
    }
}
