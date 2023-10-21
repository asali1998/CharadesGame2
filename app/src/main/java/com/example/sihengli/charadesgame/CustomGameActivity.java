package com.example.sihengli.charadesgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomGameActivity extends AppCompatActivity {
    boolean[] setUp = new boolean[4];
    String[] titles = new String[4];
    Button[] customs = new Button[4];
    Button[] deletes = new Button[4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_game);
        SharedPreferences sharedPreferences = getSharedPreferences("Decks", MODE_PRIVATE);
        customs[0] = findViewById(R.id.customOne);
        customs[1] = findViewById(R.id.customTwo);
        customs[2] = findViewById(R.id.customThree);
        customs[3] = findViewById(R.id.customFour);
        deletes[0] = findViewById(R.id.delOne);
        deletes[1] = findViewById(R.id.delTwo);
        deletes[2] = findViewById(R.id.delThree);
        deletes[3] = findViewById(R.id.delFour);


        for(int i=0; i<4; i++){
            titles[i] = sharedPreferences.getString(String.format(getString(R.string.title), i), null);
            setUp[i] = titles[i] != null;
            //Log.d("ahh", "" + setUp[i]);
            if(setUp[i]) {
                customs[i].setText(titles[i] );
                customs[i].setTextColor(getResources().getColor(R.color.black));
                deletes[i].setVisibility(View.VISIBLE);
            }
            else{
                customs[i].setText(R.string.create_custom_deck);
                customs[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                deletes[i].setVisibility(View.INVISIBLE);
            }
            customs[i].setOnClickListener(new MyOnClickListener(i) {
                @Override
                public void onClick(View v) {
                    if(setUp[this.store]) {
                        Intent in = new Intent(CustomGameActivity.this, WordActivity.class);
                        in.putExtra(getString(R.string.type), getString(R.string.custom));
                        in.putExtra(getString(R.string.title2), Integer.toString(this.store));
                        startActivity(in);
                        finish();
                    }
                    else{
                        Intent gameIntent = new Intent(CustomGameActivity.this, CreateDeckActivity.class);
                        gameIntent.putExtra(getString(R.string.deckNum), Integer.toString(this.store));
                        startActivity(gameIntent);
                    }
                }
            });
            deletes[i].setOnClickListener(new MyOnClickListener(i) {
                @Override
                public void onClick(View v) {
                    Button customOne = findViewById(R.id.customOne);
                    Button delOne = findViewById(R.id.delOne);
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            "Decks", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("Title" + this.store);
                    editor.remove("" + this.store);
                    editor.apply();
                    customs[this.store].setText("Create Custom Deck");
                    customs[this.store].setTextColor(getResources().getColor(R.color.colorPrimary));
                    delOne.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(CustomGameActivity.this,MainActivity.class));
        finish();
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
