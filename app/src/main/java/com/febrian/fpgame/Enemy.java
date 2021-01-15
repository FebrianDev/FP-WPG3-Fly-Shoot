package com.febrian.fpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Enemy {
    Bitmap [] enemy = new Bitmap[3];
    int screenX,screenY;
    float enemyMove;
    float posY;
    boolean isVisible;

    int width,height;
    BulletEnemy bulletEnemy;
    int i = 0;
    float timer = 0, timerShoot = 0;
    Enemy(int x, int y, Resources resources){
        screenX = x;
        screenY = y;
        width = 160;
        height = 160;

        enemy[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.enemy1), width, height, false);
        enemy[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.enemy2), width, height, false);
        enemy[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.enemy3), width, height, false);
        isVisible = true;
        bulletEnemy = new BulletEnemy(x,y,resources);
        bulletEnemy.setVisible(false);
    }

    public void update(float fps)
    {
        timer += fps;
        if(timer > 400){
            i++;
            timer = 0;
        }

        if(i == 3){
            i = 0;
        }

        enemyMove -= 0.15 * fps;
    }

    public void drawBitmap(Canvas canvas){
        canvas.drawBitmap(enemy[i], getEnemyMove(),getPosY(), null);
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
