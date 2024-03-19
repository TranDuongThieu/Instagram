package com.hcmute.instagram;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.hcmute.instagram.Like.LikeFragment;
import com.hcmute.instagram.Post.PostActivity;
import com.hcmute.instagram.Profile.ProfileFragment;
import com.hcmute.instagram.Search.SearchFragment;
import com.hcmute.instagram.home.HomeFragment;
import com.hcmute.instagram.R;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigationView = findViewById(R.id.insta_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Load HomeFragment by default
        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
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
        return loadFragment(fragment);
    }

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
