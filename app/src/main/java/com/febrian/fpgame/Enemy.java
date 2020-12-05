package com.febrian.fpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Enemy {
    Bitmap enemy;
    int screenX,screenY;
    float enemyMove;
    float posY;
    boolean isVisible;

    int width,height;

    Enemy(int x, int y, Resources resources){
        screenX = x;
        screenY = y;
        width = 200;
        height = 200;
        enemy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.batas), width,height,false);
        isVisible = true;
    }

    public void update(float fps)
    {
        enemyMove -= 0.3 * fps;
    }

    public void drawBitmap(Canvas canvas){
        canvas.drawBitmap(enemy, getEnemyMove(),getPosY(), null);
    }

    public void setPosYBullet(float posY){
        this.posY = posY;
    }

    public float getPosY() {
        return posY;
    }

    public float getEnemyMove() {
        return enemyMove;
    }

    public void setEnemyMove(float enemyMove) {
        this.enemyMove = enemyMove;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean getVisible(){
        return isVisible;
    }

    public RectF getCollision() {
        return new RectF(getEnemyMove(),getPosY(), getEnemyMove()+width,getPosY()+height);
    }
}
