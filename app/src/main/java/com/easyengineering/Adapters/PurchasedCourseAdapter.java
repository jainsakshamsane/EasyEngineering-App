package com.easyengineering.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.easyengineering.Models.CourseModel;
import com.easyengineering.Models.PurchaseModel;
import com.easyengineering.PurchasePageActivity;
import com.easyengineering.R;
import com.easyengineering.VideoPlayerActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchasedCourseAdapter extends RecyclerView.Adapter<PurchasedCourseAdapter.ViewHolder> {

    private List<PurchaseModel> purchaseModels;
    private Context context;

    public PurchasedCourseAdapter(List<PurchaseModel> purchaseModels, Context context) {
        this.purchaseModels = purchaseModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wrapper_purchasedcourses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PurchaseModel purchase = purchaseModels.get(position);

        String timestamps = purchase.getTimestamp();

        // Step 1: Parse timestamp into a Date object
        Date date = parseTimestamp(timestamps);

        // Step 2: Get current date and time
        Date currentDate = new Date();

        // Step 3: Compare dates to determine if it's today, yesterday, or another day
        String dateString = getDateString(date, currentDate);

        // Step 4: Format time into 12-hour format with AM/PM
        String timeString = getTimeString(date);

        holder.purchasedtime.setText("Purchased Time : " + dateString + " at " + timeString);

        holder.name.setText(purchase.getCoursename());
        holder.price.setText("Time Left : " + purchase.getVideoduration());
        holder.lasttime.setText("Last Time Video Played at : " + purchase.getLasttime());

        // Check if videoduration is equal to 00:00:00
        if ("00:00:00".equals(purchase.getVideoduration())) {
            holder.donotplay.setVisibility(View.VISIBLE);
            holder.purchase.setVisibility(View.GONE);

            holder.donotplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Renew Subscription");
                    builder.setMessage("Your watch time for this video has reached the allotted limit. Renew it to enjoy uninterrupted video streaming for the course. Do you want to Renew it?");
                    builder.setPositiveButton("Pay & Renew", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Fetch videoduration from the "course" node in Firebase
                            DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses").child(purchase.getCourseid());

                            // Show a ProgressDialog
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Updating Courses...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Course exists, get videoduration
                                        String videoduration = dataSnapshot.child("videoduration").getValue(String.class);

                                        // Update videoduration in the "purchase" node
                                        DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchase").child(purchase.getPurchaseid());
                                        purchaseRef.child("videoduration").setValue(videoduration);

                                        Toast.makeText(context, "Video duration renewed!", Toast.LENGTH_SHORT).show();
                                        holder.donotplay.setVisibility(View.GONE);
                                        holder.purchase.setVisibility(View.VISIBLE);
                                    } else {
                                        // Course doesn't exist, handle accordingly
                                        Toast.makeText(context, "Error: Course not found", Toast.LENGTH_SHORT).show();
                                    }

                                    // Dismiss the ProgressDialog after the data is updated
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle onCancelled
                                    Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                    // Dismiss the ProgressDialog in case of an error
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle Cancel action here, if needed
                            dialog.dismiss(); // Dismiss the dialog
                        }
                    });

                    // Show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        } else {
            holder.donotplay.setVisibility(View.GONE);
            holder.purchase.setVisibility(View.VISIBLE);

        }

        holder.purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name", purchase.getCoursename());
                bundle.putString("courseid", purchase.getCourseid());
                bundle.putString("details", purchase.getDetails());
                bundle.putString("price", purchase.getPrice());
                bundle.putString("purchaseid", purchase.getPurchaseid());
                bundle.putString("videourl", purchase.getVideourl());
                bundle.putString("videoduration", purchase.getVideoduration());
                bundle.putString("lasttime", purchase.getLasttime());
                // Start the activity and pass the Bundle
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private static Date parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.parse(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDateString(Date date, Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);
        String currentStr = sdf.format(currentDate);

        if (dateStr.equals(currentStr)) {
            return "Today";
        } else {
            // Check if the date is yesterday
            long millisecondsInDay = 24 * 60 * 60 * 1000;
            if (currentDate.getTime() - date.getTime() < millisecondsInDay) {
                return "Yesterday";
            } else {
                return dateStr; // Return the date in yyyy-MM-dd format for other days
            }
        }
    }

    private static String getTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return purchaseModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, purchasedtime, lasttime;
        ImageView purchase, donotplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.coursename);
            price = itemView.findViewById(R.id.price);
            purchase = itemView.findViewById(R.id.purchase);
            purchasedtime = itemView.findViewById(R.id.purchasedtime);
            donotplay = itemView.findViewById(R.id.donotplay);
            lasttime = itemView.findViewById(R.id.lasttime);
        }
    }
}
