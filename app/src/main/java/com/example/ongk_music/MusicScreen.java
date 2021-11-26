package com.example.ongk_music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicScreen extends AppCompatActivity {
    Button btnLogout, btnBack;
    TextView txtDuration,txtPosition;
    ImageView btnPlay,btnPause,btnBackward,btnForward;
    GoogleSignInClient mGoogleSignInClient;
    CircleImageView imgHinh;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogout=findViewById(R.id.btnLogout);
        btnBack=findViewById(R.id.btnBack);
        btnPause=findViewById(R.id.btnPause);
        btnPlay=findViewById(R.id.btnPlay);
        btnBackward=findViewById(R.id.btnBackward);
        btnForward=findViewById(R.id.btnForward);
        txtPosition=findViewById(R.id.txtPosition);
        txtDuration=findViewById(R.id.txtDuration);
        seekBar=findViewById(R.id.seekBar);
        imgHinh=findViewById(R.id.imgHinh);
        mediaPlayer = MediaPlayer.create(this,R.raw.gangnamstyle);

        runnable = new Runnable() {
            @Override
            public void run() {
                //Set progress on seek bar
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                //Handler post delay for 0.5 second
                handler.postDelayed(this, 500);
            }
        };

        //Get duration of media player
        int duration = mediaPlayer.getDuration();
        //Convert millisecond to minute and second
        String sDuration = convertFormat(duration);
        //Set duration on text view
        txtDuration.setText(sDuration);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();

                mediaPlayer.start();

                seekBar.setMax(mediaPlayer.getDuration());

                handler.postDelayed(runnable, 0);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAnimation();
                mediaPlayer.pause();
                //Stop handler
                handler.removeCallbacks(runnable);
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get current position of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                //Get duration of media player
                int duration = mediaPlayer.getDuration();
                //Check condition
                if (mediaPlayer.isPlaying() && duration != currentPosition) {
                    //When media is playing and duration is not equal to current postion
                    //Fast forward for 5 seconds
                    currentPosition = currentPosition + 5000;
                    //Set current position on text view
                    txtPosition.setText(convertFormat(currentPosition));
                    //Set progress on seekbar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get current positon of media player
                int currentPosition = mediaPlayer.getCurrentPosition();
                //Check condition
                if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                    //When media is playing and current postion in greater than 5 seconds
                    currentPosition = currentPosition - 5000;
                    //Get current position on text view
                    txtPosition.setText(convertFormat(currentPosition));
                    //Set progress on seekbar
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Check condition
                if(fromUser) {
                    //When drag the seek bar
                    //Set progress on seek bar
                    mediaPlayer.seekTo(progress);
                }
                //Set current position on text view
                txtPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Hide pause button
                btnPause.setVisibility(View.GONE);
                //Show play button
                btnPlay.setVisibility(View.VISIBLE);
                //Set media player to initial position
                mediaPlayer.seekTo(0);
            }
        });






        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicScreen.this,MainActivity.class);

                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MusicScreen.this,btnBack,
                        ViewCompat.getTransitionName(btnBack)
                );
                startActivity(intent, optionsCompat.toBundle());
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnLogout:
                        signOut();
                        break;
                }
            }
        });

    }

    public void startAnimation(){
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                imgHinh.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
            }
        };
        imgHinh.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    public void stopAnimation(){
        imgHinh.animate().cancel();
    }


    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d"
                , TimeUnit.MILLISECONDS.toMinutes(duration)
                , TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes(duration))));
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MusicScreen.this,"Logout succesful",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}