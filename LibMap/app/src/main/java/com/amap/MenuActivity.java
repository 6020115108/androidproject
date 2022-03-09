package com.amap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.loc.diagnose.R;
import com.amap.loc.diagnose.demo.MainDiagnoseActivity;
import com.amap.location.demo.StartActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_menu);

        findViewById(R.id.btn_location).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, MainDiagnoseActivity.class));
            }
        });

        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, StartActivity.class));
            }
        });
    }
}
