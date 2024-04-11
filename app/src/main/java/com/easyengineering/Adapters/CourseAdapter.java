package com.easyengineering.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyengineering.Models.CourseModel;
import com.easyengineering.PurchasePageActivity;
import com.easyengineering.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<CourseModel> courseModelList;
    private Context context;

    public CourseAdapter(List<CourseModel> courseModelList, Context context) {
        this.courseModelList = courseModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wrapper_courses, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final CourseModel course = courseModelList.get(position);

        int maxLength = 18; // set your desired maximum length
        String displayedCourseName = course.getCoursename();

        if (displayedCourseName.length() > maxLength) {
            // If length exceeds, truncate and append "..."
            displayedCourseName = displayedCourseName.substring(0, maxLength) + "...";
        }

        holder.name.setText(displayedCourseName);
        holder.price.setText("Rs. " + course.getPrice() + "/-");
        holder.details.setText(course.getDetails());

//        String videoUrl = course.getVideourl();
//
//        // Set the video URL to the VideoView
//        if (videoUrl != null && !videoUrl.isEmpty()) {
//            holder.video.setVideoURI(Uri.parse(videoUrl));
//            holder.video.start();
//        }

        holder.purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("name", course.getCoursename());
                bundle.putString("courseid", course.getCourseid());
                bundle.putString("details", course.getDetails());
                bundle.putString("price", course.getPrice());
                bundle.putString("videourl", course.getVideourl());
                bundle.putString("videoduration", course.getVideoduration());
                // Start the activity and pass the Bundle
                Intent intent = new Intent(context, PurchasePageActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, purchase, details;
        ImageView video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.coursename);
            price = itemView.findViewById(R.id.price);
            video = itemView.findViewById(R.id.video);
            purchase = itemView.findViewById(R.id.purchase);
            details = itemView.findViewById(R.id.details);
        }
    }
}
