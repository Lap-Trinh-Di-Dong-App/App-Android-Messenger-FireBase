package com.example.appmessengerfirebase.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmessengerfirebase.Adapter.UserAdapter;
import com.example.appmessengerfirebase.Model.User;
import com.example.appmessengerfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/* Part 4,5
   - Đưa các Users khác tài khoản đang dùng vào danh sách mUsers
   - Dùng UserAdapter để chuyển dữ các item trong mUsers đưa lên Control TextView và ImageView
   - Gắn các item trong danh sách mUsers đã qua UserAdapter lên RecycleView
 */

public class UsersFragment extends Fragment {

    private RecyclerView recycleView;

    private UserAdapter userAdapter;
    private List<User> mUsers; // Tạo danh sách ngừoi dùng

    EditText edit_SerchUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users,container,false);

        recycleView = view.findViewById(R.id.recycler_view);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUsers();

        // Part 13
        edit_SerchUser = view.findViewById(R.id.edit_SearchUser);
        edit_SerchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // Over Part 13

        return view;
    }

    // Hàm đọc ngừoi dùng
    private void readUsers() {

        // Lấy Tài Khoản đang sử dụng
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear danh sách khi chạy
                mUsers.clear();

                // Duyệt dữ liệu từ Firebase
                for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                    User user = snapShot.getValue(User.class);

                    // Xác nhận user và firebaseUser khác rỗng
                    assert user != null;
                    assert firebaseUser != null;

                    // Thêm những tài khoản khác tài khoản đang sử dụng vào danh sách
                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                }

                // Chuyển đổi các thông tin và gắn vào các control trên RecycleView
                userAdapter = new UserAdapter(getContext(), mUsers);
                recycleView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Part 13
    private void searchUser(String search){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(search).endAt(search+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers);
                recycleView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    // Over part 13


}
