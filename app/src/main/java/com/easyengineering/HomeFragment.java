package com.easyengineering;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyengineering.Adapters.CourseAdapter;
import com.easyengineering.Models.CourseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<CourseModel> courseModelList = new ArrayList<>();
    CourseAdapter adapter;
    ProgressDialog progressDialog;
    TextView text;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        text = view.findViewById(R.id.text);

        SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("user_information", MODE_PRIVATE);
        String userid = sharedPreferencess.getString("userId", "");

        DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchase");
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses");

        adapter = new CourseAdapter(courseModelList, getContext());

        purchaseRef.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> purchasedCourseIds = new ArrayList<>();

                // Get the list of purchased course IDs
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String purchasedCourseId = ds.child("courseid").getValue(String.class);
                    purchasedCourseIds.add(purchasedCourseId);
                }

                // Query courses that are not purchased by the current user
                courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot courseSnapshot) {
                        courseModelList.clear();

                        for (DataSnapshot courseDS : courseSnapshot.getChildren()) {
                            String courseid = courseDS.child("courseid").getValue(String.class);

                            // Only add courses that are not purchased by the current user
                            if (!purchasedCourseIds.contains(courseid)) {
                                String coursename = courseDS.child("coursename").getValue(String.class);
                                String price = courseDS.child("price").getValue(String.class);
                                String videourl = courseDS.child("videourl").getValue(String.class);
                                String coursedetails = courseDS.child("details").getValue(String.class);
                                String videoduration = courseDS.child("videoduration").getValue(String.class);

                                CourseModel courseModel = new CourseModel(courseid, coursename, price, videourl, coursedetails, videoduration);
                                courseModelList.add(courseModel);
                            }
                        }

                        // Notify adapter after the loop to avoid unnecessary multiple notifications
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                        if (courseModelList.isEmpty()) {
                            text.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        // Dismiss the loading dialog once data is loaded
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Dismiss the loading dialog in case of error
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Dismiss the loading dialog in case of error
                progressDialog.dismiss();
            }
        });

        return view;
    }
}