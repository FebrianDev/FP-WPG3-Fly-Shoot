package com.febrian.fpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Parallax {
    Bitmap[] bg = new Bitmap[2];
    float posX = 0,posX2 = 2100;
    Parallax(int x, int y, Resources resources){
        bg[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bg), x, y, false);
        bg[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bg), x, y, false);
    }

    public void drawBitmap(Canvas canvas){
        canvas.drawBitmap(bg[0], posX,0, null);
        canvas.drawBitmap(bg[1], posX2,0, null);
    }

    public void update(float fps){
        posX -= fps * 0.08;
        posX2 -= fps * 0.08;
        if (posX <= -2100)
            posX = 2100;
        if(posX2 <= -2100)
            posX2 = 2100;
    }
}
