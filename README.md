# Number-Recognition-Game

## Project Description

The goal of this project is to develop an interactive game app where a player competes against the computer in a two-stage game, focusing on number recognition and machine learning. The app uses neural networks to demonstrate AI's capability to predict and work with real data.

### Game Structure

- **Stage 1**: The user draws 10 numbers on the screen. The app captures these drawings using the FingerPaint library, processes them into pixel data, and the neural network predicts the numbers.
- **Stage 2**: The app randomly selects 10 numbers from the `load_digits` dataset from sklearn. The user guesses these digits, earning points for correct guesses.

At the end of both stages, the results are compared to determine who performed better, the computer or the user, showcasing the neural network's efficiency in real-time number prediction and recognition.

### Repository Contents

1. **B409FinalProjectPaper.docx**: Report detailing the project.
2. **DigitImages.ipynb**: Python notebook for extracting images and target array from sklearn's `load_digits` dataset.
3. **ISATB409FinalProjectAbstract.docx**: Initial project idea submission that was accepted.
4. **MNIST.ipynb**: Python notebook with the model and logic for training the digit recognition model (provided by the professor).
5. **Screen_recording_20241208_230928.webm**: Screen recording of the app in action.
6. **Project**: Complete code for the Android app, including Java logic, XML layout, and digit images from sklearn.
