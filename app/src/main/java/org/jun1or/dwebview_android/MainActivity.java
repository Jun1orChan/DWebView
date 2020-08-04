package org.jun1or.dwebview_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import org.jun1or.dwebview.fragment.WebFragment;
import org.jun1or.dwebview.webview.DWebView;
import org.jun1or.dwebview_android.glide.GlideApp;
import org.jun1or.imgsel.ISNav;
import org.jun1or.imgsel.callback.ImageLoader;

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
                intent.putExtra("url", "http://www.baidu.com");
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
