package org.jun1or.dwebview_android;

import android.util.Log;
import android.webkit.JavascriptInterface;


import org.jun1or.dwebview.callback.CompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by du on 16/12/31.
 */

public class JsEchoApi {


    @JavascriptInterface
    public Object syn1(Object a) throws JSONException {
        Log.e("TAG", "syn1=====>args:NULL" + a);
        return new JSONObject();
    }

    @JavascriptInterface
    public Object syn(Object args) throws JSONException {
        Log.e("TAG", "args:" + args);
        return args;
    }

    @JavascriptInterface
    public void asyn(Object args, CompletionHandler handler) {
        handler.complete(args);
    }
}