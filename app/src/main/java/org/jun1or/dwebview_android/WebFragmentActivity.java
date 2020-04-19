package org.jun1or.dwebview_android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.jun1or.dwebview.callback.OnWebViewListener;
import org.jun1or.dwebview.fragment.WebFragment;
import com.istrong.dwebview_android.R;

public class WebFragmentActivity extends AppCompatActivity implements OnWebViewListener {

    private JWebFragment mWebFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webfragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mWebFragment = new JWebFragment();
        Bundle bundle = new Bundle();
//        bundle.putString(WebFragment.URL, "http://strongmobile.b0.upaiyun.com/Products/Q_Signin/signin.apk");
//        bundle.putString(WebFragment.URL, "https://www.cnblogs.com/cuishuang/p/5728529.html");
//        bundle.putString(WebFragment.URL, "http://zh.hainan.gov.cn/app/index.html#/flood-situation");
        bundle.putString(WebFragment.URL, getIntent().getStringExtra("url"));
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


    public void btnRefreshClick(View view) {
        mWebFragment.reload();
    }

    @Override
    public void onReceiveTitle(String title) {
//        Log.e("TAG", "=======" + title);
    }
}
