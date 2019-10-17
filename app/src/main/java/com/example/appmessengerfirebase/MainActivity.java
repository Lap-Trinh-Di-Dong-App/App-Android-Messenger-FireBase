package com.example.appmessengerfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.appmessengerfirebase.Fragment.ChatsFragment;
import com.example.appmessengerfirebase.Fragment.UsersFragment;
import com.example.appmessengerfirebase.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


// Part 2 đăng hập tài khoản
// Part 3 Logout Tài khoản dưới dạng Menu
/* Part 4 Tạo 2 View Page Chats và Users
   - Gắn các Users khác tài khoản đăng nhập lên View Page Users
 */
// Part 12 hiển thị Status của tài khoản

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView userName;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Thanh toolbar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); // Ẩn thanh toolbar chính

        setActionBar(toolbar);
        getActionBar().setTitle("");


        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        userName = (TextView) findViewById(R.id.userName_Start);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // truyền dữ liệu từ Firebase xuống User Class
                User user = dataSnapshot.getValue(User.class);

                userName.setText(user.getUserName());

                if (user.getImage().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MainActivity.this).load(user.getImage()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Part 4
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Chèn 2 page Chat và User
        viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        viewPagerAdapter.addFragment(new UsersFragment(), "User");

        // chèn dữ liệu xuống control ViewPager và TabLayout
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        // end part 4
    }

    // Menu Logout part 3
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            case R.id.user_profile:
                Intent itent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(itent);
                return true;



        }
        return false;

    }
    // end Menu part 3


    // ViewPager part 4
    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // add fragmet and title
        public void addFragment(Fragment fragmnet, String title){
            fragments.add(fragmnet);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }

    }

    // Part 12 update Users thêm thuộc tính Status
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

}
