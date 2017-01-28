package com.kevincarrierdeveloper.rxapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends Activity implements SurfaceHolder.Callback{
//, View.OnClickListener
    private Socket mSocket;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private int cameraId;
    private Camera camera;
    //private Button captureImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //captureImage = (Button) findViewById(R.id.captureImage);
        //captureImage.setOnClickListener(this);
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private Socket getSocket() {
        try {
            mSocket = IO.socket("http://138.197.0.96:80");
            Log.d("connectAnything", "Success");
        } catch (URISyntaxException e) {
            Log.d("connectAnything", "Failed");
        }
        return mSocket;
    }

    public void accessCamera(View v) {

        Intent currentIntent = new Intent(getIntent());
        startActivityForResult(currentIntent, REQUEST_IMAGE_CAPTURE);

        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
    }
    /*public void accessPhotos(View v){

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , REQUEST_IMAGE_CAPTURE);
    }*/

    /*@Override
    public void onClick(View v){

        Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        camera.takePicture(null, null,  new Camera.PictureCallback() {
            //private File imageFile;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {

                    Bitmap loadedImage = null;
                    Bitmap rotatedBitmap = null;
                    loadedImage = BitmapFactory.decodeByteArray(data, 0,
                            data.length);

                    // rotate Image
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);




                    //Bitmap bmp = (Bitmap) data.getExtras().get("data");

                    // convert byte array into bitmap
                    //Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                    //        data.length);

                    // rotate Image
                    //Matrix rotateMatrix = new Matrix();
                    //rotateMatrix.postRotate(rotation);
                    //rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                    //        loadedImage.getWidth(), loadedImage.getHeight(),
                    //        rotateMatrix, false);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    byte[] byteArray = stream.toByteArray();
                    Log.d("connectAnything", "Before Connect");
                    sendToSocket(byteArray);
                    Log.d("connectAnything", "After Connect");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    public void sendToSocket(final byte[] imageByte) {

        getSocket();

        Log.d("connectAnything", "SEND TO SOCKET");

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        }).once("url", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("url", args[0].toString());
                        Toast.makeText(MainActivity.this, args[0].toString(), Toast.LENGTH_SHORT).show();
                        ding(args[0].toString());
                        mSocket.emit("delete", args[0].toString());
                    }

                });


            }
        });
        mSocket.connect();
        String language = "es";
        //mSocket.emit("image_sent", "{ \"image\":" + imageByte + ", \"language\":" + language + "}");
        Log.d("connectAnything", "SENDING");
        mSocket.emit("image_sent", imageByte);
        Log.d("connectAnything", "SENT");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                //Language
                //String language = "iw";

                //sendToSocket(byteArray, language);
                Log.d("connectAnything", "Before Connect");
                sendToSocket(byteArray);
                Log.d("connectAnything", "After Connect");

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
                Log.d("connectAnything", "Failed");
            }
        }
    }

    private void ding(final String url) {
        try {
            MediaPlayer player = new MediaPlayer();


            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
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



    /*STUFF FOR CAMERA IMAGE*/

    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            alertCameraDialog();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                camera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(MainActivity.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        //dialog.setIcon(R.drawab);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }









}







    /*private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }
//
//                    // add the message to view
//                    addMessage(username, message);
                    ding(args[0].toString());


                }
            });
        }

    };*/


