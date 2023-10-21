package com.example.sihengli.charadesgame;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;

/**
 * Created by andytang on 5/17/18.
 */

public class MyOnClickListener implements View.OnClickListener {
    public int store;

    public MyOnClickListener(int i) {
        store = i;
    }

    @Override
    public void onClick(View v) {
    }
}