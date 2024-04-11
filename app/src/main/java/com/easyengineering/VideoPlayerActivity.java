package com.easyengineering;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private ProgressBar progressBar;
    private int currentPosition;
    private SharedPreferences sharedPreferences;
    ImageView back;
    int videostart;
    int durationInSecondss;

    private Handler timerHandler;
    private Runnable timerRunnable;

    String purchaseid,videoUrl,timeleft, formattedDuration, lasttime;

    DatabaseReference reference;
    private CountDownTimer countDownTimer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer_activity);

        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
       // sharedPreferences = getSharedPreferences("VideoPreferences", MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference("purchase");

        // Initialize the timer components
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerHandler.postDelayed(this, 1000); // Update every second
            }
        };

        // Set up a listener to save the playback position when the video is stopped
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Save the playback position and update videoduration in SharedPreferences
                currentPosition = videoView.getCurrentPosition();
                //savePlaybackPosition(currentPosition);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update videoduration in the database
                currentPosition = videoView.getCurrentPosition();
               // Log.e("position from back button", videoView.getCurrentPosition());
                updateVideoDurationInDatabase(currentPosition);
                updatelasttimeindatabase();
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
             videoUrl = bundle.getString("videourl");
            purchaseid = bundle.getString("purchaseid");
             timeleft = bundle.getString("videoduration");
             lasttime = bundle.getString("lasttime");
            int temptime = convertstringtoint(timeleft);
//            countDownTimer = new CountDownTimer(temptime * 1000, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    // Update UI or do something each second if needed
//                   // Toast.makeText(VideoPlayerActivity.this,"countDownTimer"+countDownTimer,Toast.LENGTH_SHORT).show();
//                }
//
//                public void onFinish() {
//                    // Show a popup message or take action when the time limit is reached
//                  // finish();
//                   // Toast.makeText(VideoPlayerActivity.this,"countDownTimer finish"+countDownTimer,Toast.LENGTH_SHORT).show();
//                }
//            };
            showPlaybackOptionsDialog();
            // Set up the MediaController to enable play, pause, etc.
//            CustomMediaController mediaController = new CustomMediaController(VideoPlayerActivity.this);
//            mediaController.setAnchorView(videoView);
//
//            // Set video URI and media controller
//            videoView.setVideoURI(Uri.parse(videoUrl));
//            videoView.setMediaController(mediaController);
//
//            // Show loading progress bar
//            progressBar.setVisibility(View.VISIBLE);

            // Set up a listener to hide the progress bar when the video is ready
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//
//
//
//
//                    progressBar.setVisibility(View.GONE);
//                }
//            });
            //Log.e("time","time out");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the timer when the activity is resumed
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the timer when the activity is paused
     //   timerHandler.removeCallbacks(timerRunnable);
     //   countDownTimer.cancel();
    }



    private void updateVideoDurationInDatabase(int duration) {

        // Update videoduration in the "purchases" node in the database
        DatabaseReference purchaseRef = reference.child(purchaseid);

        // convert timeleft to seconds
        int timeleftupdated = convertstringtoint(timeleft);

        // convert duration to seconds
        int hourss = duration / 3600;
        int minutess = (duration % 3600) / 60;
        int secondss = duration % 60;
        int durationInSeconds = hourss * 3600 + minutess * 60 + secondss;

        String[] partss = formatDuration(durationInSeconds).split(":");
        int hoursss = Integer.parseInt(partss[0]);
        int minutesss = Integer.parseInt(partss[1]);
        int secondsss = Integer.parseInt(partss[2]);
        durationInSecondss = hoursss * 3600 + minutesss * 60 + secondsss;

        // Calculate updated time in seconds
        int updatedtime = timeleftupdated - durationInSecondss;

        // Ensure it's not negative
        updatedtime = Math.max(updatedtime, 0);

        // Convert updatedtime to "HH:mm:ss" format
        formattedDuration = convertSecondsToHHmmss(updatedtime);

        purchaseRef.child("videoduration").setValue(formattedDuration);
    }

    private void updatelasttimeindatabase() {
        DatabaseReference purchaseRef = reference.child(purchaseid).child("lasttime");

        purchaseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // "lasttime" field exists, update its value
                    purchaseRef.setValue(convertSecondsToHHmmss(durationInSecondss));
                }
            } else {
                // Handle the error if necessary
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });
    }



    private String formatDuration(int duration) {
        // Format duration into HH:mm:ss
        int seconds = duration / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
    private int convertstringtoint(String time){
        String[] parts = timeleft.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int timeleftupdated = hours * 3600 + minutes * 60 + seconds;
        return timeleftupdated;
    }
    private String convertSecondsToHHmmss(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    private int convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return hours * 3600 + minutes * 60 + seconds;
    }


    private void showPlaybackOptionsDialog() {
        // Pause the video
       // videoView.pause();
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Play Video");
        builder.setMessage("Choose playback option:");

        builder.setPositiveButton("Start Over", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Pause the video
                videoView.pause();

                int lastTimeInSeconds = convertTimeToSeconds(lasttime);

                int nexttimestart = lastTimeInSeconds;

                final int nextTimeStartInMillis = nexttimestart * 1000;

                // Create a new MediaController
               // MediaController mediaController = new MediaController(VideoPlayerActivity.this);
                CustomMediaController mediaController = new CustomMediaController(VideoPlayerActivity.this);

                mediaController.setAnchorView(videoView);

                // Set video URI and media controller
                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.setMediaController(mediaController);

                // Show loading progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Set up a listener to hide the progress bar when the video is ready
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        videoView.seekTo(nextTimeStartInMillis);

                       // videoView.start();

                       progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        builder.setNegativeButton("From the beginning", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Start the timer when the video playback starts
                timerHandler.postDelayed(timerRunnable, 0);
                // Start the video from the beginning
                videoView.pause();

                // Create a new MediaController
                CustomMediaController mediaController = new CustomMediaController(VideoPlayerActivity.this);
                mediaController.setAnchorView(videoView);

                // Set video URI and media controller
                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.setMediaController(mediaController);

                // Show loading progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Set up a listener to hide the progress bar when the video is ready
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        // Seek to the calculated position
                        videoView.seekTo(0);

                        // Start the video
                        videoView.start();

                        progressBar.setVisibility(View.GONE);
                    }
                });



            }
        });

        // Show the AlertDialog
        builder.create().show();
    }

    public void changeOrientation(View view) {
        // Change orientation logic here
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
