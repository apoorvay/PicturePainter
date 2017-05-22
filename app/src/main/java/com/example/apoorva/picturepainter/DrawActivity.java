package com.example.apoorva.picturepainter;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class DrawActivity extends AppCompatActivity implements View.OnClickListener {

    MyCanvas myCanvas;
    TouchHandler touchHandler;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button red, blue, green, undo, clear, done;
    private Bitmap lp_icon, dt_icon;
    private float lp_offsetX, lp_offsetY, dt_offsetX, dt_offsetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        myCanvas = (MyCanvas) findViewById(R.id.myCanvas);
        touchHandler = new TouchHandler(DrawActivity.this);
        myCanvas.setOnTouchListener(touchHandler);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startCameraActivity();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]
                    { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }


        red = (Button)findViewById(R.id.red);
        blue = (Button)findViewById(R.id.blue);
        green = (Button)findViewById(R.id.green);
        undo = (Button)findViewById(R.id.undo);
        clear = (Button)findViewById(R.id.clear);
        done = (Button)findViewById(R.id.done);

        red.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);
        undo.setOnClickListener(this);
        clear.setOnClickListener(this);
        done.setOnClickListener(this);

        red.setBackgroundColor(Color.RED);
        blue.setBackgroundColor(Color.BLUE);
        green.setBackgroundColor(Color.GREEN);

        lp_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.long_press_icon);
        dt_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.double_tap_icon);
        lp_offsetX = lp_icon.getWidth()/2;
        lp_offsetY = lp_icon.getHeight()/2;
        dt_offsetX = dt_icon.getWidth()/2;
        dt_offsetY = dt_icon.getHeight()/2;

    }

    public void addNewPath(int id, float x, float y) {
        myCanvas.addPath(id, x, y); }

    public void updatePath(int id, float x, float y) {
        myCanvas.updatePath(id, x, y); }

    public void removePath(int id) {
        myCanvas.removePath(id); }

    public void finishPath(int id){
        myCanvas.finishPath(id);
    }

    public void onLongPress(float x, float y){
        myCanvas.addIcon(lp_icon, x-lp_offsetX, y-lp_offsetY);
    }

    public void onDoubleTap(float x, float y){
        myCanvas.addIcon(dt_icon, x-dt_offsetX, y-dt_offsetY);
    }

    public void startCameraActivity(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
            myCanvas.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
        /*Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    */}

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.red) myCanvas.changeColor(MyCanvas.colorChoice.RED);
        else if(v.getId() == R.id.green) myCanvas.changeColor(MyCanvas.colorChoice.GREEN);
        else if(v.getId() == R.id.blue) myCanvas.changeColor(MyCanvas.colorChoice.BLUE);
        else if(v.getId() == R.id.clear) myCanvas.clearCanvas();
        else if(v.getId() == R.id.undo) {
            myCanvas.undoMove();
        }
        else if(v.getId() == R.id.done){
            this.finish();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startCameraActivity();
            }
        }
    }

    public void debugToast(String s){
        Toast toast = Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT);
        toast.show();
    }
}
