package org.jun1or.dwebview.callback;

public interface UrlOverrideListener {
    void shouldOverrideUrlLoadingHappened(String url);

    void onPageFinishedHappened(String url);

}
