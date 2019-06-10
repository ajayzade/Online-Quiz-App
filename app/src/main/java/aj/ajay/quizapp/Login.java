package aj.ajay.quizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText e1,e2;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String email,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("User Login");

        e1 = findViewById(R.id.edit_email);
        e2 = findViewById(R.id.edit_pass);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
    }

    public void registerActivity(View view) {
        startActivity(new Intent(Login.this,RegisterActivity.class));
    }

    public void cancel(View view) {
        e1.setText("");
        e2.setText("");
    }

    public void login(View view) {
        email = e1.getText().toString();
        password = e2.getText().toString();
        progressDialog.setTitle("Logging you");
        progressDialog.show();
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(Login.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else {
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this,ContentActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else {
                                Toast.makeText(Login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }
}
