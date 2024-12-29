package com.example.finalproject;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nex3z.fingerpaintview.FingerPaintView;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

public class FirstStageActivity extends AppCompatActivity {

    // UI elements
    private FingerPaintView fingerPaintView;
    private Paint paint;
    private Interpreter tflite;
    private TextView resultTextView;
    private TextView roundTextView;
    private TextView computerScoreTextView;
    private EditText userInputEditText;
    private Button predictButton;
    private Button nextStageButton;

    // Game state variables
    private int computerCorrectGuesses = 0;
    private int round = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_stage);

        // Initialize UI components
        fingerPaintView = findViewById(R.id.fpv);
        resultTextView = findViewById(R.id.resultTextView);
        roundTextView = findViewById(R.id.roundTextView);
        computerScoreTextView = findViewById(R.id.computerScoreTextView);
        userInputEditText = findViewById(R.id.userInputEditText);
        predictButton = findViewById(R.id.predictButton);
        nextStageButton = findViewById(R.id.nextStageButton);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        // Set up radio button color selection
        RadioGroup colorGroup = findViewById(R.id.colorGroup);
        colorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioRed) {
                    paint.setStrokeWidth(28);
                    paint.setColor(Color.RED);
                } else if (checkedId == R.id.radioGreen) {
                    paint.setStrokeWidth(48);
                    paint.setColor(Color.GREEN);
                } else if (checkedId == R.id.radioBlue) {
                    paint.setStrokeWidth(36);
                    paint.setColor(Color.BLUE);
                }
                fingerPaintView.setPen(paint);
            }
        });

        // Set up the clear button to reset the drawing
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> fingerPaintView.clear());

        // Set up the predict button to trigger the prediction of the drawn digit
        predictButton.setOnClickListener(v -> {
            Bitmap bitmap = fingerPaintView.exportToBitmap(28, 28);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);
            float[] probabilities = runInference(inputBuffer);
            int predictedDigit = getPredictedDigit(probabilities);
            int userDrawnDigit = Integer.parseInt(userInputEditText.getText().toString());
            resultTextView.setText("Predicted: " + predictedDigit + ", You drew: " + userDrawnDigit);
            if (predictedDigit == userDrawnDigit) {
                computerCorrectGuesses++;
            }
            round++;
            updateMetrics();
            if (round == 10) {
                nextStageButton.setVisibility(View.VISIBLE);
            }
        });

        // Set up the next stage button to proceed to the next activity
        nextStageButton.setOnClickListener(v -> {
            Intent intent = new Intent(FirstStageActivity.this, SecondStageActivity.class);
            intent.putExtra("computerCorrectGuesses", computerCorrectGuesses);
            startActivity(intent);
        });

        // Load the TensorFlow Lite model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateMetrics();
    }

    // Update the round and computer score display
    private void updateMetrics() {
        roundTextView.setText("Round: " + (round + 1) + "/10");
        computerScoreTextView.setText("Computer's Correct Guesses: " + computerCorrectGuesses);
    }

    // Load the TensorFlow Lite model from assets
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("digit.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Convert the bitmap image to a ByteBuffer for the model input
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 28 * 28);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[28 * 28];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int pixelValue : intValues) {
            int r = (pixelValue >> 16) & 0xFF;
            int g = (pixelValue >> 8) & 0xFF;
            int b = pixelValue & 0xFF;
            int gray = (r + g + b) / 3;
            byteBuffer.putFloat((255 - gray) / 255.0f);
        }
        return byteBuffer;
    }

    // Run the model inference with the input buffer
    private float[] runInference(ByteBuffer inputBuffer) {
        float[][] output = new float[1][10];
        tflite.run(inputBuffer, output);
        return output[0];
    }

    // Get the predicted digit based on the highest probability
    private int getPredictedDigit(float[] probabilities) {
        int maxIndex = -1;
        float maxProbability = -1;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}