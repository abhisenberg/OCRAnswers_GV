package com.abheisenberg.ocranswers_gv;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class WebViewService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private WebView webView;

    private static final String gQuery = "https://www.google.com/search?q=";

    private static final String TAG = WebViewService.class.getSimpleName();

    public WebViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //**************** CODE TO INITIALIZE THE OVERLAY-WINDOW ************************
        floatingView = LayoutInflater.from(this).inflate(R.layout.service_webview
                ,null);


        int height = (int) getResources().getDimension(R.dimen.webview_height);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                height,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        ImageView closeBtn = floatingView.findViewById(R.id.iv_crossicon_wv);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        //**************** INITIALIER CODE ENDS HERE ********************************

        //********** Code for webview **************
        webView = floatingView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.loadUrl(gQuery+"apple");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatingView != null) windowManager.removeView(floatingView);
    }
}
