package com.febrian.fpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Player {
    public static float timer = 0;
    public static int i = 1;
    private float playerPosY;
    Bitmap [] player = new Bitmap[3];
    int screenX, screenY;
    Player(int x, int y, Resources resources){
        screenX = x;
        screenY = y;
        playerPosY = screenY/2;
        // Simpan sprite pada array object bitmap
        player[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.karaktter01), 128, 128, false);
        player[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.karaktter02), 128, 128, false);
        player[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.karaktter03), 128, 128, false);
    }


    public void update(float fps){
        /*Animation Char Start*/
        if(i!= 2)
            timer += fps;

        if(timer > 3000 && i < 2){
            i++;
            timer = 0;
        }

        if(i == 2){
            i = 2;
        }
        /*Animation Char end*/

        playerPosY += 2;

        if(playerPosY <= 0){
            playerPosY = 0;
        }

        if(playerPosY >= screenY-220){
            playerPosY = screenY-220;
        }

        setPlayerPosY(playerPosY);
    }

    public void jump(float fps){
        playerPosY -= fps * 1f;
    }

    public void drawBitmap(Canvas canvas){
        canvas.drawBitmap(player[i], 120,playerPosY,null);
    }

    public void setPlayerPosY(float playerPosY) {
        this.playerPosY = playerPosY;
    }

    public float getPlayerPosY() {
        return playerPosY;
    }

    public RectF getCollision(){
        return new RectF(120, getPlayerPosY(), 120 + 120, getPlayerPosY() + 120);
    }
}
