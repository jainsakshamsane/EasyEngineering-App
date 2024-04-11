package com.easyengineering;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AddvideoActivity extends AppCompatActivity {

    EditText coursename, details, price;
    TextView savechanges, logout, text;
    ImageView uploadvideo;
    VideoView videoView;
    MediaController mediaController;
    String videoUrl, id, videoDuration;
    private Uri filePath;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_VIDEO_REQUEST = 22;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addvideo_activity);

        coursename = findViewById(R.id.coursename);
        details = findViewById(R.id.details);
        price = findViewById(R.id.price);
        logout = findViewById(R.id.logout);
        savechanges = findViewById(R.id.savedetails);
        uploadvideo = findViewById(R.id.uploadvideo);
        videoView = findViewById(R.id.VideoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        text = findViewById(R.id.text);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("courses");

        videoView.setVisibility(View.GONE);

        logout.setOnClickListener(v -> logoutUser());

        SharedPreferences sharedPreferencess = getSharedPreferences("user_information", MODE_PRIVATE);
        String firstname = sharedPreferencess.getString("firstname", "");
        String lastname = sharedPreferencess.getString("lastname", "");
        String mobile = sharedPreferencess.getString("mobile", "");
        String instructorId = sharedPreferencess.getString("userId", "");
        String category = sharedPreferencess.getString("category", "");

        text.setText("Welcome, " + firstname + " " + lastname);

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedPreferences.edit();
        editors.putString("role", category);
        editors.apply();

        // Set the login status in shared preferences
        SharedPreferences sharedPreferencessss = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencessss.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectVideo();
            }
        });

        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namecourse = coursename.getText().toString();
                String courseprice = price.getText().toString();
                String detailscourse = details.getText().toString();

                if (namecourse.isEmpty() || detailscourse.isEmpty() || courseprice.isEmpty()) {
                    Toast.makeText(AddvideoActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (videoUrl == null || videoUrl.isEmpty()) {
                    Toast.makeText(AddvideoActivity.this, "Please upload video", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference idRef = reference.push();
                id = idRef.getKey();

                String timestamp = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                reference.child(id).child("coursename").setValue(namecourse);
                reference.child(id).child("price").setValue(courseprice);
                reference.child(id).child("details").setValue(detailscourse);
                reference.child(id).child("videourl").setValue(videoUrl);
                reference.child(id).child("timestamp").setValue(timestamp);
                reference.child(id).child("videoduration").setValue(videoDuration);
                reference.child(id).child("instructorid").setValue(instructorId);
                reference.child(id).child("courseid").setValue(id);

                Toast.makeText(AddvideoActivity.this, "Course Uploaded successfully!", Toast.LENGTH_SHORT).show();

                coursename.setText("");
                price.setText("");
                details.setText("");
                uploadvideo.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                videoUrl = "";
            }
        });
    }

    private void SelectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video from here..."), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                videoView.setVideoURI(filePath);
                videoView.start();
                uploadVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadVideo() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("videos/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    videoUrl = downloadUri.toString();

                                    // Get the video duration
                                    videoDuration = getVideoDuration(filePath);

                                    Toast.makeText(AddvideoActivity.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                                    videoView.setVisibility(View.VISIBLE);
                                    uploadvideo.setVisibility(View.GONE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddvideoActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    // Method to get the video duration
    private String getVideoDuration(Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, videoUri);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long durationMillis = Long.parseLong(duration);

        // Convert duration to a human-readable format
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % TimeUnit.MINUTES.toSeconds(1);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void logoutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddvideoActivity.this);
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
        Intent intent = new Intent(AddvideoActivity.this, LoginRegisterActivity.class);
        Toast.makeText(AddvideoActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
