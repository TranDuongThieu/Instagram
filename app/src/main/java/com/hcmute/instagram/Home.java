package com.hcmute.instagram;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hcmute.instagram.Like.LikeFragment;
import com.hcmute.instagram.Post.PostActivity;
import com.hcmute.instagram.Profile.Account_Settings;
import com.hcmute.instagram.Profile.EditProfile;
import com.hcmute.instagram.Profile.ProfileFragment;
import com.hcmute.instagram.Search.SearchFragment;
import com.hcmute.instagram.home.HomeFragment;
import com.hcmute.instagram.R;
import com.hcmute.instagram.models.Users;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    CircleImageView avt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(userId);
        ArrayList<ImageView> imageList = new ArrayList<>();
        imageList.add(findViewById(R.id.action_home));
        imageList.add(findViewById(R.id.action_search));
        imageList.add(findViewById(R.id.action_post));
        imageList.add(findViewById(R.id.action_video));
        CircleImageView avt = findViewById(R.id.action_profile);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users user = documentSnapshot.toObject(Users.class);
                if (user != null) {
                    Glide.with(Home.this)
                            .load(user.getProfilePhoto())
                            .into(avt);
                } else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Home.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                }
            }
        });
        avt.setAlpha(0.6f);
        setActive(imageList, findViewById(R.id.action_home));
        replaceFragment(new HomeFragment());
    }

    public void onItemClick(View view) {
        ImageView clickedImageView = (ImageView) view;
        ArrayList<ImageView> imageList = new ArrayList<>();
        imageList.add(findViewById(R.id.action_home));
        imageList.add(findViewById(R.id.action_search));
        imageList.add(findViewById(R.id.action_post));
        imageList.add(findViewById(R.id.action_video));
        CircleImageView avt = findViewById(R.id.action_profile);
        if (clickedImageView == imageList.get(0)) {
            replaceFragment(new HomeFragment());
        } else if (clickedImageView == imageList.get(1)) {
            replaceFragment(new SearchFragment());
        } else if (clickedImageView == imageList.get(2)) {
            startActivity(new Intent(Home.this, PostActivity.class));
        } else if (clickedImageView == imageList.get(3)) {
//            replaceFragment(new ReelFragment());
        }
        avt.setAlpha(0.6f);
        setActive(imageList, clickedImageView);

    }

    public void onItemClick2(View view) {
        CircleImageView clickedImageView = (CircleImageView) view;
        ArrayList<ImageView> imageList = new ArrayList<>();
        imageList.add(findViewById(R.id.action_home));
        imageList.add(findViewById(R.id.action_search));
        imageList.add(findViewById(R.id.action_post));
        imageList.add(findViewById(R.id.action_video));
        CircleImageView avt = findViewById(R.id.action_profile);
        avt.setAlpha(1.0f);
        for (ImageView imageView : imageList) {
            imageView.setAlpha(0.6f);
        }
        replaceFragment(new ProfileFragment());
    }

    private void setActive(ArrayList<ImageView> imgList, ImageView active) {
        for (ImageView imageView : imgList) {
            imageView.setAlpha(imageView == active ? 1.0f : 0.6f);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameMainLayout, fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        Fragment fragment = null;
//        switch (item.getItemId()) {
//            case R.id.Home:
//                fragment = new HomeFragment();
//                break;
//
//            case R.id.search:
//                fragment = new SearchFragment();
//                break;
//
//            case R.id.post:
//                // No need to set a fragment here, just start the PostActivity
//                startActivity(new Intent(Home.this, PostActivity.class));
//                return true;
//
//            case R.id.likes:
//                fragment = new LikeFragment();
//                break;
//
//            case R.id.profile:
//                fragment = new ProfileFragment();
//                break;
//        }
    // Load the selected fragment
//        return loadFragment(fragment);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
