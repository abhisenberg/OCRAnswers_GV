package com.abheisenberg.ocranswers_gv;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MA";

    Button bt_withoutPic, bt_withPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_withoutPic = findViewById(R.id.bt_WithoutPic);
        bt_withPic = findViewById(R.id.bt_WithPic);

        bt_withPic.setOnClickListener(this);
        bt_withoutPic.setOnClickListener(this);

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
        }
    }
}
