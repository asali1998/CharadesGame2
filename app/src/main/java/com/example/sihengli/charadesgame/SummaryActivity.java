package com.example.sihengli.charadesgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    ArrayList<String> correct;
    ArrayList<String> wrong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        //hideNavigationBar();

        correct = (ArrayList<String>) getIntent().getSerializableExtra("correct");
        wrong = (ArrayList<String>) getIntent().getSerializableExtra("wrong");
        ListView correctListView = findViewById(R.id.correctLV);
        ListView wrongListView = findViewById(R.id.wrongLV);
        ArrayAdapter<String> correctAdapter = new ArrayAdapter<String>(this, R.layout.correct_row, correct);
        correctListView.setAdapter(correctAdapter);
        ArrayAdapter<String> wrongAdapter = new ArrayAdapter<String>(this, R.layout.wrong_row, wrong);
        wrongListView.setAdapter(wrongAdapter);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SummaryActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        });

        TextView score = findViewById(R.id.scoreTxt);
        score.setText(getString(R.string.score_column) + correct.size() + getString(R.string.of) + (correct.size() + wrong.size()));
    }

    @Override
    public void onBackPressed() {
        Intent backHome = new Intent(SummaryActivity.this,MainActivity.class);
        startActivity(backHome);
        finish();
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
