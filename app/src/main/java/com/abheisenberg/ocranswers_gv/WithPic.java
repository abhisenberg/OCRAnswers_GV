package com.abheisenberg.ocranswers_gv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class WithPic extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WithPic";
    private final int cameraPermissionID = 1001;

    CameraSource cameraSource;
    SurfaceView cameraView;
    Button bt_capture_wp;
    TextView tv_OCRText_wp;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_pic);

        cameraView = findViewById(R.id.surface_view_wp);
        bt_capture_wp = findViewById(R.id.bt_capture_wp);
        tv_OCRText_wp = findViewById(R.id.tv_OCRText_wp);

        bt_capture_wp.setOnClickListener(this);

        /*
        First initialize the TextRecognizer, if it is successfully done,
         initialize the CameraSource with it.
         */

        textRecognizer = new TextRecognizer
                .Builder(getApplicationContext())
                .build();

        if (!textRecognizer.isOperational()) {
            Log.d(TAG, "OCR dependencies not available");
        } else {
            cameraSource = new CameraSource
                    .Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(WithPic.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    cameraPermissionID);
                        }
                        Log.d(TAG, "surfaceCreated: Starting camera");
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case cameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(WithPic.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    cameraPermissionID);
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please grant Camera permission from settings!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.bt_capture_wp:
                cameraSource.takePicture(new CameraSource.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        Log.d(TAG, "onShutter: ");
                        Toast.makeText(WithPic.this, "Shutter pressed", Toast.LENGTH_SHORT).show();
                    }
                }, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Log.d(TAG, "onPictureTaken: ");
                        Bitmap bitpic = BitmapFactory.decodeByteArray(bytes
                        ,0, bytes.length);

                        analyzePic(bitpic);
                    }
                });
                break;
        }
    }

    public void analyzePic(Bitmap bitpic) {
        Frame pic = new Frame.Builder()
                .setBitmap(bitpic)
                .build();
        if(textRecognizer.isOperational()){
            final SparseArray<TextBlock> items = textRecognizer.detect(pic);
            if(items.size() != 0){
                tv_OCRText_wp.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i=0; i<items.size(); i++){
                            stringBuilder.append(items.valueAt(i).getValue())
                                    .append("\n");
                        }
                        tv_OCRText_wp.setText(stringBuilder.toString());
                    }
                });
            }
        } else Log.d(TAG, "TextRecognizer not available.");
    }

}
