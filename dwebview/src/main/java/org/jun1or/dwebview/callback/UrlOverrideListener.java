package org.jun1or.dwebview.callback;

public interface UrlOverrideListener {

    /**
     * 重载
     *
     * @param url
     */
    void shouldOverrideUrlLoadingHappened(String url);

    /**
     * 页面结束
     *
     * @param url
     */
    void onPageFinishedHappened(String url);

}
