package org.jun1or.dwebview_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.istrong.dwebview_android.R;

public class NormalWebViewActivity extends AppCompatActivity {

    private WebView mWebView;

    private FrameLayout mWebViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        mWebViewContainer = (FrameLayout) findViewById(R.id.webviewContainer);
        mWebView = new WebView(this);
        mWebViewContainer.addView(mWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
//        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setGeolocationEnabled(true);
//        settings.setGeolocationDatabasePath(getCacheDir().getAbsolutePath());
        // 支持通过js打开新的窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath());
        settings.setDomStorageEnabled(true);//开启DOM缓存
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        mWebView.setWebViewClient(mWebViewClient);

        mWebView.setWebChromeClient(mWebChromeClient);
//        mWebView.loadUrl("http://zh.hainan.gov.cn/app/index.html?v=3.6#/flood-situation");
        mWebView.loadUrl("http://58.221.239.201:8086/TGis/view/mobile/Document/noticeDoc_1.html");
//        mWebView.loadUrl("file:///android_asset/1.html");
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return false;
            }
            try {
                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();//信任所有证书
//            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            mWebViewContainer.removeView(mWebView);
            mWebViewContainer.addView(view);
            myView = view;
            myCallback = callback;
            mWebChromeClient = this;
        }

        private View myView = null;
        private CustomViewCallback myCallback = null;


        @Override
        public void onHideCustomView() {
            if (myView != null) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                mWebViewContainer.removeView(myView);
                mWebViewContainer.addView(mWebView);
                myView = null;
            }
        }

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
//            mToolBar.setTitle(title);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String
                                                               origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
//            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    };

}
