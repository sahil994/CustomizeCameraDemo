package com.example.sahilgoyal.customizecamerademo;

import android.content.Context;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {

    Camera mCamera;
    Button mCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCapture= (Button) findViewById(R.id.button_capture);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        if (checkCameraHardware(MainActivity.this)) {


            mCamera = getCameraInstance();

            // Create our Preview view and set it as the content of our activity.
            CustomSurfaceView mCustomSurfaceView = new CustomSurfaceView(this, mCamera);
            preview.addView(mCustomSurfaceView);


        }
      final Camera.PictureCallback mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(data);
                if (pictureFile == null){
                    Log.d("MainActivty", "Error creating media file, check storage permissions: ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d("MainActivty", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("MainActivty", "Error accessing file: " + e.getMessage());
                }
            }

        };

        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture(null,null,mPicture);
            }
        });


    }

    private File getOutputMediaFile(byte[] data) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length);
        File file = null;
        if(bitmap!=null){

            file=new File(Environment.getExternalStorageDirectory()+"/dirr");
            if(!file.isDirectory()){
                file.mkdir();
            }

            file=new File(Environment.getExternalStorageDirectory()+"/dirr",System.currentTimeMillis()+".jpg");


            try
            {
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);

                fileOutputStream.flush();
                fileOutputStream.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }

        }
        return file;
    }
    


    @Override
    protected void onStop() {
        super.onStop();
        mCamera.release();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)

     Log.e("camera",""+e);
        }
        return c; // returns null if camera is unavailable
    }

}
