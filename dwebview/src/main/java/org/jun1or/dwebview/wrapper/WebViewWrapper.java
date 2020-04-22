package org.jun1or.dwebview.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.jun1or.dwebview.R;
import org.jun1or.dwebview.callback.WebViewActionHappenListener;
import org.jun1or.dwebview.webview.DWebView;

import java.util.HashSet;
import java.util.Set;

public class WebViewWrapper extends FrameLayout implements View.OnClickListener, WebViewActionHappenListener, WebHorizenProgressBar.OnProgressStopFinishedListener {

    private static final String ERROR_URL_START = "data:";
    private static final String ERROR_INTERNET_DISCONNECTED = "net::ERR_INTERNET_DISCONNECTED";
    private DWebView mWebView;
    private WebHorizenProgressBar mWebHorizenProgressBar;
    private LinearLayout mLlErrorContainer;

    /**
     * 缓存当前出现错误的页面
     */
    private Set<String> mErrorUrlsSet = new HashSet<>();

    /**
     * 缓存等待加载完成的页面 onPageStart()执行之后 ，onPageFinished()执行之前
     */
    private Set<String> mWaittingFinishSet = new HashSet<>();

    public WebViewWrapper(@NonNull Context context) {
        this(context, null);
    }

    public WebViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStypeAttr) {
        //添加布局
        LayoutInflater.from(context).inflate(R.layout.dwebview_view_web, this, true);
        FrameLayout webViewContainer = (FrameLayout) findViewById(R.id.flContainer);
        mLlErrorContainer = (LinearLayout) findViewById(R.id.llErrorContainer);
        findViewById(R.id.tvRefresh).setOnClickListener(this);
        mWebHorizenProgressBar = (WebHorizenProgressBar) findViewById(R.id.progressBar);
        mWebHorizenProgressBar.setOnProgressStopFinishedListener(this);
        mWebView = new DWebView(getContext());
        mWebView.setWebViewActionHappenListener(this);
        webViewContainer.addView(mWebView,
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }


    public WebHorizenProgressBar getWebHorizenProgressBar() {
        return mWebHorizenProgressBar;
    }

    public DWebView getWebView() {
        return mWebView;
    }

    public boolean canGoBack() {
        if (TextUtils.isEmpty(mWebView.getUrl()) || mWebView.getUrl().startsWith(ERROR_URL_START)) {
            return false;
        }
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public void removeWebView() {
        if (mWebView == null)
            return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
        } else {
            mWebView.removeAllViews();
            mWebView.destroy();
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvRefresh) {
            mWebView.reload();
        }
    }

    private void setErrorStatus(String url) {
        mErrorUrlsSet.add(url);
        mLlErrorContainer.setVisibility(View.VISIBLE);
    }


    private synchronized void hideErrorLayout() {
        mLlErrorContainer.setVisibility(View.GONE);
    }

    @Override
    public void onReceivedTitleHappened(String title) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (title.contains("404") || title.contains("500") || title.contains("Error") || title.contains("error")) {
                //加载错误界面
                setErrorStatus(mWebView.getUrl());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpErrorHappened(WebResourceRequest request, WebResourceResponse errorResponse) {
//        Log.e("TAG", "onReceivedHttpErrorHappened:description:" + errorResponse.getStatusCode() + "\nfailingUrl:" + request.getUrl());
        if (shouldIgnore(request)) {
            return;
        }
        //        Log.e("TAG", "onReceivedHttpErrorHappened:");
        // 这个方法在6.0才出现
        int statusCode = errorResponse.getStatusCode();
//            Log.e("TAG", "=======statusCode:" + statusCode);
        if (request.isForMainFrame()) {
            if (statusCode < 200 || statusCode >= 400) {
                setErrorStatus(request.getUrl() + "");
            }
        }
    }

    private boolean shouldIgnore(WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String url = request.getUrl().toString();
            if (url.contains(".ico") || url.contains(".json") || url.contains(".js") || url.contains(".css") || url.contains(".xml")) {
                return true;
            }
        }
        return false;
    }

    //6.0版本
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedErrorHappened(WebResourceRequest request, WebResourceError error) {
//        Log.e("TAG", "onReceivedErrorHappened:description:" + error.getDescription() + "\nfailingUrl:" + request.getUrl());
        if (request.isForMainFrame()//避免内嵌iframe导致的显示错误界面
            /*|| error.getDescription().toString().equals(ERROR_INTERNET_DISCONNECTED)*/) {
            // 显示错误界面
            setErrorStatus("" + request.getUrl());
        }
    }

    @Override
    public void onReceivedErrorHappened(int errorCode, String description, String failingUrl) {
//        Log.e("TAG", "description:" + description + "\nfailingUrl:" + failingUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        if (failingUrl.equals(mWebView.getUrl()) || description.equals(ERROR_INTERNET_DISCONNECTED)) {
            // 显示错误界面
            setErrorStatus(failingUrl);
        }
    }

    @Override
    public void onPageStartedHappened(String url, Bitmap favicon) {
//        Log.e("TAG", "onPageStartedHappened===" + url);
        if (!mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.add(url);
        }
        if (!url.startsWith(ERROR_URL_START))
            mWebHorizenProgressBar.start();
    }

    @Override
    public void onPageFinishedHappened(String url) {
//        Log.e("TAG", "onPageFinishedHappened=============" + url);
        mWebHorizenProgressBar.stop();
        if (!mErrorUrlsSet.contains(url) && mWaittingFinishSet.contains(url)) {
            hideErrorLayout();
        }
        if (mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.remove(url);
        }
        if (!mErrorUrlsSet.isEmpty()) {
            mErrorUrlsSet.clear();
        }
    }

    @Override
    public void onProgressStopFinished() {
//        Log.e("TAG", "=========onProgressStopFinished");
//        if (mIsError) {
//            showErrorLayout();
//            mIsError = false;
//        }
    }
}
