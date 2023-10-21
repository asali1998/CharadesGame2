package com.example.sihengli.charadesgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateDeckActivity extends AppCompatActivity {

    private String currentDeck;
    private ArrayList<String> deckList = new ArrayList<>();
    private ListView cardsLV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cardsLV = findViewById(R.id.cardsLV);
        currentDeck = getIntent().getStringExtra("deckNum");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);
        //hideNavigationBar();
        EditText deckTitle = findViewById(R.id.deckTitle);
        deckTitle.setText("Custom Deck " + currentDeck);
        Button addCardBtn = findViewById(R.id.addCard);
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
            }
        });
        Button sbtBtn = findViewById(R.id.submitDeck);
        sbtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDeck();
                Intent gameIntent = new Intent(CreateDeckActivity.this,CustomGameActivity.class);
                startActivity(gameIntent);
                finish();
            }
        });
        Button cnlBtn = findViewById(R.id.cancelCreate);
        cnlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(CreateDeckActivity.this,CustomGameActivity.class);
                startActivity(gameIntent);
                finish();
            }
        });
    }

    private void addDialog() {
        cardsLV = findViewById(R.id.cardsLV);
        AlertDialog.Builder addCard = new AlertDialog.Builder(CreateDeckActivity.this);
        addCard.setTitle("Add Card");
        final EditText input = new EditText(CreateDeckActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        addCard.setView(input);
        addCard.setPositiveButton(R.string.add,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String toAdd = input.getText().toString();
                        deckList.add(toAdd);
                        ArrayAdapter<String> deckAdapter = new ArrayAdapter<String>(
                                CreateDeckActivity.this, R.layout.card_row, deckList);
                        cardsLV.setAdapter(deckAdapter);
                    }
                });

        addCard.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        addCard.show();
    }

    private void saveDeck(){
        SharedPreferences sharedPreferences = getSharedPreferences(
                "Decks", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        set.addAll(deckList);
        editor.putStringSet(currentDeck, set);
        EditText title = findViewById(R.id.deckTitle);
        editor.putString("Title" + currentDeck, title.getText().toString());
        editor.apply();
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
