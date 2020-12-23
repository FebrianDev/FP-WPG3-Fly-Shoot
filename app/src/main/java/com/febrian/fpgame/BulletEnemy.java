package com.febrian.fpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class BulletEnemy {
    Bitmap bullet;
    int screenX,screenY;
    float bulletMove;
    float posY;
    boolean isVisible;
    RectF bulletPos;
    int width,height;
    BulletEnemy(int x, int y, Resources resources){
        screenX = x;
        screenY = y;
        width = 20;
        height = 20;
        bulletPos = new RectF(0,20,20,0);
        bullet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.batas), width,height,false);
        isVisible = false;
    }

    public void update(float fps){
        bulletMove -= 1 * fps;
    }

    public void drawBitmap(Canvas canvas){
        canvas.drawBitmap(bullet, getBulletMove() -120,getPosY()+60, null);
    }

    public void setPosYBullet(float posY){
        this.posY = posY;
    }

    public float getPosY() {
        return posY;
    }

    public void setBulletMove(float bulletMove) {
        this.bulletMove = bulletMove;
    }

    public float getBulletMove() {
        return bulletMove;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean getVisible(){
        return isVisible;
    }

    public RectF getCollision() {
        return new RectF(getBulletMove(),getPosY(), getBulletMove()+width,getPosY()+height);
    }
}
