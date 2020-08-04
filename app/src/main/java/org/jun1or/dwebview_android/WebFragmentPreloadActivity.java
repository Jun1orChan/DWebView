package org.jun1or.dwebview_android;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.jun1or.dwebview.fragment.WebFragment;


public class WebFragmentPreloadActivity extends AppCompatActivity {

    private PreloadWebFragment mWebFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webfragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mWebFragment = new PreloadWebFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(WebFragment.URL, "http://strongmobile.b0.upaiyun.com/Products/Q_Signin/signin.apk");
//        bundle.putString(WebFragment.URL, "https://www.cnblogs.com/cuishuang/p/5728529.html");
//        bundle.putString(WebFragment.URL, "http://zh.hainan.gov.cn/app/index.html#/flood-situation");
        bundle.putString(WebFragment.URL, getIntent().getStringExtra(WebFragment.URL));
        bundle.putInt(WebFragment.PROGRESSBAR_COLOR, Color.RED);
        mWebFragment.setArguments(bundle);
        ft.add(R.id.flContainer, mWebFragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        if (mWebFragment.canGoBack()) {
            mWebFragment.goBack();
            return;
        }
        super.onBackPressed();
    }
}
