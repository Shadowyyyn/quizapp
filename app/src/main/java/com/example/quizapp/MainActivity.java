package com.example.quizapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvQuestion, tvTimer, tvScore, tvQuestionNumber;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnNext;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;
    private boolean isAnswered = false;

    private static final long TIMER_DURATION = 30000; // 30 секунд
    private long timeLeftInMillis = TIMER_DURATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadQuestions();
        displayQuestion();

        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                resetForNextQuestion();
                displayQuestion();
            } else {
                showFinalScore();
            }
        });
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnNext = findViewById(R.id.btnNext);

        // Изначально кнопка Next неактивна
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        // Добавляем вопросы
        questionList.add(new Question(
                "Какая планета самая большая в Солнечной системе?",
                new String[]{"Марс", "Сатурн", "Юпитер", "Нептун"},
                2 // индекс правильного ответа (Юпитер)
        ));

        questionList.add(new Question(
                "Кто написал роман 'Война и мир'?",
                new String[]{"Достоевский", "Толстой", "Пушкин", "Чехов"},
                1 // Толстой
        ));

        questionList.add(new Question(
                "Сколько континентов на Земле?",
                new String[]{"5", "6", "7", "8"},
                2 // 7 континентов
        ));

        questionList.add(new Question(
                "Какой газ необходим для дыхания человека?",
                new String[]{"Углекислый газ", "Кислород", "Азот", "Водород"},
                1 // Кислород
        ));

        questionList.add(new Question(
                "В каком году человек впервые высадился на Луну?",
                new String[]{"1965", "1969", "1972", "1958"},
                1 // 1969
        ));
    }

    private void displayQuestion() {
        // Сбрасываем состояние
        isAnswered = false;
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        // Обновляем текст
        Question currentQuestion = questionList.get(currentQuestionIndex);
        tvQuestion.setText(currentQuestion.getQuestion());

        // Получаем варианты ответов и перемешиваем их для разнообразия
        String[] options = currentQuestion.getOptions();
        List<String> optionsList = new ArrayList<>();
        for (String option : options) {
            optionsList.add(option);
        }
        Collections.shuffle(optionsList);

        // Устанавливаем текст кнопок
        btnOption1.setText(optionsList.get(0));
        btnOption2.setText(optionsList.get(1));
        btnOption3.setText(optionsList.get(2));
        btnOption4.setText(optionsList.get(3));

        // Сбрасываем цвета кнопок
        resetButtonColors();

        // Обновляем номер вопроса и счет
        tvQuestionNumber.setText(String.format(Locale.getDefault(),
                "Вопрос %d/%d", currentQuestionIndex + 1, questionList.size()));
        tvScore.setText(String.format(Locale.getDefault(), "Счет: %d", score));

        // Запускаем таймер
        startTimer();
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOption2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOption3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnOption4.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        btnOption1.setTextColor(getResources().getColor(android.R.color.white));
        btnOption2.setTextColor(getResources().getColor(android.R.color.white));
        btnOption3.setTextColor(getResources().getColor(android.R.color.white));
        btnOption4.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void startTimer() {
        timeLeftInMillis = TIMER_DURATION;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimerText();

                // Если время вышло и ответ еще не дан
                if (!isAnswered) {
                    disableAllButtons();
                    Toast.makeText(MainActivity.this,
                            "Время вышло!", Toast.LENGTH_SHORT).show();
                    btnNext.setEnabled(true);
                    btnNext.setAlpha(1.0f);
                }
            }
        }.start();
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeFormatted = String.format(Locale.getDefault(), "00:%02d", seconds);
        tvTimer.setText(timeFormatted);

        // Меняем цвет таймера, когда мало времени
        if (timeLeftInMillis < 10000) {
            tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvTimer.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void disableAllButtons() {
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnOption4.setEnabled(false);
    }

    public void onOptionClick(View view) {
        if (isAnswered) return; // Если уже ответили, игнорируем

        Button clickedButton = (Button) view;
        String selectedAnswer = clickedButton.getText().toString();

        Question currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getOptions()[currentQuestion.getCorrectAnswerIndex()];

        // Останавливаем таймер
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        isAnswered = true;

        // Проверяем правильность ответа
        if (selectedAnswer.equals(correctAnswer)) {
            // Правильный ответ - зеленый
            clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            score += 10; // Добавляем очки
            tvScore.setText(String.format(Locale.getDefault(), "Счет: %d", score));
            Toast.makeText(this, "Правильно! +10 очков", Toast.LENGTH_SHORT).show();
        } else {
            // Неправильный ответ - красный для выбранной кнопки
            clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

            // Находим и подсвечиваем правильный ответ зеленым
            highlightCorrectAnswer(correctAnswer);

            Toast.makeText(this, "Неправильно!", Toast.LENGTH_SHORT).show();
        }

        // Делаем кнопку Next активной
        btnNext.setEnabled(true);
        btnNext.setAlpha(1.0f);

        // Блокируем все кнопки ответов
        disableAllButtons();
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (btnOption1.getText().toString().equals(correctAnswer)) {
            btnOption1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (btnOption2.getText().toString().equals(correctAnswer)) {
            btnOption2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (btnOption3.getText().toString().equals(correctAnswer)) {
            btnOption3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (btnOption4.getText().toString().equals(correctAnswer)) {
            btnOption4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void resetForNextQuestion() {
        // Включаем кнопки ответов
        btnOption1.setEnabled(true);
        btnOption2.setEnabled(true);
        btnOption3.setEnabled(true);
        btnOption4.setEnabled(true);

        // Сбрасываем цвета
        resetButtonColors();

        // Сбрасываем таймер
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showFinalScore() {
        String message = String.format(Locale.getDefault(),
                "Игра окончена!\nВаш итоговый счет: %d\nМаксимальный: %d",
                score, questionList.size() * 10);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Здесь можно добавить диалог с результатами или переход на другой экран
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}