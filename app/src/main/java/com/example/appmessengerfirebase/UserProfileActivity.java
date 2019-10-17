package com.example.appmessengerfirebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appmessengerfirebase.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

// Trang thông tin cá nhân User
// Upload Image

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView image_profile;
    TextView userName;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    // Part 10
    StorageReference storageReference;
    private  static final int IMAGE_REQUEST = 1;
    private Uri imageUrl;
    private StorageTask uploadTask;
    // Over Part 10

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Thanh toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); // Ẩn thanh toolbar chính

        setActionBar(toolbar);
        getActionBar().setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Khai báo các biến ứng với các control
        image_profile = findViewById(R.id.profile_image);
        userName = findViewById(R.id.userName);

        // Lấy User đang sử dụng hiện tại
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Trả về ID của user đang sử dụng
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Gán UserName
                userName.setText(user.getUserName());

                // Lấy hình ảnh
                if(user.getImage().equals("default"))
                    image_profile.setImageResource(R.mipmap.ic_appstart);
                else
                    Glide.with(UserProfileActivity.this).load(user.getImage()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Part 10
        // Mở Thư Mục Image trong điện thoại
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        // Over Part 10
    }

    // Part 10
    // Mở Thư Mục Image trong điện thoại
    private  void openImage(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    // Hàm trả về loại đuôi của Uri
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getBaseContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //Trả về phần mở rộng đã đăng ký cho loại MIME đã cho
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }
    // Over Part 10

    // Part 11
    // Hàm Tải Hình Ảnh Lên Từ Bộ Nhớ
    private void uploadImage(){
        // Hộp Thoại Tiến Độ Khi Tải File Lên
        final ProgressDialog pd = new ProgressDialog(getBaseContext());
        pd.setMessage("Uploading");
        pd.show();

        // Nếu chưa chọn hình thì thông báo
        // Ngược lại thì
        if(imageUrl != null){
            // System.currentTimeMillis() Trả về thời gian hiện tại mili giây
            // trỏ đến một vị trí con của tham chiếu hiện tại.
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            +"."+getFileExtension(imageUrl));

            // Lấy một cách không đồng bộ một URL tải xuống tồn tại lâu với mã thông báo có thể thu hồi được.
            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception{
                    if(!task.isSuccessful())
                        throw task.getException();
                    return  fileReference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri dowloadUri = task.getResult();
                        String mUri = dowloadUri.toString();

                        // Lấy ID của User Đang Sử dụng và Đẩy Hình Ảnh Lên Storage ở trên firebase
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image",mUri);
                        reference.updateChildren(map);

                        pd.dismiss(); // Loại bỏ hộp thoại
                    } else {
                        Toast.makeText(getBaseContext(), "Thất Bại", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getBaseContext(),"Không Có Hình Nào Được Chọn.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    // Hàm Kết Quả Hoạt Động Đăng Ảnh
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getBaseContext(), "Đang Tải", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

}

