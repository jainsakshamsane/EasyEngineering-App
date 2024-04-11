package com.easyengineering;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easyengineering.Adapters.CourseAdapter;
import com.easyengineering.Adapters.PurchasedCourseAdapter;
import com.easyengineering.Models.CourseModel;
import com.easyengineering.Models.PurchaseModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment {

    RecyclerView recyclerView;
    List<PurchaseModel> purchaseModels = new ArrayList<>();
    PurchasedCourseAdapter adapter;
    ProgressDialog progressDialog;
    TextView text;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.course_fragment, container, false);

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

        adapter = new PurchasedCourseAdapter(purchaseModels, getContext());

        purchaseRef.orderByChild("userid").equalTo(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Retrieve data from the "purchase" node
                    String coursename = ds.child("coursename").getValue(String.class);
                    String price = ds.child("price").getValue(String.class);
                    String details = ds.child("details").getValue(String.class);
                    String courseid = ds.child("courseid").getValue(String.class);
                    String videourl = ds.child("videourl").getValue(String.class);
                    String purchaseid = ds.child("purchaseid").getValue(String.class);
                    String videoduration = ds.child("videoduration").getValue(String.class);
                    String timestamp = ds.child("timestamp").getValue(String.class);
                    String lasttime = ds.child("lasttime").getValue(String.class);

                    PurchaseModel purchaseModel = new PurchaseModel(coursename, price, details, courseid, videourl, videoduration, timestamp, purchaseid, lasttime);
                    purchaseModels.add(purchaseModel);
                }

                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                // Show the "text" TextView if there are no purchased courses
                if (purchaseModels.isEmpty()) {
                    text.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                // Dismiss the loading dialog once data is loaded
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                progressDialog.dismiss();
            }
        });

        return view;
    }
}
