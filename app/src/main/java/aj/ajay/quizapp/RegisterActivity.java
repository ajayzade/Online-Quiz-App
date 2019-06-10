package aj.ajay.quizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText e1,e2,e3;
    private FirebaseAuth auth;
    private String username,email,password;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("User Registration");

        e1 = findViewById(R.id.register_username);
        e2 = findViewById(R.id.register_email);
        e3 = findViewById(R.id.register_pass);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
    }

    public void register(View view) {
        username = e1.getText().toString();
        email = e2.getText().toString();
        password = e3.getText().toString();
        progressDialog.setTitle("Registering your data ...");
        progressDialog.show();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String userID = auth.getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                                databaseReference.child("Username").setValue(username);
                                databaseReference.child("Email").setValue(email);
                                databaseReference.child("Password").setValue(password);
                                databaseReference.child("score").setValue(0);
                                databaseReference.child("number").setValue(0);
                                databaseReference.child("right").setValue(0);
                                databaseReference.child("duration").setValue(10);
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void cancel(View view) {
        e1.setText("");
        e2.setText("");
        e3.setText("");
    }

    public void loginActivity(View view) {
        startActivity(new Intent(RegisterActivity.this,Login.class));
    }
}