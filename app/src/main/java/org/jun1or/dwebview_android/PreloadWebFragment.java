package org.jun1or.dwebview_android;


import org.jun1or.dwebview.fragment.WebFragment;
import org.jun1or.dwebview.wrapper.WebViewWrapper;

public class PreloadWebFragment extends WebFragment {
    public static WebViewWrapper sWebViewWrapper;

    @Override
    public WebViewWrapper getWebViewWrapper() {
//        PreloadWebFragment.sWebViewWrapper.setBackgroundColor(Color.BLUE);
        return PreloadWebFragment.sWebViewWrapper;
    }
}
