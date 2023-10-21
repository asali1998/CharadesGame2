package com.example.sihengli.charadesgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class StandardGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_game);

        //hideNavigationBar();
        Button classicButton = findViewById(R.id.classicBtn);
        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StandardGameActivity.this, WordActivity.class);
                i.putExtra("type", "standard");
                i.putExtra("title", "classics");
                startActivity(i);
            }
        });
        Button moviesButton = findViewById(R.id.moviesBtn);
        moviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StandardGameActivity.this, WordActivity.class);
                i.putExtra("type", "standard");
                i.putExtra("title", "movies");
                startActivity(i);
            }
        });
        Button popButton = findViewById(R.id.popBtn);
        popButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StandardGameActivity.this, WordActivity.class);
                i.putExtra("type", "standard");
                i.putExtra("title", "pop");
                startActivity(i);
            }
        });
        Button animalsButton = findViewById(R.id.animalsBtn);
        animalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StandardGameActivity.this, WordActivity.class);
                i.putExtra("type", "standard");
                i.putExtra("title", "animals");
                startActivity(i);
            }
        });
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
