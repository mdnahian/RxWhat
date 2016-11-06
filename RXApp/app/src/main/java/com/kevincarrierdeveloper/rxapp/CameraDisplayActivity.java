package com.kevincarrierdeveloper.rxapp;

import android.app.Activity;
import android.os.Bundle;


public class CameraDisplayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraTestingActivity.newInstance())
                    .commit();
        }

    }

}
