package com.easyengineering;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ImageView logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        // Set the login status in shared preferences
        SharedPreferences sharedPreferencessss = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencessss.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

    }

    public void onSideMenuItemClick(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);

        // Assume you have a method to retrieve user information
        // For example, getUserInformation() returns a User object with name and phone
        User user = getUserInformation();

        if (user != null) {
            TextView nameTextView = findViewById(R.id.name); // Assume this is the TextView in your navigation header
            TextView phoneTextView = findViewById(R.id.phone); // Assume this is the TextView for phone in your navigation header

            // Set the user's name and phone in the NavigationView header
            nameTextView.setText("Welcome, " + user.getName());
            phoneTextView.setText("  " + user.getPhone());
        }

        if (view.getId() == R.id.menu_item1) {
            // Handle "My Courses" selected
            // You can open a new fragment or perform any other action
        } else if (view.getId() == R.id.menu_item2) {
            // Handle "Packages" selected
        } else if (view.getId() == R.id.menu_item3) {
            // Handle "Profile" selected
        } else if (view.getId() == R.id.menu_item4) {
            // Handle "Explore" selected
        } else if (view.getId() == R.id.menu_item5) {
            // Handle "About" selected
        } else if (view.getId() == R.id.menu_item6) {
            // Handle "Help" selected
        }
//        else if (view.getId() == R.id.menu_item7) {
//            // Handle "Logout" selected
//            // logoutUser();
//        }
    }

    // Example User class (adjust as per your user model)
    private static class User {
        private String name;
        private String phone;

        public User(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }
    }

    // Assume you have a method to retrieve user information
    private User getUserInformation() {
        // Replace this with your logic to retrieve user information
        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        String name = sharedPreferencess.getString("firstname", "") + " " + sharedPreferencess.getString("lastname", "");
        String phone = sharedPreferencess.getString("mobile", "");
        String userid = sharedPreferencess.getString("userId", "");
        return new User(name, phone);
    }


    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all preferences, including the switch state
        editor.clear();
        editor.apply();

        // Navigate to the login page
        Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
        Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedfragment = null;

            if (item.getItemId() == R.id.home) {
                selectedfragment = new HomeFragment();
            } else if (item.getItemId() == R.id.course) {
                selectedfragment = new CourseFragment();
            } else if (item.getItemId() == R.id.explore) {
                selectedfragment = new ExploreFragment();
            }

            if (selectedfragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfragment).commit();
                return true;
            }
            return false;
        }
    };
}
