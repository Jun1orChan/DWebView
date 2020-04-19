package org.jun1or.dwebview.callback;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

public interface WebViewActionHappenListener {

    void onReceivedTitleHappened(String title);

    void onReceivedHttpErrorHappened(WebResourceRequest request, WebResourceResponse errorResponse);

    void onReceivedErrorHappened(WebResourceRequest request, WebResourceError error);

    void onReceivedErrorHappened(int errorCode, String description, String failingUrl);

    void onPageStartedHappened(String url, Bitmap favicon);

    void onPageFinishedHappened(String url);
}