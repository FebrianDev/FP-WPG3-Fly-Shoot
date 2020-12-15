
package com.febrian.fpgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
* id :
* jump = 1
* shoot = 2
* pause = 3
* resume = 4
* restart = 5
* exit = 6
*/

public class MainActivity extends Activity {
    private GameView gameView;
    private Button btnExit, btnJump,btnShoot,btnRestart, btnPause,btnResume;
    private EditText name;
    private Button btnName;
    private TextView gameOver;

    public static boolean paused = false;
    public static boolean isGameOver = false;
    public static volatile boolean playing;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        RelativeLayout gameButtons = new RelativeLayout(this);
        FrameLayout game = new FrameLayout(this);

        btnJump = new Button(this);
        btnShoot = new Button(this);
        btnPause = new Button(this);
        btnRestart = new Button(this);
        btnResume = new Button(this);
        btnExit = new Button(this);

        name = new EditText(this);
        btnName = new Button(this);

        RelativeLayout.LayoutParams bJump = new RelativeLayout.LayoutParams(240, 240);
        RelativeLayout.LayoutParams bShoot = new RelativeLayout.LayoutParams(240,240);
        RelativeLayout.LayoutParams bPause = new RelativeLayout.LayoutParams(150, 150);
        RelativeLayout.LayoutParams bResume = new RelativeLayout.LayoutParams(250, 150);
        RelativeLayout.LayoutParams bRestart = new RelativeLayout.LayoutParams(250,150);
        RelativeLayout.LayoutParams bExit = new RelativeLayout.LayoutParams(250, 150);

        RelativeLayout.LayoutParams EdName = new RelativeLayout.LayoutParams(300, 150);
        RelativeLayout.LayoutParams bName = new RelativeLayout.LayoutParams(250, 150);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        gameButtons.setLayoutParams(params);
        gameButtons.addView(btnJump);
        gameButtons.addView(btnShoot);
        gameButtons.addView(btnPause);
        gameButtons.addView(btnRestart);
        gameButtons.addView(btnResume);
        gameButtons.addView(btnExit);
        gameButtons.addView(name);
        gameButtons.addView(btnName);

        btnJump.setBackground(getDrawable(R.drawable.jump_on));
        int ID_JUMP = 1;
        btnJump.setId(ID_JUMP);
        bJump.bottomMargin = 24;
        bJump.leftMargin = 128;
        bJump.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        bJump.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        btnShoot.setBackground(getDrawable(R.drawable.shoot_on));
        int ID_SHOOT = 2;
        btnShoot.setId(ID_SHOOT);
        bShoot.bottomMargin = 24;
        bShoot.rightMargin = 128;
        bShoot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bShoot.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        btnPause.setBackground(getDrawable(R.drawable.pause));
        int ID_PAUSE = 3;
        btnPause.setId(ID_PAUSE);
        bPause.topMargin = 24;
        bPause.rightMargin = 24;
        bPause.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bPause.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        btnResume.setBackground(getDrawable(R.drawable.resume));
        int ID_RESUME = 4;
        btnResume.setId(ID_RESUME);
        bResume.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnRestart.setBackground(getDrawable(R.drawable.restart));
        int ID_RESTART = 5;
        btnRestart.setId(ID_RESTART);
        bRestart.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnExit.setBackground(getDrawable(R.drawable.exit2));
        int ID_EXIT = 6;
        btnExit.setId(ID_EXIT);
        bExit.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        name.setId(7);
        name.setHint("Name");
        EdName.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnName.setId(8);
        btnName.setText("Submit!");
        bName.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        gameView.setGameOverOff();
        gameView.setResume();

        btnJump.setLayoutParams(bJump);
        btnShoot.setLayoutParams(bShoot);
        btnPause.setLayoutParams(bPause);
        btnResume.setLayoutParams(bResume);
        btnRestart.setLayoutParams(bRestart);
        btnExit.setLayoutParams(bExit);
        name.setLayoutParams(EdName);
        btnName.setLayoutParams(bName);

        game.addView(gameView);
        game.addView(gameButtons);
        setContentView(game);
    }

    class GameView extends SurfaceView implements Runnable, View.OnClickListener {
        private int width;
        private int height;
        private Thread gameThread = null;
        private final SurfaceHolder ourHolder;

        private final Paint paint;
        private long fps;
        private Parallax parallax;
        private Player player;
        private Bullet bullet, bullet2;

        private Enemy enemy;
        //ArrayList<Enemy> enemies = new ArrayList<>();
        private boolean fire = false, fire2 = false;
        private float score = 0;
        private float randPosY;

        //ArrayList<Bullet> bullets = new ArrayList<>();
        Bullet mybul;
        float timerShoot = 0;
        int i = 0;
        public GameView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            playing = true;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
            int xmax = getResources().getDisplayMetrics().widthPixels;

            createAndRestart();

            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused)
                    update();
                draw();
                long timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void update() {
            float min = 200;
            float max = 650;
            randPosY = (float) Math.random() * max + min;
            parallax.update(fps);
            player.update(fps);
            bullet.update(fps);
            bullet2.update(fps);
            enemy.update(fps);
            //enemies.forEach(n -> n.update(fps));

//            for (int j = 0; j < bullets.size(); j++){
//                bullets.get(j).update(fps);
//            }

            timerShoot += fps;
            btnJump.setOnClickListener(this);
            btnShoot.setOnClickListener(this);
            btnPause.setOnClickListener(this);
            btnResume.setOnClickListener(this);
            btnRestart.setOnClickListener(this);
            btnExit.setOnClickListener(this);
            btnName.setOnClickListener(this);
        }

        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                Canvas canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                paint.setTextSize(45);

                //BG
                parallax.drawBitmap(canvas);
                //Player
                player.drawBitmap(canvas);

                if (fire && bullet.getVisible())
                    bullet.drawBitmap(canvas);
                if (fire2 && bullet2.getVisible())
                    bullet2.drawBitmap(canvas);

                if (bullet.getBulletMove() >= width || !bullet.getVisible()) {
                    fire = false;
                    bullet.setBulletMove(120);
                }

                if (bullet2.getBulletMove() >= width || !bullet2.getVisible()) {
                    fire2 = false;
                    bullet2.setBulletMove(120);
                }
                if (RectF.intersects(enemy.getCollision(), bullet.getCollision()) && bullet.getVisible()) {
                    bullet.setVisible(false);
                    enemy.setVisible(false);
                    score++;
                }

                if (RectF.intersects(enemy.getCollision(), bullet2.getCollision()) && bullet2.getVisible()) {
                    bullet2.setVisible(false);
                    enemy.setVisible(false);
                    score++;
                }

                if ((!enemy.getVisible()) || enemy.getEnemyMove() <= -200) {
                    enemy = new Enemy(width, height, getResources());
                    enemy.setPosYBullet(randPosY);
                    enemy.setEnemyMove(width + 200);
                    enemy.setVisible(true);
                }

                if (enemy.getVisible())
                    enemy.drawBitmap(canvas);

                if (RectF.intersects(enemy.getCollision(), player.getCollision())) {
                    paused = true;
                    isGameOver = true;
                    setGameOverOn();
                }

//                if(bullets.size() > 0) {
//                    for (int j = 0; j < bullets.size(); j++) {
//                        bullets.get(j).drawBitmap(canvas);
//
//                        if (bullets.get(j).getBulletMove() >= width) {
//                            bullets.remove(j);
//                        }
//                    }
//                    for (int j = 0; j < bullets.size(); j++) {
//                        if (RectF.intersects(enemy.getCollision(), bullets.get(j).getCollision()) && bullets.get(j).getVisible()) {
//                            //     bullets.get(j).setVisible(false);
//                            enemy.setVisible(false);
//                            bullets.remove(j);
//                            score++;
//                        }
//                    }
//                }

                canvas.drawText("Score : " + score, 440, 120, paint);
                canvas.drawText("FPS:" + fps, 120, 120, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void createAndRestart() {
            parallax = new Parallax(width, height, getResources());
            player = new Player(width, height, getResources());
            bullet = new Bullet(width, height, getResources());
            bullet2 = new Bullet(width, height, getResources());
            enemy = new Enemy(width, height, getResources());
            enemy.setPosYBullet(player.getPlayerPosY());
            enemy.setVisible(true);
            enemy.setEnemyMove(width - 200);

            btnName.setEnabled(true);

            //timerShoot = 0;

//            bullets.clear();
//
//            for(int i = 0; i <enemies.size(); i++){
//                enemies.add(enemy);
//            }

            score = 0;
            isGameOver = false;
            paused = false;
            setGameOverOff();
            setResume();
        }

        public void setGameOverOn() {
            btnExit.setTranslationY(100);
            btnExit.setTranslationX(-200);
            btnExit.setAlpha(1);
            btnExit.setVisibility(VISIBLE);

            btnRestart.setTranslationY(100);
            btnRestart.setTranslationX(200);
            btnRestart.setAlpha(1);
            btnRestart.setVisibility(VISIBLE);

            name.setTranslationY(-100);
            name.setTranslationX(-200);
            name.setAlpha(1);
            name.setVisibility(VISIBLE);

            btnName.setTranslationY(-100);
            btnName.setTranslationX(200);
            btnName.setAlpha(1);
            btnName.setVisibility(VISIBLE);
        }

        public void setGameOverOff(){
            btnExit.setVisibility(VISIBLE);
            btnExit.setTranslationY(height);
            btnExit.setAlpha(0);

            btnRestart.setVisibility(VISIBLE);
            btnRestart.setTranslationY(height);
            btnRestart.setAlpha(0);

            name.setTranslationY(height);
          //  name.setTranslationX(-200);
            name.setAlpha(0);
            name.setVisibility(VISIBLE);

            btnName.setTranslationY(height);
            //btnName.setTranslationX(200);
            btnName.setAlpha(0);
            btnName.setVisibility(VISIBLE);
        }

        public void setPauseOn() {
            btnExit.setTranslationY(100);
            btnExit.setTranslationX(0);
            btnExit.setAlpha(1);
            btnExit.setVisibility(VISIBLE);

            btnResume.setTranslationY(-100);
            btnResume.setAlpha(1);
            btnResume.setVisibility(VISIBLE);
        }

        public void setResume() {
            btnExit.setTranslationY(height);
            btnExit.setAlpha(0);
            btnExit.setVisibility(VISIBLE);

            btnResume.setTranslationY(height);
            btnResume.setAlpha(0);
            btnResume.setVisibility(VISIBLE);
        }


        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case 1:
                    BtnJump();
                    break;
                case 2:
                    BtnShoot();
                    break;
                case 3:
                    BtnPause();
                    break;
                case 4:
                    BtnResume();
                    break;
                case 5:
                    BtnRestart();
                    break;
                case 6:
                    BtnExit();
                    break;
                case 8:
                    BtnName();
                    break;
            }
        }

        private void BtnPause(){
            paused = true;
            setPauseOn();
        }

        private void BtnResume(){
            paused = false;
            setResume();
        }

        private void BtnRestart(){
            createAndRestart();
        }

        private void BtnExit(){
            startActivity(new Intent(MainActivity.this, MainMenu.class));
            finish();
        }

        private void BtnJump(){
            player.jump(fps);
            Player.i = 0;
            Player.timer = 0;
        }

        private void BtnShoot(){
            if (!fire) {
                bullet.setPosYBullet(player.getPlayerPosY());
                bullet.setBulletMove(120);
                bullet.setVisible(true);
                fire = true;

            } else if (fire && !fire2) {
                fire2 = true;
                bullet2.setPosYBullet(player.getPlayerPosY());
                bullet2.setBulletMove(120);
                bullet2.setVisible(true);
            }

//            if(timerShoot > 1000) {
//                btnShoot.setEnabled(true);
//                mybul.setPosYBullet(player.getPlayerPosY());
//                mybul.setBulletMove(120);
//                mybul.setVisible(true);
//                bullets.add(mybul);
//                timerShoot = 0;
//            }else{
//                btnShoot.setEnabled(false);
//            }
//            i++;
        }

        @SuppressLint("ResourceType")
        private void BtnName(){
      //      name = findViewById(7);
            String getName = name.getText().toString();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(getName);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child("name").setValue(getName);
                    dataSnapshot.getRef().child("score").setValue(score);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println(databaseError);
                }
            });

            btnName.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}