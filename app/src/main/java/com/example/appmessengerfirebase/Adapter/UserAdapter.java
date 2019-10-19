package com.example.appmessengerfirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appmessengerfirebase.MessageActivity;
import com.example.appmessengerfirebase.Model.User;
import com.example.appmessengerfirebase.R;

import java.util.List;

// Part 5
// Chuyển đổi dữ liệu nhận được từ UsersFragment lên các Control TextView và ImageView

// Part 6 Message class
// Sự kiện khi chọn 1 tài khoản vào khung chat thì thông tin user được chon sẽ
// được gởi qua cho MessageAcitity xử lý

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers; // danh sách người dùng

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.userName.setText(user.getUserName()); // lấy tên tài khoản sở hữu

        // gắn hình mặt định cho user nào không có hình
        if(user.getImage().equals("default")){
            holder.profile_image_userItem.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImage()).into(holder.profile_image_userItem);
        }

        // part 12
        // Hiển thị dấu chấm trạng thái khi tài khoản
            if(user.getStatus().equals("online")) {
                holder.image_on.setVisibility(View.VISIBLE);
                holder.image_off.setVisibility(View.GONE);
            } else {
                holder.image_on.setVisibility(View.GONE);
                holder.image_off.setVisibility(View.VISIBLE);
            }
        // Over Part 12

        // Part 6 this is event it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
        // Over part 6

    }

    // đếm sô tài khoản trênm Firebase khác với tài khoản đang sử dụng
    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    // gán giá trị vào control để hiển thị tên và hình đại diện
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public ImageView profile_image_userItem;
        public ImageView image_off, image_on;

        public ViewHolder(View itemView){
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            profile_image_userItem = itemView.findViewById(R.id.profile_image_userItem);
            image_off = itemView.findViewById(R.id.image_off);
            image_on = itemView.findViewById(R.id.image_on);

        }
    }
}
