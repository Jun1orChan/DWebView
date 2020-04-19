package org.jun1or.dwebview.callback;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

public interface OpenFileChooserCallback {
    /**
     * for  < 5.0
     *
     * @param filePathCallback
     * @param acceptType
     * @param capture
     */
    void openFileChooserCallBack(ValueCallback<Uri> filePathCallback, String acceptType, String capture);

    /**
     * for >=5.0
     *
     * @param filePathCallback
     * @param fileChooserParams
     */
    void openFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
}
