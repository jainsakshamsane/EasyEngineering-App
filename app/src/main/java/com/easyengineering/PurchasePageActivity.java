package com.easyengineering;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PurchasePageActivity extends AppCompatActivity {

    TextView purchase, coursename, courseprice, coursedetails, duration;
    ImageView video;
    ImageView back;
    FirebaseDatabase database;
    DatabaseReference reference;
    String purchaseid, name, courseid, details, price, videourl, videoduration;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchasepage_activity);

        back = findViewById(R.id.back);
        purchase = findViewById(R.id.purchase);
        coursename = findViewById(R.id.coursename);
        courseprice = findViewById(R.id.price);
        duration = findViewById(R.id.duration);
        coursedetails = findViewById(R.id.description);
        video = findViewById(R.id.video);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name", "");
            courseid = bundle.getString("courseid", "");
            details = bundle.getString("details", "");
            price = bundle.getString("price", "");
            videourl = bundle.getString("videourl", "");
            videoduration = bundle.getString("videoduration", "");

            coursename.setText(name);
            courseprice.setText("Rs. " + price + "/-");
            coursedetails.setText(details);
            duration.setText(videoduration);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchasePageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
                String userid = sharedPreferencess.getString("userId", "");

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("purchase");

                DatabaseReference idRef = reference.push();
                purchaseid = idRef.getKey();

                String lasttime = convertSecondsToHHmmss(0);

                String timestamp = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                reference.child(purchaseid).child("purchaseid").setValue(purchaseid);
                reference.child(purchaseid).child("coursename").setValue(name);
                reference.child(purchaseid).child("details").setValue(details);
                reference.child(purchaseid).child("videourl").setValue(videourl);
                reference.child(purchaseid).child("price").setValue(price);
                reference.child(purchaseid).child("timestamp").setValue(timestamp);
                reference.child(purchaseid).child("userid").setValue(userid);
                reference.child(purchaseid).child("courseid").setValue(courseid);
                reference.child(purchaseid).child("videoduration").setValue(videoduration);
                reference.child(purchaseid).child("lasttime").setValue(lasttime);

                SweetAlertDialog successDialog = new SweetAlertDialog(PurchasePageActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("Thank you for purchasing")
                        .setConfirmText("Go to Home Page")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                // Dismiss the SweetAlertDialog
                                sweetAlertDialog.dismiss();

                                // Show AlertDialog with loading message
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PurchasePageActivity.this);
                                alertDialogBuilder.setMessage("Redirecting to homepage...");
                                alertDialogBuilder.setCancelable(false);

                                // Create the AlertDialog
                                final AlertDialog alertDialog = alertDialogBuilder.create();

                                // Handler to delay the dismissal of AlertDialog after 3 seconds
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Dismiss the AlertDialog
                                        alertDialog.dismiss();

                                        // Handle navigation to the next page here
                                        // For example, start a new activity
                                        startActivity(new Intent(PurchasePageActivity.this, MainActivity.class));

                                        // Finish the current activity
                                        finish();
                                    }
                                }, 1000);

                                // Show the AlertDialog
                                alertDialog.show();
                            }
                        });
                successDialog.show();
            }
        });
    }

    private String convertSecondsToHHmmss(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
