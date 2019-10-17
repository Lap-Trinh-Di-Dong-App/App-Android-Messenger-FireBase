package com.example.appmessengerfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

// Part 2 Login User
// Part 3 Auto Login User đã Login


public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, passWord;
    Button btn_login;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");


        // khởi tạo tài khoản firebase
        auth = FirebaseAuth.getInstance();

        // gán giá trị từ các control xuống
        email = findViewById(R.id.email);
        passWord = findViewById(R.id.passWord);
        btn_login = findViewById(R.id.btn_login);

        // Sự kiện click button Login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
                String txt_passWord = passWord.getText().toString();

                // Kiểm tra các trường
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_passWord)){
                    Toast.makeText(LoginActivity.this, "Tất cả các trường phải được điền đủ.",Toast.LENGTH_SHORT).show();
                } else{

                    auth.signInWithEmailAndPassword(txt_email, txt_passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                // Đưa dữ liêu từ loginActivity qua và chuyển qua màn hình MainActivity xừ lý
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent); // di chuyển qua MainA
                                finish();
                            } else{
                                Toast.makeText(LoginActivity.this, "Đăng Nhập Thất Bại.",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }
}
