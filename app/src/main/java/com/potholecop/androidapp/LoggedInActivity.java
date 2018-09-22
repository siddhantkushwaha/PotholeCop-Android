package com.potholecop.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.potholecop.androidapp.fragments.FeedFragment;
import com.potholecop.androidapp.fragments.LocalFragment;
import com.potholecop.androidapp.fragments.ProfileFragment;

public class LoggedInActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FeedFragment feedFragment = FeedFragment.newInstance();
                    openFragment(feedFragment);
                    return true;
                case R.id.navigation_dashboard:
                    LocalFragment localFragment = LocalFragment.newInstance();
                    openFragment(localFragment);
                    return true;
                case R.id.navigation_notifications:
                    ProfileFragment profileFragment = ProfileFragment.newInstance();
                    openFragment(profileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void openFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_report_pothole:
                intent = new Intent(LoggedInActivity.this, AddPothole.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(LoggedInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
