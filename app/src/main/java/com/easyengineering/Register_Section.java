package com.easyengineering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.easyengineering.Models.RegisterModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Register_Section extends Fragment {

    EditText firstname, lastname, email, mobile, password;
    TextView register;
    RadioButton instructorbutton, studentbutton;
    RadioGroup radiogroup;
    RadioButton selectedradiobutton;
    String firstnames, lastnames, emails, mobiles, passwords, category;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedbundleinstance) {

        View view = layoutInflater.inflate(R.layout.register_section, container, false);

        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        email = view.findViewById(R.id.email);
        mobile = view.findViewById(R.id.mobile);
        password = view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        instructorbutton = view.findViewById(R.id.instructorbutton);
        studentbutton = view.findViewById(R.id.studentbutton);
        radiogroup = view.findViewById(R.id.radiogroup);

        database = FirebaseDatabase.getInstance();

        radiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedradiobutton = view.findViewById(checkedId);
            if (checkedId == R.id.instructorbutton) {
                reference = database.getReference("instructor");
            } else if (checkedId == R.id.studentbutton) {
                reference = database.getReference("student");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstnames = firstname.getText().toString();
                lastnames = lastname.getText().toString();
                emails = email.getText().toString();
                mobiles = mobile.getText().toString();
                passwords = password.getText().toString();
                category = selectedradiobutton != null ? selectedradiobutton.getText().toString() : "";

                if (firstnames.isEmpty() || lastnames.isEmpty() || emails.isEmpty() || mobiles.isEmpty() || passwords.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all details", Toast.LENGTH_SHORT).show();
                } else if (!emails.toLowerCase().endsWith("@gmail.com")) {
                    Toast.makeText(getContext(), "Email must end with @gmail.com", Toast.LENGTH_SHORT).show();
                } else if (selectedradiobutton == null) {
                    Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference idRef = reference.push();
                    String id = idRef.getKey();

                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    RegisterModel registerModel = new RegisterModel(id, firstnames, lastnames, emails, mobiles, passwords, category, timestamp);
                    reference.child(id).setValue(registerModel);

                    Toast.makeText(getContext(), "Registered Sucessfully", Toast.LENGTH_SHORT).show();

                    firstname.setText("");
                    lastname.setText("");
                    email.setText("");
                    mobile.setText("");
                    password.setText("");
                }
            }
        });

        return view;
    }
}
