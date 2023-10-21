package com.example.sihengli.charadesgame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

//public class WordActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
public class WordActivity extends AppCompatActivity {

    public static final String TAG = "WordActivity";

    TextView tvCountdown;
    TextView tvWord;
    TextView tvProgress;
    TextView tvRecognitionResult;
    TextView tvRecognitionState;
    //List<Integer> colorList;
    List<String> wordList;
    int wordListSize;
    int wordCount;
    long lastWordRefreshTimestampNanos;
    static final long gyroscopeCooloffTime = 2000000000L;//enforce a min. 2 seconds between two gyro/speech-inititated actions
    SensorEventListener gyroListener;
    SensorManager sensorManager;
    Sensor gyro;
    CountDownTimer timer;
    //private TextToSpeech tts;
    private android.speech.SpeechRecognizer sr;
    String currentWord;
    private AudioManager mAudioManager;
    private int mStreamVolume = 0;
    HashMap<String, List<String>> standards = new HashMap<>();
    ArrayList<String> correct = new ArrayList<>();
    ArrayList<String> wrong = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        hideNavigationBar();
        tvWord = findViewById(R.id.tvWord);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvProgress = findViewById(R.id.tvProgress);
        tvRecognitionResult = findViewById(R.id.tvRecognitionResult);
        tvRecognitionState = findViewById(R.id.tvRecognitionState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //tts = new TextToSpeech(this, this);
        wordCount = 0;
        lastWordRefreshTimestampNanos = 0;


        setUpVoiceRecognizer();


        loadStandards();
        Intent i = getIntent();
        if (i.getStringExtra("type").equals("standard")) {
            wordList = standards.get(i.getStringExtra("title"));
        }
        if (i.getStringExtra("type").equals("custom")) {
            SharedPreferences sp = getSharedPreferences("Decks", MODE_PRIVATE);
            wordList = new ArrayList<String>();
            wordList.addAll(sp.getStringSet(i.getStringExtra("title"), null));
        }

        begin();
    }

    private void loadStandards() {
        String[] classics = new String[]{"dancing ballet", "rock climbing", "bowling", "sewing",
                "making pizza", "flipping pancakes", "riding a motorcycle", "building a sandcastle",
                "sumo wrestling", "playing drums", "playing the oboe", "practicing karate"};
        List<String> classicList = new LinkedList<>(Arrays.asList(classics));
        standards.put("classics", classicList);
        String[] movies = new String[]{"Goodfellas", "Aliens", "The Shawshank Redemption", "Pulp Fiction",
                "Forrest Gump", "The Avengers", "The Dark Knight", "The Matrix", "Fargo", "Good Will Hunting",
                "The Fast and Furious", "Jurassic Park", "Star Wars: A New Hope", "Titanic", "Avatar", "The Kissing Booth"};
        List<String> moviesList = new LinkedList<>(Arrays.asList(movies));
        standards.put("movies", moviesList);
        String[] popHits = new String[]{"Teenage Dream, Katy Perry", "Get Lucky, Daft Punk ft. " +
                "Pharrel Williams", "Rolling in the Deep, Adele", "We Can't Stop, Miley Cyrus",
                "Hold On, We're Going Home, Drake", "Super Bass, Nicki Minaj", "Havana, Camila Cabello",
                "Sia, Chandelier", "Some Nights, Fun", "Do I Wanna Known, Arctic Monkeys",
                "Call Me Maybe, Carly Rae Jepsen", "I Gotta Feeling, Black Eyed Peas",
                "Gold Digger, Kanye West", "Hey Ya!, Outkast", "Mr. Brightside, The Killers",
                "Stacy's Mom, Fountains of Wayne", "Drop It Like It's Hot, Snoop Dogg",
                "You Belong With Me, Taylor Swift"};
        List<String> popList = new LinkedList<>(Arrays.asList(popHits));
        standards.put("pop", popList);
        String[] animals = new String[]{"Dog", "Pig", "Cat", "Cow", "Lion", "Giraffe", "Hippo",
                "Wildabeest", "Cheetah", "Kiwi", "Buffalo", "Crocodile", "Zebra", "Wolf", "Bear",
                "Panda", "Panther", "Jaguar", "Cougar", "Deer", "Shark", "Whale", "Shrimp",
                "Monkey", "Beetle", "Gorilla", "Elephant", "Koala", "Kangaroo"};
        List<String> animalList = new LinkedList<>(Arrays.asList(animals));
        standards.put("animals", animalList);
    }

    private void setUpVoiceRecognizer() {

        //if no audio permission, then disable voice recognition function
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            tvRecognitionResult.setText("");
            tvRecognitionState.setText("");
            return;
        }
        try {
            if (sr != null) {
                sr.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sr = android.speech.SpeechRecognizer
                .createSpeechRecognizer(this);
        sr.setRecognitionListener(new WordActivity.SpeechRecognizer() {
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000);//60 secs
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 60000);//60 secs
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 30000);//30 secs
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.example.sihengli.charadesgame");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        sr.startListening(intent);
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAndRegisterGyro();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            if (sr != null) {
                sr.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAndRegisterGyro() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] >= 2f && event.timestamp - lastWordRefreshTimestampNanos >= gyroscopeCooloffTime) {
                    lastWordRefreshTimestampNanos = event.timestamp;
                    //Toast.makeText(WordActivity.this, "Rotate Up", Toast.LENGTH_SHORT).show();
                    nextScreen(false);
                } else if (event.values[1] <= -2f && event.timestamp - lastWordRefreshTimestampNanos >= gyroscopeCooloffTime) {
                    lastWordRefreshTimestampNanos = event.timestamp;
                    //Toast.makeText(WordActivity.this, "Rotate Down", Toast.LENGTH_SHORT).show();
                    nextScreen(true);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_UI);
    }

    /*private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }*/


    @Override
    protected void onPause() {
        ((SensorManager) getSystemService(SENSOR_SERVICE)).unregisterListener(gyroListener);
        if (sr != null) {
            sr.destroy();
        }
        super.onPause();
    }


    private void showNextWord() {
        Random rand = new Random();
        try {
            currentWord = wordList.remove(rand.nextInt(wordList.size()));
        }catch (IllegalArgumentException e){
            Toast.makeText(WordActivity.this,"The word deck cannot be empty.",Toast.LENGTH_LONG);
            onBackPressed();

        }
        tvWord.setText(currentWord);
    }


    private void begin() {
        final View root = tvCountdown.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        showNextWord();
        resetTimer();
        setUpVoiceRecognizer();
        wordListSize = wordList.size() + 1;
        wordCount++;
        tvProgress.setText(wordCount + "/" + wordListSize);
    }

    private void nextScreen(boolean isCorrect) {
        final View root = tvCountdown.getRootView();
        setUpVoiceRecognizer();
        if (isCorrect) {
            correct.add(tvWord.getText().toString());
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            tvWord.setText("Correct!");
        } else {
            wrong.add(tvWord.getText().toString());
            root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            tvWord.setText("Incorrect!");
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (wordList.size() == 0) {
                    Intent summary = new Intent(WordActivity.this, SummaryActivity.class);
                    ArrayList<String> myList = new ArrayList<String>();
                    summary.putExtra("correct", correct);
                    summary.putExtra("wrong", wrong);
                    timer.cancel();
                    startActivity(summary);
                    finish();
                } else {
                    nextScreenTwo(root);
                }
            }
        }, 1500);
    }

    private void nextScreenTwo(View root) {
        root.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        //setRandomBackgroundColor();
        showNextWord();
        resetTimer();

        wordCount++;
        tvProgress.setText(wordCount + "/" + wordListSize);
        //tvRecognitionResult.setText("listening...");
        //lastWordRefreshTimestampNanos = System.nanoTime();
    }

    private void resetTimer() {
        if (timer != null)
            timer.cancel();
        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished >= 10000)
                    tvCountdown.setText(getString(R.string.double_zero_time) + millisUntilFinished / 1000);
                else
                    tvCountdown.setText(getString(R.string.double_zero_column_zero) + millisUntilFinished / 1000);
            }

            public void onFinish() {
                nextScreen(false);
            }
        };
        timer.start();
    }


    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(WordActivity.this, MainActivity.class));
        finish();
    }



    class SpeechRecognizer implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
            tvRecognitionState.setText("ready for speech...");
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0);
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            tvRecognitionState.setText("speech begun...");
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            //Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            tvRecognitionState.setText("sound detected");
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.wtf(TAG, "onEndofSpeech");
            tvRecognitionState.setText("end of speech, analysing...");
            //setUpVoiceRecognizer();]\
        }

        public void onError(int error) {
            //tvRecognitionState.setText("error: "+ error);
            Log.d(TAG, "error " + error);
            if (error == 6) {//time-out
                setUpVoiceRecognizer();
            } else if (error == 7) {//no match
                tvRecognitionResult.setText("Unidentified. Try again.");
                setUpVoiceRecognizer();
            }
        }

        public void onResults(Bundle results) {

            String str = new String();
            tvRecognitionState.setText("analysing...");
            Log.d(TAG, "onResults " + results);
            ArrayList<String> data = results
                    .getStringArrayList(
                            android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            tvRecognitionResult.setText("\"" + data.get(0) + "\"");
            for (String text : data) {
                String[] inputs = text.split(" ");
                String[] correctAnswer = currentWord.split(",| |\\.");
                if (fuzzyMatch(inputs, correctAnswer)) {
                    Toast.makeText(WordActivity.this, "Speech Recognized: " + text, Toast.LENGTH_SHORT).show();
                    nextScreen(true);
                    return;
                }
            }
            setUpVoiceRecognizer();
        }

        public void onPartialResults(Bundle partialResults) {
            //String str = new String();
            tvRecognitionState.setText("partial result");
            Log.d(TAG, "onPartialResults " + partialResults);
            ArrayList<String> data = partialResults
                    .getStringArrayList(
                            android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            tvRecognitionResult.setText("\"" + data.get(0) + "...\"");
            for (String text : data) {
                String[] inputs = text.split(" ");
                String[] correctAnswer = currentWord.split(",| |\\.");
                if (fuzzyMatch(inputs, correctAnswer)) {
                    Toast.makeText(WordActivity.this, "Speech Recognized: " + text, Toast.LENGTH_SHORT).show();
                    nextScreen(true);
                    return;
                }
            }
        }

        private boolean fuzzyMatch(String[] inputs, String[] target) {

            Set<String> matchedWords = new HashSet<>();
            int sizeTarget = target.length;

            if (sizeTarget == 1) {
                for (String singleInput : inputs) {
                    if (singleInput.equalsIgnoreCase(target[0])) {
                        return true;
                    }
                }
            } else if (sizeTarget == 2) {
                for (String singleInput : inputs) {
                    if (singleInput.equalsIgnoreCase(target[0])) {
                        for (String singleInput2 : inputs) {
                            if (singleInput2.equalsIgnoreCase(target[1])) {
                                return true;
                            }
                        }
                    }
                }
            }
            //target size is greater than 3, then we use fuzzy matching
            else {
                for (String singleInput : inputs) {
                    for (String singleTarget : target) {
                        if (singleInput.equalsIgnoreCase(singleTarget)) {
                            matchedWords.add(singleTarget);
                        }
                    }
                }
                if (matchedWords.size() >= Math.ceil(sizeTarget * 2 / 3)) {
                    return true;
                }
            }

            return false;
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
            tvRecognitionState.setText("on event: " + eventType);
        }
    }


}