package org.jun1or.dwebview_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.jun1or.dwebview.fragment.WebFragment;
import org.jun1or.dwebview.webview.DWebView;

/**
 * 测试添加项目自定义通信方法
 */
public class AddJSObjectFragment extends WebFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DWebView.setWebContentsDebuggingEnabled(true);
        mWebViewWrapper.getWebView().addJavascriptObject(new JsApi(), null);
        mWebViewWrapper.getWebView().addJavascriptObject(new JsEchoApi(), "echo");
    }
}
