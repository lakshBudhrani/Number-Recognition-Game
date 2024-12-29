package com.example.finalproject;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FinalActivity extends AppCompatActivity {

    // TextView for displaying the game result
    private TextView resultTextView;

    // TextView for showing the user's score
    private TextView userScoreTextView;

    // TextView for showing the computer's score
    private TextView computerScoreTextView;

    // TextView for displaying the winner of the game
    private TextView winnerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        // Initialize TextViews
        resultTextView = findViewById(R.id.resultTextView);
        userScoreTextView = findViewById(R.id.userScoreTextView);
        computerScoreTextView = findViewById(R.id.computerScoreTextView);
        winnerTextView = findViewById(R.id.winnerTextView);

        // Retrieve scores passed from the previous activity
        int userCorrectGuesses = getIntent().getIntExtra("userCorrectGuesses", 0);
        int computerCorrectGuesses = getIntent().getIntExtra("computerCorrectGuesses", 0);

        // Set TextViews to display user and computer scores
        userScoreTextView.setText("User's Correct Guesses: " + userCorrectGuesses);
        computerScoreTextView.setText("Computer's Correct Guesses: " + computerCorrectGuesses);

        // Determine the winner based on scores
        String result;
        if (userCorrectGuesses > computerCorrectGuesses) {
            result = "User wins!";
        } else if (computerCorrectGuesses > userCorrectGuesses) {
            result = "Computer wins!";
        } else {
            result = "It's a tie!";
        }

        // Display the winner or tie result
        winnerTextView.setText(result);
    }
}