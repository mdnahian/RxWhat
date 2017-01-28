package com.kevincarrierdeveloper.rxapp;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class OutputActivity extends AppCompatActivity implements View.OnClickListener{

    String dosage;
    String name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");
        String mp3 = intent.getExtras().getString("mp3");
        String image = intent.getExtras().getString("image");
        String description = intent.getExtras().getString("description");
        name = intent.getExtras().getString("name");
        setTitle(name);
        dosage = intent.getExtras().getString("dosage");


        TextView tvDosage = (TextView)findViewById(R.id.txtDosage);
        tvDosage.setText(dosage);

        TextView tvDescription = (TextView)findViewById(R.id.txtDescription);
        tvDescription.setText(description);

        ImageView imageView = (ImageView) findViewById(R.id.loadImage);
        Picasso.with(this).load(image).into(imageView);

       /* Button btnReminders = (Button) findViewById(R.id.btnReminders);
       if(dosage == null)
       {
           //btnReminders
       }*/
        if(dosage != null){
            tvDosage.setVisibility(View.VISIBLE);
        }
        else
        {
            tvDosage.setVisibility(View.GONE);
        }

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

    @Override
    public void onClick(View v){
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");

        Calendar begin = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY,9);
        begin.set(Calendar.MINUTE,0);
        begin.set(Calendar.SECOND,0);
        begin.set(Calendar.MILLISECOND,0);

        Date dBegin = begin.getTime();

        intent.putExtra("beginTime",dBegin);
        //intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        //intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY,9);
        end.set(Calendar.MINUTE,30);
        end.set(Calendar.SECOND,0);
        end.set(Calendar.MILLISECOND,0);

        Date dEnd = end.getTime();
        intent.putExtra("endTime",dEnd);
        intent.putExtra("title", name + " - " + dosage);
        startActivity(intent);
    }
}
