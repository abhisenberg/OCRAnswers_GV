package com.abheisenberg.ocranswers_gv;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.abheisenberg.ocranswers_gv.SSFiles.ScreenshotCallback;
import com.abheisenberg.ocranswers_gv.SSFiles.Screenshotter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SSButtonService extends Service {
    private static final String TAG = SSButtonService.class.getSimpleName();

    private static final String path = Environment.getExternalStorageDirectory() + "/Captured/";

    private final IBinder binder = new LocalBinder();
    private WindowManager windowManager;
    private View floatingView;
    private ImageView iv_bt_ss;

    private SSInterface callbacks;

    public class LocalBinder extends Binder {
        SSButtonService getService(){
            return SSButtonService.this;
        }
    }

    public SSButtonService() {

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
        floatingView = LayoutInflater.from(this).inflate(R.layout.service_ssbutton
                ,null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        params.x = 0;
        params.y = 0;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        ImageView closeBtn = floatingView.findViewById(R.id.iv_crossicon_ss);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        //**************** INITIALIER CODE ENDS HERE ********************************

        iv_bt_ss = floatingView.findViewById(R.id.ib_ss);
        iv_bt_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SS Button pressed ");
//                createImage(getBitmapOfRootView(floatingView));

                if(MainActivity.mediaProjData == null){
                    Log.d(TAG, "Intent data is null");
                }

                Screenshotter.getInstance()
                        .setSize(720,1280)
                        .takeScreenshot(getApplicationContext(), -1, MainActivity.mediaProjData, new ScreenshotCallback() {
                            @Override
                            public void onScreenshot(Bitmap bitmap) {
                                Log.d(TAG, "Taking ss ");
                                createImage(bitmap);
                            }
                        });
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatingView != null) windowManager.removeView(floatingView);
    }

    public void setSSInterface(SSInterface callbacks){
        this.callbacks = callbacks;
    }

    private Bitmap getBitmapOfRootView(View v){
        View rootView = v.getRootView();
        rootView.setDrawingCacheEnabled(true);
        Log.d(TAG, "getBitmapOfRootView: ");
        return rootView.getDrawingCache();
    }

    private void createImage(Bitmap bmp){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File picFile = new File(path, Long.toString(System.currentTimeMillis())+".jpg");
        bmp = Bitmap.createBitmap(bmp,0, pixelValue(150), bmp.getWidth(),  180);
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        try {
            Log.d(TAG, "Writing images");
            picFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(picFile);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int pixelValue(int val){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
                getResources().getDisplayMetrics());
        return (int)px;
    }
}

