package com.example.apoorva.picturepainter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyCanvas extends View {
    HashMap<Integer, Path> activePaths;
    Paint pathPaint, redPathPaint, bluePathPaint, greenPathPaint;

    class ColoredPath{
        private Path path;
        private colorChoice color;

        public ColoredPath(Path path, colorChoice color){
            this.path = path;
            this.color = color;
        }

        public colorChoice getColor() {return color;}
        public Path getPath() {return path;}
    }
    class DrawIcon{
        private float x;
        private float y;
        Bitmap bm;

        public DrawIcon(Bitmap bm, float x, float y) {
            this.bm = bm;
            this.x = x;
            this.y = y;
        }

        public Bitmap getBm() {return bm;}
        public float getX() {return x;}
        public float getY() {return y;}
    }

    ArrayList<ColoredPath> finishedPaths;
    ArrayList<DrawIcon> drawIcons;
    ArrayList<IconPath> orderIconPath;
    DrawActivity drawActivity;

    public enum IconPath {ICON, PATH};
    public enum colorChoice {RED, BLUE, GREEN};

    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        activePaths = new HashMap<>();
        finishedPaths = new ArrayList<ColoredPath>();
        drawIcons = new ArrayList<DrawIcon>();
        orderIconPath = new ArrayList<IconPath>();

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(15);
        changeColor(colorChoice.RED);
        initializeAllPaths();
        drawActivity = new DrawActivity();
    }

    public void initializeAllPaths(){
        redPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPathPaint.setStyle(Paint.Style.STROKE);
        redPathPaint.setStrokeWidth(15);
        redPathPaint.setColor(Color.RED);
        bluePathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePathPaint.setStyle(Paint.Style.STROKE);
        bluePathPaint.setStrokeWidth(15);
        bluePathPaint.setColor(Color.BLUE);
        greenPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPathPaint.setStyle(Paint.Style.STROKE);
        greenPathPaint.setStrokeWidth(15);
        greenPathPaint.setColor(Color.GREEN);
    }

    public void undoMove(){
        if(!orderIconPath.isEmpty()){
            IconPath removeIconPath = orderIconPath.remove(orderIconPath.size()-1);
            if(removeIconPath.equals(IconPath.ICON)){
                drawIcons.remove(drawIcons.size()-1);
            }
            else if(removeIconPath.equals(IconPath.PATH)){
                finishedPaths.remove(finishedPaths.size()-1);
            }
            invalidate();
        }
    }

    public void changeColor(colorChoice color){
        if(color == colorChoice.RED) pathPaint.setColor(Color.RED);
        else if (color == colorChoice.BLUE) pathPaint.setColor(Color.BLUE);
        else if (color == colorChoice.GREEN) pathPaint.setColor(Color.GREEN);
    }

    public void addPath(int id, float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        activePaths.put(id, path);
        invalidate();
    }

    public void updatePath(int id, float x, float y) {
        Path path = activePaths.get(id);
        if (path != null) {
            path.lineTo(x, y);
        }
        invalidate();
    }

    public void removePath(int id) {
        if(activePaths.containsKey(id)){
            activePaths.remove(id);
        }
        invalidate();
    }

    public void addIcon(Bitmap bm, float x, float y){
        DrawIcon drawIcon= new DrawIcon(bm, x, y);
        boolean sameIcon = false;
        if(!drawIcons.isEmpty()) {
            DrawIcon prevIcon = drawIcons.get(drawIcons.size()-1);
            sameIcon = (
                    (drawIcon.getX() == prevIcon.getX()) &&
                    (drawIcon.getY() == prevIcon.getY()));
        }

        if( drawIcons.isEmpty() ||
                (!drawIcons.isEmpty() && !sameIcon) ){
            drawIcons.add(drawIcon);
            orderIconPath.add(IconPath.ICON);
            invalidate();
        }
    }

    public void finishPath(int id){
        if(activePaths.containsKey(id)){
            Path path = activePaths.get(id);
            colorChoice thisColor;

            if(pathPaint.getColor() == Color.RED) thisColor = colorChoice.RED;
            else if(pathPaint.getColor() == Color.GREEN) thisColor = colorChoice.GREEN;
            else thisColor = colorChoice.BLUE;

            ColoredPath coloredPath = new ColoredPath(path, thisColor);
            finishedPaths.add(coloredPath);
            removePath(id);
            orderIconPath.add(IconPath.PATH);
            invalidate();
        }

    }

    public void clearCanvas(){
        finishedPaths.clear();
        drawIcons.clear();
        orderIconPath.clear();
        activePaths.clear();
        invalidate();
    }

    public int getIconLength(){
        return orderIconPath.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int currentFinishedPath = 0;
        int currentIcon = 0;

        for(IconPath iconPath : orderIconPath){
            if(iconPath.equals(IconPath.ICON)){
                DrawIcon drawIcon = drawIcons.get(currentIcon);
                Bitmap bm = drawIcon.getBm();
                float x = drawIcon.getX();
                float y = drawIcon.getY();
                canvas.drawBitmap(bm, x, y, null);
                currentIcon = currentIcon + 1;
            }
            else {
                ColoredPath coloredPath = finishedPaths.get(currentFinishedPath);
                Path path = coloredPath.getPath();
                colorChoice color = coloredPath.getColor();
                if(color == colorChoice.RED) canvas.drawPath(path, redPathPaint);
                else if(color == colorChoice.BLUE) canvas.drawPath(path, bluePathPaint);
                else canvas.drawPath(path, greenPathPaint);
                currentFinishedPath++;
            }
        }
        /*for(ColoredPath coloredPath : finishedPaths){
            Path path = coloredPath.getPath();
            colorChoice color = coloredPath.getColor();
            if(color == colorChoice.RED) canvas.drawPath(path, redPathPaint);
            else if(color == colorChoice.BLUE) canvas.drawPath(path, bluePathPaint);
            else canvas.drawPath(path, greenPathPaint);
        }

        for(DrawIcon drawIcon : drawIcons){
            Bitmap bm = drawIcon.getBm();
            float x = drawIcon.getX();
            float y = drawIcon.getY();
            canvas.drawBitmap(bm, x, y, null);
        }*/
        for (Path path : activePaths.values()) {
            canvas.drawPath(path, pathPaint);
        }
    }
}
