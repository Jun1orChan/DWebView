package org.jun1or.dwebview.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;


import com.istrong.util.AppUtil;
import com.istrong.util.SPUtil;

import static android.content.Context.DOWNLOAD_SERVICE;

public class WebViewDownloadCompleteReceiver extends BroadcastReceiver {

    public static final String WEBVIEW_DOWNLOADID = "webview_downloadId";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
        if (intent != null) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                long webviewDownloadId = -1;
                try {
                    webviewDownloadId = Long.parseLong(SPUtil.get(context, WEBVIEW_DOWNLOADID, -1l).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (webviewDownloadId != downloadId) {
                    return;
                }
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//                String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
//                Log.e("TAG", "getMimeTypeForDownloadedFile:" + type);
//                if (TextUtils.isEmpty(type)) {
//                    type = "*/*";
//                }
                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
//                Log.e("UriForDownloadedFile:{}", uri.toString());
                if (uri == null)
                    return;
                String path = AppUtil.getRealPathFromURI(context, uri);
                if (TextUtils.isEmpty(path))
                    return;
                AppUtil.openFile(context, path, context.getPackageName() + ".fileprovider");
            }
        }
    }
}