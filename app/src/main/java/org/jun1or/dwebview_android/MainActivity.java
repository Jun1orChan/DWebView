package org.jun1or.dwebview_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.jun1or.dwebview.fragment.WebFragment;
import org.jun1or.dwebview.webview.DWebView;
import com.istrong.dwebview_android.R;
import com.istrong.dwebview_android.glide.GlideApp;
import com.istrong.imgsel.ISNav;
import com.istrong.imgsel.callback.ImageLoader;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long time = System.currentTimeMillis();
//        Log.e("TAG", "time===" + time);
//        DWebView dWebView = new DWebView(getApplicationContext());
//        Log.e("TAG", "spend time===" + (System.currentTimeMillis() - time));
//
//        long time2 = System.currentTimeMillis();
//        Log.e("TAG", "time2===" + time);
//        DWebView dWebView2 = new DWebView(getApplicationContext());
//        Log.e("TAG", "spend time2===" + (System.currentTimeMillis() - time2));
        DWebView.setWebContentsDebuggingEnabled(true);
        WebFragment.setAllowOnPauseExecuteJs(true);
        setContentView(R.layout.activity_main);
        findViewById(R.id.callJs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CallJavascriptActivity.class));
            }
        });
        findViewById(R.id.callNative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JavascriptCallNativeActivity.class));
            }
        });

        findViewById(R.id.commontest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebFragmentActivity.class);
                intent.putExtra("url", "file:///android_asset/test.html");
                startActivity(intent);
            }
        });
        findViewById(R.id.errorTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebFragmentActivity.class);
                intent.putExtra("url", "https://m.dcdapp.com/motor/inapp/car_classify/index.html?native_open_camera=1&hide_bar=1");
//                intent.putExtra("url", "http://fzlpld.strongsoft.net:8056/webmobile/#/menu");
//                intent.putExtra("url", "http://202.109.200.36:50053/webmobile/index.html#/project");
                startActivity(intent);
            }
        });

//        findViewById(R.id.noPreload).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                Flowable.interval(1, 1, TimeUnit.SECONDS)
//                        .subscribeOn(Schedulers.io())
//                        .take(3)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Long>() {
//                            @Override
//                            public void accept(Long aLong) throws Exception {
////                                Log.e("TAG", "===" + aLong);
//                                ((Button) v).setText(((Button) v).getText().toString() + aLong);
//                                if (aLong != 2)
//                                    return;
//                                Intent intent = new Intent(MainActivity.this, WebFragmentActivity.class);
////                                intent.putExtra("url", "http://zh.hainan.gov.cn/app/index.html#/flood-situation");
//                                intent.putExtra("url", "http://61.145.9.103:50054/masterTest/rivermanage/mobile/TaskList.html?manageid=0");
//                                startActivity(intent);
//                            }
//                        });
//            }
//        });
//        findViewById(R.id.preload).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                PreloadWebFragment.sWebViewWrapper = new WebViewWrapper(MainActivity.this);//
////                PreloadWebFragment.sWebViewWrapper.getWebView().loadUrl("http://47.94.240.212:8027/#/actual-rain");
//                PreloadWebFragment.sWebViewWrapper.getWebView().loadUrl("http://zh.hainan.gov.cn/app/index.html#/flood-situation");
//                WebViewUtil.generalSetting(PreloadWebFragment.sWebViewWrapper.getWebView());
//                Flowable.interval(1, 1, TimeUnit.SECONDS)
//                        .subscribeOn(Schedulers.io())
//                        .take(3)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Long>() {
//                            @Override
//                            public void accept(Long aLong) throws Exception {
//                                Log.e("TAG", "===" + aLong);
//                                ((Button) v).setText(((Button) v).getText().toString() + aLong);
//                                if (aLong != 2)
//                                    return;
//                                Intent intent = new Intent(MainActivity.this, WebFragmentPreloadActivity.class);
////                                intent.putExtra(WebFragment.URL, "http://zh.hainan.gov.cn/app/index.html#/flood-situation");
//                                startActivity(intent);
//                            }
//                        });
//            }
//        });

        findViewById(R.id.normalWebView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NormalWebViewActivity.class);
                startActivity(intent);
            }
        });

        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                GlideApp.with(context).load(path).into(imageView);
            }
        });
    }
}
