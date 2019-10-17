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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

// Part 2, 3

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText userName, email, passWord;
    Button btn_register; // nút Register

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register");

        //dữ liệu từ các control xuống
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        passWord = findViewById(R.id.passWord);

        // khởi tạo tài khoản firebase
        auth = FirebaseAuth.getInstance();

        btn_register = findViewById(R.id.btn_register);

        //sự kiện khi button Register click
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //gán giá trị cho các trường từ biến khai báo trên
                String txt_userName = userName.getText().toString();
                String txt_email = email.getText().toString();
                String txt_passWord = passWord.getText().toString();

                // kiểm tra các trường
                if (TextUtils.isEmpty(txt_userName) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_passWord)){
                    Toast.makeText(RegisterActivity.this, "Tất cả các trường phải được điền đủ.",Toast.LENGTH_SHORT).show();
                }
                else if(txt_passWord.length() < 6) // mật khẩu phải hơn 6 ký
                {
                    Toast.makeText(RegisterActivity.this, "Mật Khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(txt_userName,txt_email,txt_passWord);
                }

            }
        });

    }

    //Hàm đăng ký
    private void register(final String userName, String email , String passWord){
        auth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser(); //kiểm tra người dùng hiện
                    assert firebaseUser != null; // Xác nhận người dùng hiện tại khác null
                    String userid = firebaseUser.getUid();

                    //thêm dữ liệu xuống database có nhánh cha là User
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("userName",userName);
                    hashMap.put("image","default");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"Tài khoản Email này đã được sử ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
