package aj.ajay.quizapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luolc.emojirain.EmojiRainLayout;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private int score,correctAnswers,wrongAnswers;
    private ArrayList<CharSequence> questions,selectedAnswers,rightAnswers;
    private EmojiRainLayout emojiRainLayout;
    private ListView listView;
    private TextView showScore,showCorrect,showWrong;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String userID;
    int a,b,c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        questions = new ArrayList<>();
        selectedAnswers = new ArrayList<>();
        rightAnswers = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        if (userID != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        }

        emojiRainLayout = (EmojiRainLayout) findViewById(R.id.emoji_rain);
        showScore = (TextView) findViewById(R.id.score_text_view);
        showCorrect = (TextView) findViewById(R.id.correct_text_view);
        showWrong = (TextView) findViewById(R.id.wrong_text_view);
        listView = (ListView) findViewById(R.id.list_view);

        Intent intent = getIntent();
        score = intent.getIntExtra("score",0);
        correctAnswers = intent.getIntExtra("correct_answer",0);
        wrongAnswers = intent.getIntExtra("wrong_answer",0);
        questions = intent.getCharSequenceArrayListExtra("questions");
        selectedAnswers = intent.getCharSequenceArrayListExtra("selected");
        rightAnswers = intent.getCharSequenceArrayListExtra("correct");

        emojiRainLayout.addEmoji(R.drawable.voltage);
        emojiRainLayout.addEmoji(R.drawable.star);
        emojiRainLayout.addEmoji(R.drawable.snow);
        emojiRainLayout.addEmoji(R.drawable.money);
        emojiRainLayout.addEmoji(R.drawable.gift);
        emojiRainLayout.addEmoji(R.drawable.blossom);
        emojiRainLayout.stopDropping();
        emojiRainLayout.setPer(10);
        emojiRainLayout.setDuration(3000);
        emojiRainLayout.setDropDuration(2500);
        emojiRainLayout.setDropFrequency(500);
        emojiRainLayout.startDropping();

        showScore.setText("Score : " + score);
        showCorrect.setText("Correct : " + correctAnswers);
        showWrong.setText("Wrong : " + wrongAnswers);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reference.child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                a = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                Log.e("Message", String.valueOf(a));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                b = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                Log.e("Message", String.valueOf(b));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("right").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                c = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                Log.e("Message", String.valueOf(c));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        a = a + score;
        b = b + questions.size();
        c = c + rightAnswers.size();
        reference.child("score").setValue(a);
        reference.child("number").setValue(b);
        reference.child("right").setValue(c);
        Intent intent = new Intent(ResultActivity.this,ContentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int position) {
            return questions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.custom_list,null,false);
            TextView question = view.findViewById(R.id.custom_question);
            TextView correct = view.findViewById(R.id.custom_correct_answer);
            TextView your = view.findViewById(R.id.custom_selected_answer);
            ImageView imageView = view.findViewById(R.id.custom_image_view);
            TextView singleQuestionScore = view.findViewById(R.id.custom_score);
            question.setText((position + 1) + "." + questions.get(position));
            correct.setText("Correct Answer : " + rightAnswers.get(position));
            your.setText("Your Answer : " + selectedAnswers.get(position));
            if (rightAnswers.get(position).equals(selectedAnswers.get(position))) {
                singleQuestionScore.setText("10");
                imageView.setImageResource(R.drawable.righttick);
            }else {
                singleQuestionScore.setText("0");
                imageView.setImageResource(R.drawable.wrongtick);
            }
            return view;
        }
    }

}
