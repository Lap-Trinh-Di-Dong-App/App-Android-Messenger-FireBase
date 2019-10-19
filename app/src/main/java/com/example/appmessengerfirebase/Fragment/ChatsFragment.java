package com.example.appmessengerfirebase.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appmessengerfirebase.Adapter.UserAdapter;
import com.example.appmessengerfirebase.Model.Chat;
import com.example.appmessengerfirebase.Model.User;
import com.example.appmessengerfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


// Part 9 hiển thị danh  các user đã nhắn tin với user đang sử dụng lên RecycleView

public class ChatsFragment extends Fragment {

    private RecyclerView recycleView;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<String> usersList;

    FirebaseUser firebaseUser;
    DatabaseReference referencee;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats,container,false);

        recycleView = view.findViewById(R.id.recycle_view);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        // lấy tài khoản đăng nhập hiện tại
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        // lấy dữ liệu từ bảng Chats trên database xuống
        referencee = FirebaseDatabase.getInstance().getReference("Chats");
        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                // Duyệt danh sách bảng Chats trên Firebase
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    // Nếu ngừoi gởi hoặc người nhận có id trùng với id user hiện tại thì thêm vào danh sách UserList
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())){
                        usersList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


    public void readChats(){
        mUsers = new ArrayList<>();
        // lấy dữ liệu từ bảng Chats trên database xuống
        referencee = FirebaseDatabase.getInstance().getReference("Users");

        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                // Hiển thị user từ danh sách Chats
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    // Duyệt userList để tìm user có id trong danh sách
                    // còn lỗi
                    for (String id : usersList) {
                        if(user.getId().equals(id)) {
                            // lấy tất cả các user đã nhắn tin trong danh sách ra
                          if(mUsers.size() != 0) {
                              int i = 0;
                              while (i < mUsers.size()) {
                                  User u;
                                  u = mUsers.get(i);
                                  if (!user.getId().equals(u.getId())) {
                                      mUsers.add(user);
                                  }
                                  i++;
                              }
                          } else {
                              mUsers.add(user);
                          }
                        }
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


}
