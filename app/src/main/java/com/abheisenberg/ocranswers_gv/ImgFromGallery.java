package com.abheisenberg.ocranswers_gv;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ImgFromGallery extends AppCompatActivity implements View.OnClickListener{

    private static final int IMAGE_REQ_CODE = 1;
    private static final String TAG = ImgFromGallery.class.getSimpleName();
    Button bt_startOCR_ifg, bt_searchWeb_ifg;
    ImageView iv_showImg;
    TextView tv_OCRText_ifg;
    String foundText;
    Bitmap bitpic;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_from_gallery);

        foundText = null;

        bt_startOCR_ifg = findViewById(R.id.bt_startOCR_ifg);
        bt_searchWeb_ifg = findViewById(R.id.bt_searchG_ifg);
        iv_showImg = findViewById(R.id.iv_showImg_ifg);
        tv_OCRText_ifg = findViewById(R.id.tv_OCRText_ifg);

        bt_startOCR_ifg.setOnClickListener(this);
        bt_searchWeb_ifg.setOnClickListener(this);

        selectImage();

        textRecognizer = new TextRecognizer
                .Builder(getApplicationContext())
                .build();

        if(!textRecognizer.isOperational()){
            Log.d(TAG, "OCR dependencies not available ");
        }
    }

    public void selectImage(){
        Intent openGallery = new Intent();
        openGallery.setType("image/*");
        openGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openGallery, IMAGE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQ_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Uri uri = data.getData();
            try {
                bitpic = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), uri);
                iv_showImg.setImageBitmap(bitpic);
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.bt_startOCR_ifg:
                startOCR(bitpic);
                break;

            case R.id.bt_searchG_ifg:
                startGSearch();
                break;
        }
    }

    public void startOCR(Bitmap bitpic){
        Frame pic = new Frame.Builder()
                .setBitmap(bitpic)
                .build();
        if(textRecognizer.isOperational()){
            final SparseArray<TextBlock> items = textRecognizer.detect(pic);
            if(items.size() != 0){
                tv_OCRText_ifg.post(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i=0; i<items.size(); i++){
                            stringBuilder.append(items.valueAt(i).getValue())
                                    .append("\n");
                        }
                        foundText = stringBuilder.toString();
                        tv_OCRText_ifg.setText(foundText);
                    }
                });
            }
        } else Log.d(TAG, "TextRecognizer not available.");
    }

    public void startGSearch(){
        if(TextUtils.isEmpty(foundText)){
            Toast.makeText(this, "Please do OCR first!", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO: Do google search here
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        search.putExtra(SearchManager.QUERY, foundText);
        startActivity(search);
    }
}
