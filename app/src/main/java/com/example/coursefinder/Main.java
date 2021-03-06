package com.example.coursefinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Main.this, Login.class); //현재 액티비티, 이동하고 싶은 액티비티
                startActivity(intent);
                finish();
            }
        }, 1500); //딜레이 타임 조절


    }
}
