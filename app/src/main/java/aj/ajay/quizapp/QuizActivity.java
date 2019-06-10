package aj.ajay.quizapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    String category,s1,s2,s3;
    int noQuestions,currentQuestion = -1,score = 0,correctAnswers = 0,wrongAnswers = 0;
    private Firebase firebase;
    private ArrayList<Model> allQuestionsList = new ArrayList<>();
    private ArrayList<Model> categoryQuestionsList = new ArrayList<>();
    private TextView questionTextView,questionCounter;
    private Button btnA,btnB,btnC,btnD;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private ArrayList<CharSequence> sendQuestions = new ArrayList<>();
    private ArrayList<CharSequence> selectedAnswers = new ArrayList<>();
    private ArrayList<CharSequence> rightAnswers = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String userID;
    private long duration = 10000;
    private AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Firebase.setAndroidContext(this);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        if (userID != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        }

        questionTextView = (TextView) findViewById(R.id.question_textview);
        questionCounter = (TextView) findViewById(R.id.question_counter);
        btnA  = (Button) findViewById(R.id.btn_a);
        btnB  = (Button) findViewById(R.id.btn_b);
        btnC  = (Button) findViewById(R.id.btn_c);
        btnD  = (Button) findViewById(R.id.btn_d);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mediaPlayer = MediaPlayer.create(this,R.raw.click);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        noQuestions = intent.getIntExtra("number",10);
        getSupportActionBar().setTitle(category.toUpperCase());

        firebase = new Firebase("https://wallpaper-download-5549f.firebaseio.com/questions");

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    allQuestionsList.add(new Model(
                            d.child("question").getValue(String.class),
                            d.child("category").getValue(String.class),
                            d.child("correctAnswer").getValue(String.class),
                            d.child("choices").child("0").getValue(String.class),
                            d.child("choices").child("1").getValue(String.class),
                            d.child("choices").child("2").getValue(String.class),
                            d.child("choices").child("3").getValue(String.class)
                    ));
                }

                for (int i = 0;i < allQuestionsList.size();i++){
                    if (allQuestionsList.get(i).getCategory().equals(category.toLowerCase())) {
                        categoryQuestionsList.add(allQuestionsList.get(i));
                    }
                }

                Collections.shuffle(categoryQuestionsList);

                reference.child("duration").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        duration = Long.parseLong(String.valueOf(dataSnapshot.getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                countDownTimer = new CountDownTimer(duration,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        progressBar.setProgress((int) (progressBar.getMax() - (millisUntilFinished / 1000)));
                    }

                    @Override
                    public void onFinish() {
                        selectedAnswers.add("Not Selected");
                        showNextQuestion();
                    }
                };

                showNextQuestion();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showNextQuestion() {
        currentQuestion++;
        progressBar.setProgress(0);
        questionCounter.setText((currentQuestion) + " / " + noQuestions);
        if (currentQuestion < noQuestions) {
            questionTextView.setText(categoryQuestionsList.get(currentQuestion).getQuestion());
            sendQuestions.add(categoryQuestionsList.get(currentQuestion).getQuestion());
            btnA.setText(categoryQuestionsList.get(currentQuestion).getChoiceA());
            btnB.setText(categoryQuestionsList.get(currentQuestion).getChoiceB());
            btnC.setText(categoryQuestionsList.get(currentQuestion).getChoiceC());
            btnD.setText(categoryQuestionsList.get(currentQuestion).getChoiceD());
            countDownTimer.start();
        }else {
            Intent i = new Intent(QuizActivity.this,ResultActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("score",score);
            i.putExtra("correct_answer",correctAnswers);
            i.putExtra("wrong_answer",wrongAnswers);
            i.putCharSequenceArrayListExtra("questions",sendQuestions);
            i.putCharSequenceArrayListExtra("selected",selectedAnswers);
            i.putCharSequenceArrayListExtra("correct",rightAnswers);
            startActivity(i);
        }
    }

    public void onBtnA(View view) {
        mediaPlayer.start();
        countDownTimer.cancel();
        s1 = btnA.getText().toString();
        s2 = categoryQuestionsList.get(currentQuestion).getAnswer();
        selectedAnswers.add(s1);
        if (s2.equals("0")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceA();
        }else if (s2.equals("1")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceB();
        }else if (s2.equals("2")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceC();
        }else {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceD();
        }
        rightAnswers.add(s3);
        if (s1.equals(s3)) {
            score = score + 10;
            correctAnswers = correctAnswers + 1;
            showNextQuestion();
        }else {
            wrongAnswers = wrongAnswers + 1;
            showNextQuestion();
        }
    }

    public void onBtnB(View view) {
        mediaPlayer.start();
        countDownTimer.cancel();
        s1 = btnB.getText().toString();
        s2 = categoryQuestionsList.get(currentQuestion).getAnswer();
        selectedAnswers.add(s1);
        if (s2.equals("0")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceA();
        }else if (s2.equals("1")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceB();
        }else if (s2.equals("2")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceC();
        }else {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceD();
        }
        rightAnswers.add(s3);
        if (s1.equals(s3)) {
            score = score + 10;
            correctAnswers = correctAnswers + 1;
            showNextQuestion();
        }else {
            wrongAnswers = wrongAnswers + 1;
            showNextQuestion();
        }
    }

    public void onBtnC(View view) {
        mediaPlayer.start();
        countDownTimer.cancel();
        s1 = btnC.getText().toString();
        s2 = categoryQuestionsList.get(currentQuestion).getAnswer();
        selectedAnswers.add(s1);
        if (s2.equals("0")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceA();
        }else if (s2.equals("1")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceB();
        }else if (s2.equals("2")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceC();
        }else {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceD();
        }
        rightAnswers.add(s3);
        if (s1.equals(s3)) {
            score = score + 10;
            correctAnswers = correctAnswers + 1;
            showNextQuestion();
        }else {
            wrongAnswers = wrongAnswers + 1;
            showNextQuestion();
        }
    }

    public void onBtnD(View view) {
        mediaPlayer.start();
        countDownTimer.cancel();
        s1 = btnD.getText().toString();
        s2 = categoryQuestionsList.get(currentQuestion).getAnswer();
        selectedAnswers.add(s1);
        if (s2.equals("0")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceA();
        }else if (s2.equals("1")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceB();
        }else if (s2.equals("2")) {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceC();
        }else {
            s3 = categoryQuestionsList.get(currentQuestion).getChoiceD();
        }
        rightAnswers.add(s3);
        if (s1.equals(s3)) {
            score = score + 10;
            correctAnswers = correctAnswers + 1;
            showNextQuestion();
        }else {
            wrongAnswers = wrongAnswers + 1;
            showNextQuestion();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
    }
}