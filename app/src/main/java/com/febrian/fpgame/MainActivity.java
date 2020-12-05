package com.febrian.fpgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    GameView gameView;
    RelativeLayout GameButtons;
    FrameLayout game;
    Button btnExit;
    Button btnJump,btnShoot,btnRestart, btnPause,btnResume;;
    Context c;

    public static boolean paused = false;
    public static boolean isGameOver = false;
    public static volatile boolean playing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        c = this;
        gameView = new GameView(this);
        GameButtons = new RelativeLayout(this);
        game = new FrameLayout(this);
        btnJump = new Button(this);
        btnShoot = new Button(this);
        btnPause = new Button(this);
        btnRestart = new Button(this);
        btnResume = new Button(this);
        btnExit = new Button(this);

        RelativeLayout.LayoutParams bJump = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bShoot = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bPause = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bResume = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bRestart = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams bExit = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        GameButtons.setLayoutParams(params);
        GameButtons.addView(btnJump);
        GameButtons.addView(btnShoot);
        GameButtons.addView(btnPause);
        GameButtons.addView(btnRestart);
        GameButtons.addView(btnResume);
        GameButtons.addView(btnExit);

        btnJump.setText("Jump");
        bJump.bottomMargin = 24;
        bJump.leftMargin = 128;
        bJump.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        bJump.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        btnShoot.setText("Shoot");
        bShoot.bottomMargin = 24;
        bShoot.rightMargin = 128;
        bShoot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bShoot.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        btnPause.setText("Pause");
        bPause.topMargin = 24;
        bPause.rightMargin = 24;
        bPause.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bPause.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        btnResume.setText("Resume");
        bResume.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnRestart.setText("Restart");
        bRestart.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnExit.setText(" Exit ");
        bExit.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnJump.setLayoutParams(bJump);
        btnShoot.setLayoutParams(bShoot);
        btnPause.setLayoutParams(bPause);
        btnResume.setLayoutParams(bResume);
        btnRestart.setLayoutParams(bRestart);
        btnExit.setLayoutParams(bExit);

        game.addView(gameView);
        game.addView(GameButtons);
        setContentView(game);
    }

    class GameView extends SurfaceView implements Runnable {
        int width, height, Xmax;
        Thread gameThread = null;
        SurfaceHolder ourHolder;

        Canvas canvas;
        Paint paint;
        long fps;
        private long timeThisFrame;
        Parallax parallax;
        Player player;
        Bullet bullet, bullet2;

        Enemy enemy;
        boolean fire = false, fire2 = false;
        float score = 0;
        float randPosY, min = 200, max = 1080 - 200;

        public GameView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            playing = true;
        }

        @Override
        public void run() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
            Xmax = getResources().getDisplayMetrics().widthPixels;
            createAndRestart();
            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused)
                    update();
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            randPosY = (float) Math.random() * max + min;
            parallax.update(fps);
            player.update(fps);
            bullet.update(fps);
            bullet2.update(fps);
            enemy.update(fps);

            //Button Jump
            btnJump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.jump(fps);
                    player.i = 0;
                    player.timer = 0;
                }
            });

            //Button shoot
            btnShoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            btnPause.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    paused = true;
                    setPause();
                }
            });
            btnResume.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    paused = false;
                    setResume();
                }
            });
            btnRestart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAndRestart();
                }
            });
            btnExit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, MainMenu.class));
                    finish();
                }
            });
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
            score = 0;
            isGameOver = false;
            paused = false;

            btnExit.setVisibility(VISIBLE);
            btnExit.setTranslationY(height);
            btnExit.setAlpha(0);

            btnRestart.setVisibility(VISIBLE);
            btnRestart.setTranslationY(height);
            btnRestart.setAlpha(0);

            setResume();
        }

        public void setGameOver() {
            btnExit.setTranslationY(100);
            btnExit.setAlpha(1);
            btnExit.setVisibility(VISIBLE);

            btnRestart.setTranslationY(-100);
            btnRestart.setAlpha(1);
            btnRestart.setVisibility(VISIBLE);
        }

        public void setPause() {
            btnExit.setTranslationY(100);
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

        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
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
                    setGameOver();
                }

                canvas.drawText("Score : " + score, 440, 120, paint);
                canvas.drawText("FPS:" + fps, 120, 120, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
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