package com.example.appmessengerfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText yourEmail;
    Button btn_sendEmail;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        yourEmail = findViewById(R.id.yourEmail);
        btn_sendEmail = findViewById(R.id.btn_sendEmail);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = yourEmail.getText().toString();

                if(email.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Bạn Chưa Nhập Email", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Bắt Lỗi
                            String error = task.getException().getMessage();

                            if(task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                            "Mật Khẩu Được Đã Gởi. Làm Ơn Kiểm Tra Email Của Bạn", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
