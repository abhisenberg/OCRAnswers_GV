package com.abheisenberg.ocranswers_gv;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
    SSInterface{

    private static final String TAG = "MA";
    private static final int CODE_DRAW_PERMISSION = 10;
    private static final int CODE_WRITE_EXT_PERMISSION = 11;
    private static final int CODE_MEDIA_PROJ = 12;

    Button bt_withoutPic, bt_withPic, bt_selectImage;
    Button bt_openOverlayW, bt_openOverlayVW, bt_ss;

    private SSButtonService SSService;
    private boolean bound = false;
    static Intent mediaProjData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_ss = findViewById(R.id.bt_startSS);
        bt_withoutPic = findViewById(R.id.bt_WithoutPic);
        bt_withPic = findViewById(R.id.bt_WithPic);
        bt_selectImage = findViewById(R.id.bt_SelectImage);
        bt_openOverlayW = findViewById(R.id.bt_overlaywind);
        bt_openOverlayVW = findViewById(R.id.bt_overlayWebView);

        bt_ss.setOnClickListener(this);
        bt_withPic.setOnClickListener(this);
        bt_withoutPic.setOnClickListener(this);
        bt_selectImage.setOnClickListener(this);
        bt_openOverlayW.setOnClickListener(this);
        bt_openOverlayVW.setOnClickListener(this);

        //For the floating window
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)){
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent, CODE_DRAW_PERMISSION);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.bt_WithoutPic:
                startActivity(new Intent(MainActivity.this, WithoutPic.class));
                break;

            case R.id.bt_WithPic:
                startActivity(new Intent(MainActivity.this, WithPic.class));
                break;

            case R.id.bt_SelectImage:
                startActivity(new Intent(MainActivity.this, ImgFromGallery.class));
                break;

            case R.id.bt_overlaywind:
                initializeView();
                break;

            case R.id.bt_overlayWebView:
                initializeWebView();
                break;

            case R.id.bt_startSS:
                initializeSSView();;
                break;
        }
    }

    private void initializeView(){
        startService(new Intent(MainActivity.this, FloatingViewService.class));
        finish();
    }

    private void initializeWebView(){
        startService(new Intent(MainActivity.this, WebViewService.class));
        finish();
    }

    private void initializeSSView(){
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getSystemService(this.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                CODE_MEDIA_PROJ);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_DRAW_PERMISSION || requestCode == CODE_WRITE_EXT_PERMISSION){
            if(resultCode == RESULT_OK){
                initializeView();
            } else {
                Toast.makeText(this, "Please give required permissions."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == CODE_MEDIA_PROJ){
            if(resultCode == RESULT_OK){
                mediaProjData = data;
                Log.d(TAG, "Is mediaProjData null? "+ (mediaProjData == null));
                startService(new Intent(MainActivity.this, SSButtonService.class));
                finish();
            } else {
                Toast.makeText(this, "Please give required permissions."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            SSButtonService.LocalBinder binder = (SSButtonService.LocalBinder) service;
//            SSService = binder.getService();
//            bound = true;
//            SSService.setSSInterface(MainActivity.this);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            bound = false;
//        }
//    };

    @Override
    public void getSS() {

    }
}
