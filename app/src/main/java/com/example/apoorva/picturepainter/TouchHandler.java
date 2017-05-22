package com.example.apoorva.picturepainter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class TouchHandler implements View.OnTouchListener {
    DrawActivity drawActivity;

    GestureDetectorCompat gestureDetectorCompat;
    private int finishedPathsCounter;

    public TouchHandler(DrawActivity drawActivity) {
        this.drawActivity = drawActivity;
        gestureDetectorCompat = new GestureDetectorCompat(this.drawActivity, new MyGestureListener());
        finishedPathsCounter = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int maskedAction = event.getActionMasked();
        gestureDetectorCompat.onTouchEvent(event);
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = 0, size = event.getPointerCount(); i < size; i++) {
                    int id = event.getPointerId(i);
                    drawActivity.addNewPath(id, event.getX(i), event.getY(i));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0, size = event.getPointerCount(); i < size; i++) {
                    int id = event.getPointerId(i);
                    drawActivity.updatePath(id, event.getX(i), event.getY(i));
                }
                break;
            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:

            case MotionEvent.ACTION_CANCEL:
                for (int i = 0, size = event.getPointerCount(); i < size; i++) {
                    int id = event.getPointerId(i);
                    drawActivity.finishPath(id);
                    finishedPathsCounter = finishedPathsCounter + 1;
                }
                break;
        }
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            drawActivity.onLongPress(e.getX(), e.getY());
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            drawActivity.onDoubleTap(e.getX(), e.getY());
            return super.onDoubleTap(e);
        }
    }
}
