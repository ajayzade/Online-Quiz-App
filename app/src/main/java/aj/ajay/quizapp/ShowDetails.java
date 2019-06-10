package aj.ajay.quizapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowDetails extends AppCompatActivity {

    private Spinner spinner;
    private TextView t1,t2,t3,t;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userID;
    private ArrayList<Integer> s = new ArrayList<>();
    int d = 10;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        adView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        s.add(8);
        s.add(10);
        s.add(12);
        s.add(15);
        s.add(18);
        s.add(20);

        t = findViewById(R.id.user_text);
        spinner = findViewById(R.id.duration_spinner);
        t1 = findViewById(R.id.total_score);
        t2 = findViewById(R.id.questions_played);
        t3 = findViewById(R.id.right_answers);

        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_dropdown_item_1line,s);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrayAdapter);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        if (userID != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.setText((String) dataSnapshot.child("Username").getValue());
                t1.setText(dataSnapshot.child("score").getValue() + "");
                t2.setText(dataSnapshot.child("number").getValue() + "");
                t3.setText(dataSnapshot.child("right").getValue() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    d = 8;
                }
                if (position == 1) {
                    d = 10;
                }
                if (position == 2) {
                    d = 12;
                }
                if (position == 3) {
                    d = 15;
                }
                if (position == 4) {
                    d = 18;
                }
                if (position == 5) {
                    d = 20;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onSave(View view) {
        databaseReference.child("duration").setValue(d);
        Toast.makeText(this, "Duration Saved", Toast.LENGTH_SHORT).show();
    }
}
