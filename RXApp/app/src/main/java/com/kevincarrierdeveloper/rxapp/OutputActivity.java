package com.kevincarrierdeveloper.rxapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class OutputActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");
        String mp3 = intent.getExtras().getString("mp3");
        String image = intent.getExtras().getString("image");
        String description = intent.getExtras().getString("description");
        String name = intent.getExtras().getString("name");
        setTitle(name);

        TextView tvDescription = (TextView)findViewById(R.id.txtDescription);
        tvDescription.setText(description);

        ImageView imageView = (ImageView) findViewById(R.id.loadImage);
        Picasso.with(this).load(image).into(imageView);

        ding(mp3);
    }

    private void ding(final String url) {
        try {
            MediaPlayer player = new MediaPlayer();


            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "play media");
                    Log.d(TAG, "URL: " + url);

                }
            });
            //Stops Looping
            player.setLooping(false);
            player.prepare();
            player.start();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
