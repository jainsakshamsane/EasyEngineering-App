package com.easyengineering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login_Section extends Fragment {

    EditText email, password;
    TextView login;
    RadioButton instructorbutton, studentbutton;
    RadioGroup radiogroup;
    RadioButton selectedradiobutton;
    String emails, passwords, category, userId;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        View view = layoutInflater.inflate(R.layout.login_section, container, false);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        instructorbutton = view.findViewById(R.id.instructorbutton);
        studentbutton = view.findViewById(R.id.studentbutton);
        radiogroup = view.findViewById(R.id.radiogroup);
        login = view.findViewById(R.id.login);

        database = FirebaseDatabase.getInstance();

        radiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedradiobutton = view.findViewById(checkedId);
            if (checkedId == R.id.instructorbutton) {
                reference = database.getReference("instructor");
            } else if (checkedId == R.id.studentbutton) {
                reference = database.getReference("student");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emails = email.getText().toString().trim();
                passwords = password.getText().toString().trim();
                category = selectedradiobutton != null ? selectedradiobutton.getText().toString() : "";

                if (emails.isEmpty() || passwords.isEmpty() || category.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
                } else {
                    Query checkUserDatabase;

                    if (emails.contains("@")) {
                        // The entered value looks like an email
                        checkUserDatabase = reference.orderByChild("email").equalTo(emails);
                    } else {
                        // The entered value looks like a phone number
                        checkUserDatabase = reference.orderByChild("mobile").equalTo(emails);
                    }

                    checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                                    String categoryFromDB = userSnapshot.child("category").getValue(String.class);

                                    if (passwordFromDB != null && passwordFromDB.equals(passwords) && categoryFromDB != null && categoryFromDB.equals(category)) {
                                        // Successfully logged in
                                        email.setError(null);

                                        String firstnameFromDB = userSnapshot.child("firstname").getValue(String.class);
                                        String lastnameFromDB = userSnapshot.child("lastname").getValue(String.class);
                                        String mobileFromDB = userSnapshot.child("mobile").getValue(String.class);
                                        String idFromDB = userSnapshot.child("id").getValue(String.class);

                                        // Store user information in SharedPreferences
                                        storeUserInformation(firstnameFromDB, lastnameFromDB, mobileFromDB, idFromDB, categoryFromDB);

                                        Toast.makeText(getContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();

                                        if ("Instructor".equals(categoryFromDB)) {
                                            // If the user is an instructor, go to AddvideoActivity
                                            Intent intent = new Intent(getContext(), AddvideoActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // If the user is a student, go to MainActivity
                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        password.setError("Invalid Credentials");
                                        password.requestFocus();
                                    }
                                }
                            } else {
                                email.setError("User does not exist");
                                email.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }
        });

        return view;
    }

    // Add a new method to store user information in SharedPreferences
    private void storeUserInformation(String firstname, String lastname, String mobile, String userId, String category) {
        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencess.edit();
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putString("mobile", mobile);
        editor.putString("userId", userId);
        editor.putString("category", category);
        editor.apply();
    }
}
