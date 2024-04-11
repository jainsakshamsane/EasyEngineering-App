package com.easyengineering;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FlashScreenActivity extends AppCompatActivity {

    TextView login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashscreen_activity);

        login = findViewById(R.id.login);

                // Check if the user is already logged in
                if (isLoggedIn()) {
                    // If logged in, check role and redirect accordingly
                    if (isInstructor()) {
                        // If an instructor is logged in, go to AddvideoActivity
                        Intent intent = new Intent(FlashScreenActivity.this, AddvideoActivity.class);
                        startActivity(intent);
                    } else {
                        // If a student is logged in, go to MainActivity
                        Intent intent = new Intent(FlashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // If not logged in, go to LoginRegisterActivity
                    Intent intent = new Intent(FlashScreenActivity.this, LoginRegisterActivity.class);
                    startActivity(intent);
                }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlashScreenActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isLoggedIn() {
        // Check your login status logic using shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private boolean isInstructor() {
        // Implement your logic to check if the user is an instructor
        // You might use an additional shared preference to store the role
        // For example, if the role is "instructor," return true; otherwise, return false
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        return role.equals("Instructor");
    }
}
