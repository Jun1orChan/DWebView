package org.jun1or.dwebview.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import org.jun1or.dialog.ListDialog;
import org.jun1or.dialog.MaterialDialog;
import org.jun1or.dialog.listener.OnItemClickListener;
import org.jun1or.dwebview.R;
import org.jun1or.dwebview.callback.JavascriptCloseWindowListener;
import org.jun1or.dwebview.callback.OnWebViewListener;
import org.jun1or.dwebview.callback.OpenFileChooserCallback;
import org.jun1or.dwebview.wrapper.WebViewUtil;
import org.jun1or.dwebview.wrapper.WebViewWrapper;
import org.jun1or.util.AppUtil;
import org.jun1or.util.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 承载WebView
 */
public class WebFragment extends Fragment implements JavascriptCloseWindowListener, OpenFileChooserCallback, DownloadListener {

    //默认不允许
    private static boolean sAllowOnPauseExecuteJs = false;
    public static final int REQUEST_CODE_ALL = 0x2001;
    public static final int REQUEST_CODE_TAKEAUDIO = 0x3001;
    public static final int REQUEST_CODE_TAKEPHOTO = 0x4001;
    public static final int REQUEST_CODE_TAKEVIDEO = 0x5001;

    public static final String URL = "url";
    public static final String PROGRESSBAR_COLOR = "progressbar_color";

    private final String MIMETYPE_IMAGE = "image/";
    private final String MIMETYPE_VIDEO = "video/";
    private final String MIMETYPE_AUDIO = "audio/";
    private final String CAPTURE_IMAGE = "相机拍照";
    private final String CAPTURE_VIDEO = "录制视频";
    private final String CAPTURE_AUDIO = "音频录制";
    private final String FROM_ALBUM = "手机相册";

    private ValueCallback<Uri> mFilePathCallback;
    private ValueCallback<Uri[]> mFilePathCallbackForAndroid5;

    protected WebViewWrapper mWebViewWrapper;
    private Context mContext;

    private File mTakePhotoFile = null;
    private ListDialog mTypeChoiceDialog;
    private boolean mIsChoice;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dwebview_fragment_web, null, false);
        initViews(viewGroup);
        return viewGroup;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(ViewGroup viewGroup) {
        mWebViewWrapper = getWebViewWrapper();
        if (mWebViewWrapper == null) {
            mWebViewWrapper = new WebViewWrapper(mContext);
            WebViewUtil.generalSetting(mWebViewWrapper.getWebView());
            if (!TextUtils.isEmpty(getArguments().getString(URL))) {
                mWebViewWrapper.getWebView().loadUrl(getArguments().getString(URL));
            }
        }
        viewGroup.addView(mWebViewWrapper);
        mWebViewWrapper.getWebView().setOpenFileChooserCallback(this);
        mWebViewWrapper.getWebView().setJavascriptCloseWindowListener(this);
        mWebViewWrapper.getWebView().setWebChromeClient(mWebChromeClient);
        mWebViewWrapper.getWebView().setWebViewClient(mWebViewClient);
        mWebViewWrapper.getWebView().setDownloadListener(this);
        if (getArguments().getInt(PROGRESSBAR_COLOR, -1) != -1) {
            mWebViewWrapper.getWebHorizenProgressBar().setColor(getArguments().getInt(PROGRESSBAR_COLOR));
        }
    }

    public WebViewWrapper getWebViewWrapper() {
        return null;
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            //提示用户
            final MaterialDialog sslErrorTipsDialog = new MaterialDialog();
            sslErrorTipsDialog.content(view.getContext().getString(R.string.dwebview_ssl_error_tips))
                    .btnText(view.getContext().getString(R.string.dwebview_cancel), view.getContext().getString(R.string.dwebview_ok))
                    .btnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sslErrorTipsDialog.dismiss();
                            handler.cancel();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sslErrorTipsDialog.dismiss();
                            //信任所有证书
                            handler.proceed();
                        }
                    })
                    .show(getChildFragmentManager());
        }
    };


    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        private View mVideoView = null;
        private CustomViewCallback mCustomViewCallback = null;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
//            Log.e("TAG", "progress=====" + newProgress);
//            if (newProgress == 100) {
//                mJWebHorizenProgressBar.stop();
//            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            showTipsDialog(message);
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            showConfirmDialog(message, result);
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            callback.invoke(origin, true, true);
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            callback.invoke(origin, false, true);
                            showPermissionDeniedDialog(String.format(mContext.getString(R.string.dwebview_location_permission_denied_tips), AppUtil.getAppName(getActivity()), AppUtil.getAppName(getActivity())));
                        }
                    }).start();

//            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mCustomViewCallback != null) {
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
                return;
            }
            ViewGroup viewGroup = (ViewGroup) mWebViewWrapper.getWebView().getParent();
            viewGroup.removeView(mWebViewWrapper.getWebView());
            viewGroup.addView(view);
            mVideoView = view;
            mCustomViewCallback = callback;
            mWebChromeClient = this;
        }

        @Override
        public void onHideCustomView() {
            if (mVideoView != null) {
                if (mCustomViewCallback != null) {
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;
                    ViewGroup viewGroup = (ViewGroup) mVideoView.getParent();
                    viewGroup.removeView(mVideoView);
                    viewGroup.addView(mWebViewWrapper.getWebView());
                }
                mVideoView = null;
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title.startsWith("data:text/html")) {
                return;
            }
            if (getActivity() != null && getActivity() instanceof OnWebViewListener) {
                ((OnWebViewListener) getActivity()).onReceiveTitle(title);
            }
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }
    };

    private void showTipsDialog(String msg) {
        if (getActivity() == null) {
            return;
        }
        MaterialDialog tipsDialog = new MaterialDialog();
        tipsDialog
                .content(msg)
                .btnText(getString(R.string.dwebview_ok))
                .show(getChildFragmentManager());
    }

    private void showConfirmDialog(String message, final JsResult result) {
        if (getActivity() == null) {
            return;
        }
        final MaterialDialog confirmDialog = new MaterialDialog();
        confirmDialog
                .content(message)
                .btnText(getString(R.string.dwebview_cancel), getString(R.string.dwebview_ok))
                .btnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        result.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        result.confirm();
                    }
                })
                .show(getChildFragmentManager());
    }

    public boolean canGoBack() {
        if (mWebViewWrapper == null) {
            return false;
        } else {
            return mWebViewWrapper.canGoBack();
        }
    }

    public void goBack() {
        if (mWebViewWrapper != null) {
            mWebViewWrapper.goBack();
        }
//        Log.e("TAG", mWebViewWrapper.getWebView().copyBackForwardList().getSize() + "=====");
    }

    @Override
    public void onDestroyView() {
//        if (getActivity() != null)
//            getActivity().unregisterReceiver(mWebViewDownloadCompleteReceiver);
        if (mWebViewWrapper != null) {
            mWebViewWrapper.removeWebView();
        }
        mMainHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    @Override
    public boolean onClose() {
        if (getActivity() != null) {
            getActivity().finish();
        }
        return false;
    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
        mFilePathCallback = filePathCallback;
        acceptType = acceptType.replace(",", ";");
        actionByAcceptType(acceptType, !TextUtils.isEmpty(capture));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void openFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        mFilePathCallbackForAndroid5 = filePathCallback;
        String acceptType = "";
        if (fileChooserParams.getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length > 0) {
            for (int i = 0; i < fileChooserParams.getAcceptTypes().length; i++) {
                if (TextUtils.isEmpty(fileChooserParams.getAcceptTypes()[i])) {
                    continue;
                }
                if (!TextUtils.isEmpty(acceptType)) {
                    acceptType += ";" + fileChooserParams.getAcceptTypes()[i];
                } else {
                    acceptType = fileChooserParams.getAcceptTypes()[i];
                }
            }
        }
        actionByAcceptType(acceptType, fileChooserParams.isCaptureEnabled());
    }

    /**
     * 为了保持5.0以下的文件选择，统一只能进行单选
     *
     * @param acceptType
     */
    private void actionByAcceptType(String acceptType, boolean captureEnabled) {
//        Log.e("TAG", "=====" + acceptType);
        if (TextUtils.isEmpty(acceptType)) {
            acceptType = "*/*";
        }
        List<String> itemList = new ArrayList<>();
        if (acceptType.contains("*/*")) {
            itemList.add(CAPTURE_IMAGE);
            itemList.add(CAPTURE_VIDEO);
            itemList.add(CAPTURE_AUDIO);
        }
        if (acceptType.contains(MIMETYPE_IMAGE)) {
            if (!itemList.contains(CAPTURE_IMAGE)) {
                itemList.add(CAPTURE_IMAGE);
            }
        }
        if (acceptType.contains(MIMETYPE_VIDEO)) {
            if (!itemList.contains(CAPTURE_VIDEO)) {
                itemList.add(CAPTURE_VIDEO);
            }
        }
        if (acceptType.contains(MIMETYPE_AUDIO)) {
            if (!itemList.contains(CAPTURE_AUDIO)) {
                itemList.add(CAPTURE_AUDIO);
            }
        }
        if (itemList.size() == 0) {
            startFileChooser(acceptType, REQUEST_CODE_ALL);
            return;
        }
        if (captureEnabled) {
            captureAction(itemList);
        } else {
            choiceAction(itemList, acceptType);
        }
    }


    private void captureAction(final List<String> itemList) {
        if (itemList.size() == 1) {
            //直接启动对应的录制界面
            startCapture(itemList.get(0));
        } else {
            //多种类型
            final OnItemClickListener onItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClick(int i) {
                    mIsChoice = true;
                    startCapture(itemList.get(i));
                    if (mTypeChoiceDialog != null) {
                        mTypeChoiceDialog.dismiss();
                    }
                }
            };
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    showTypeChoiceDialog(itemList.toArray(new CharSequence[itemList.size()]), onItemClickListener);
                }
            });
        }
    }

    private void startCapture(String captureName) {
        if (captureName.contains(CAPTURE_IMAGE)) {
            requestPermission(getTakePhotoRunnable(), String.format(getString(R.string.dwebview_camera_permission_denied_tips),
                    AppUtil.getAppName(getActivity()), AppUtil.getAppName(getActivity())),
                    Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE);
        } else if (captureName.contains(CAPTURE_VIDEO)) {
            requestPermission(getTakeVideoRunnable(), String.format(getString(R.string.dwebview_video_permission_denied_tips),
                    AppUtil.getAppName(getActivity()), AppUtil.getAppName(getActivity())),
                    Permission.CAMERA, Permission.RECORD_AUDIO, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE);
        } else if (captureName.contains(CAPTURE_AUDIO)) {
            requestPermission(getTakeAudioRunnable(),
                    String.format(getString(R.string.dwebview_audio_permission_denied_tips), AppUtil.getAppName(getActivity()), AppUtil.getAppName(getActivity())),
                    Permission.CAMERA, Permission.RECORD_AUDIO, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void showTypeChoiceDialog(final CharSequence[] itemArray, OnItemClickListener onItemClickListener) {
        if (mTypeChoiceDialog == null) {
            mTypeChoiceDialog = new ListDialog();
        }
        mTypeChoiceDialog.itemArray(itemArray)
                .itemClickListener(onItemClickListener)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (!mIsChoice) {
                            sendFile2Web(null);
                        }
                        mIsChoice = false;
                    }
                });
        mTypeChoiceDialog.show(getChildFragmentManager());
    }

    private void choiceAction(final List<String> itemList, final String acceptType) {
        itemList.add(FROM_ALBUM);
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                mIsChoice = true;
                if (itemList.get(i).equals(FROM_ALBUM)) {
                    //单击了从相册选择
                    startFileChooser(acceptType, REQUEST_CODE_ALL);
                } else {
                    startCapture(itemList.get(i));
                }
                if (mTypeChoiceDialog != null) {
                    mTypeChoiceDialog.dismiss();
                }
            }
        };
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                showTypeChoiceDialog(itemList.toArray(new CharSequence[itemList.size()]), onItemClickListener);
            }
        });
    }

    private Runnable getTakePhotoRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                takePhoto();
            }
        };
    }

    private void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.addCategory(Intent.CATEGORY_DEFAULT);
        File photoFile = new File(StorageUtil.getCacheDirectory(getActivity()).getAbsolutePath() + File.separator + "photos");
        if (!photoFile.exists()) {
            photoFile.mkdirs();
        }
        mTakePhotoFile = new File(photoFile, "IMG" + System.currentTimeMillis() + ".png");
        Uri takePhotoUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            takePhotoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider", mTakePhotoFile);
            captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            takePhotoUri = Uri.fromFile(mTakePhotoFile);
        }
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
        startActivityForResult(captureIntent, REQUEST_CODE_TAKEPHOTO);
    }

    private Runnable getTakeVideoRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                takeVideo();
            }
        };
    }

    private void takeVideo() {
        Intent intent = new Intent();
        try {
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(intent, REQUEST_CODE_TAKEVIDEO);
        } catch (Exception e) {
            sendFile2Web(null);
            e.printStackTrace();
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "打开视频录制失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Runnable getTakeAudioRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                takeAudio();
            }
        };
    }

    private void takeAudio() {
        try {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, REQUEST_CODE_TAKEAUDIO);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "打开录音界面失败！", Toast.LENGTH_SHORT).show();
            sendFile2Web(null);
        }
    }

    private void requestPermission(final Runnable runnable, final String deniedTip, @NonNull @PermissionDef String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        runnable.run();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        sendFile2Web(null);
                        showPermissionDeniedDialog(deniedTip);
                    }
                })
                .start();

    }

    private void startFileChooser(String acceptType, int requestCode) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!TextUtils.isEmpty(acceptType)) {
            i.setType(acceptType);
        }
        if (getActivity() != null) {
            if (AppUtil.isIntentAvailable(getActivity(), i)) {
                startActivityForResult(Intent.createChooser(i, "文件选择"), requestCode);
            } else {
                Toast.makeText(getActivity(), "未找到相关选择器！", Toast.LENGTH_SHORT).show();
                sendFile2Web(null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            sendFile2Web(null);
            return;
        }
        if (requestCode == REQUEST_CODE_ALL || requestCode == REQUEST_CODE_TAKEAUDIO
                || requestCode == REQUEST_CODE_TAKEVIDEO) {
            if (data == null) {
                Toast.makeText(getActivity(), "录音失败！", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();
            sendFile2Web(uri);
        } else if (requestCode == REQUEST_CODE_TAKEPHOTO) {
            sendFile2Web(Uri.fromFile(mTakePhotoFile));
        } else if (requestCode == REQUEST_CODE_TAKEVIDEO) {
            Uri videoUri = data.getData();
            sendFile2Web(videoUri);
        }
    }

    private void sendFile2Web(Uri fileUri) {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(fileUri);
            mFilePathCallback = null;
        } else if (mFilePathCallbackForAndroid5 != null) {
            try {
                if (fileUri != null) {
                    mFilePathCallbackForAndroid5.onReceiveValue(new Uri[]{fileUri});
                } else {
                    mFilePathCallbackForAndroid5.onReceiveValue(null);
                }
                mFilePathCallbackForAndroid5 = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimetype, long contentLength) {
        if (getActivity() == null) {
            return;
        }
        //进行下载提示
        final MaterialDialog downloadConfirmDialog = new MaterialDialog();
        downloadConfirmDialog
                .title("提示")
                .content("是否下载文件：\n" + URLUtil.guessFileName(url, contentDisposition, mimetype))
                .btnText(getString(R.string.dwebview_cancel), "下载")
                .btnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadConfirmDialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadConfirmDialog.dismiss();
                        downloadBySystem(url, contentDisposition, mimetype);
                    }
                }).show(getChildFragmentManager());
    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        /**
         * 目录: Android -> data -> xxx.xxx.xxx -> files -> Download -> dxtj.apk
         * 这个文件是你的应用所专用的,软件卸载后，下载的文件将随着卸载全部被删除
         */
        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, fileName);
        final DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
//        Log.e("downloadId:{}", downloadId + "");
    }

    private void showPermissionDeniedDialog(String tipMsg) {
        if (getActivity() == null) {
            return;
        }
        final MaterialDialog audioPermissionDeniedDialog = new MaterialDialog();
        audioPermissionDeniedDialog
                .content(tipMsg)
                .btnText(getString(R.string.dwebview_btn_text_denied_cancel), getString(R.string.dwebview_btn_text_denied_setting))
                .btnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioPermissionDeniedDialog.dismiss();
                        sendFile2Web(null);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        audioPermissionDeniedDialog.dismiss();
                        AppUtil.goAppDetailsSettings(getActivity());
                    }
                })
                .show(getChildFragmentManager());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebViewWrapper != null) {
            mWebViewWrapper.getWebView().onResume();
            if (!sAllowOnPauseExecuteJs) {
                mWebViewWrapper.getWebView().resumeTimers();
            }
        }
    }

    @Override
    public void onPause() {
        if (mWebViewWrapper != null) {
            mWebViewWrapper.getWebView().onPause();
            if (!sAllowOnPauseExecuteJs) {
                mWebViewWrapper.getWebView().pauseTimers();
            }
        }
        super.onPause();
    }


    public static void setAllowOnPauseExecuteJs(boolean allowOnPauseExecuteJs) {
        sAllowOnPauseExecuteJs = allowOnPauseExecuteJs;
    }
}
