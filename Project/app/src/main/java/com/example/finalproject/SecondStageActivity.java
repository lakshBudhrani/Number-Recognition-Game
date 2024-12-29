package com.example.finalproject;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class SecondStageActivity extends AppCompatActivity {

    // Tag for logging purposes
    private static final String TAG = "SecondStageActivity";

    // UI components
    private ImageView digitImageView;
    private EditText userGuessEditText;
    private TextView guessResultTextView;
    private TextView roundTextView;
    private TextView userScoreTextView;
    private Button submitGuessButton;

    // Game state variables
    private int[] digitLabels;
    private int currentDigitIndex;
    private int userCorrectGuesses = 0;
    private int round = 0;
    private int computerCorrectGuesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_stage);

        // Initialize UI components
        digitImageView = findViewById(R.id.digitImageView);
        userGuessEditText = findViewById(R.id.userGuessEditText);
        guessResultTextView = findViewById(R.id.guessResultTextView);
        roundTextView = findViewById(R.id.roundTextView);
        userScoreTextView = findViewById(R.id.userScoreTextView);
        submitGuessButton = findViewById(R.id.submitGuessButton);

        // Get the number of correct guesses by the computer from the intent
        computerCorrectGuesses = getIntent().getIntExtra("computerCorrectGuesses", 0);

        // Load the target digit labels from assets
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("targets.txt");
            digitLabels = loadTargetArray(inputStream);
            Log.d(TAG, "Target array loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load target array.");
        }

        // Display the first digit
        showNextDigit();

        // Set the submit button's click listener to handle guesses
        submitGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userGuess = Integer.parseInt(userGuessEditText.getText().toString());
                Log.d(TAG, "User guessed: " + userGuess);
                Log.d(TAG, "Correct digit: " + digitLabels[currentDigitIndex]);

                // Check if the user's guess is correct
                if (userGuess == digitLabels[currentDigitIndex]) {
                    userCorrectGuesses++;
                    guessResultTextView.setText("Correct!");
                } else {
                    guessResultTextView.setText("Wrong! The correct digit was " + digitLabels[currentDigitIndex]);
                }

                // Update game state and proceed to the next round or finish
                round++;
                updateMetrics();
                if (round < 10) {
                    showNextDigit();
                } else {
                    Intent intent = new Intent(SecondStageActivity.this, FinalActivity.class);
                    intent.putExtra("userCorrectGuesses", userCorrectGuesses);
                    intent.putExtra("computerCorrectGuesses", computerCorrectGuesses);
                    startActivity(intent);
                }
            }
        });

        updateMetrics();
    }

    private void updateMetrics() {
        roundTextView.setText("Round: " + (round + 1) + "/10");
        userScoreTextView.setText("User's Correct Guesses: " + userCorrectGuesses);
    }

    private void showNextDigit() {
        currentDigitIndex = new Random().nextInt(1797);
        Log.d(TAG, "Current digit index: " + currentDigitIndex);
        String imageName = "digit_" + currentDigitIndex;
        int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
        digitImageView.setImageResource(resID);
        userGuessEditText.setText("");
        guessResultTextView.setText("");
    }

    private int[] loadTargetArray(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int[] array = new int[1797];
        int index = 0;
        while ((line = reader.readLine()) != null) {
            array[index++] = Integer.parseInt(line.trim());
        }
        reader.close();
        return array;
    }
}