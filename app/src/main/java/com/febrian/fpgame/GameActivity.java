package com.febrian.fpgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import static com.febrian.fpgame.Helper.KEYLOGIN;
import static com.febrian.fpgame.Helper.keylogin;

public class GameActivity extends Activity {
    private GameView gameView;
    private Button btnExit, btnJump, btnShoot, btnRestart, btnPause, btnResume;

    public static boolean paused = false;
    public static boolean isGameOver = false;
    public static volatile boolean playing;

    DatabaseReference reference;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        gameView = new GameActivity.GameView(this);
        RelativeLayout gameButtons = new RelativeLayout(this);
        FrameLayout game = new FrameLayout(this);

        btnJump = new Button(this);
        btnShoot = new Button(this);
        btnPause = new Button(this);
        btnRestart = new Button(this);
        btnResume = new Button(this);
        btnExit = new Button(this);

        RelativeLayout.LayoutParams bJump = new RelativeLayout.LayoutParams(248, 248);
        RelativeLayout.LayoutParams bShoot = new RelativeLayout.LayoutParams(248, 248);
        RelativeLayout.LayoutParams bPause = new RelativeLayout.LayoutParams(150, 150);
        RelativeLayout.LayoutParams bResume = new RelativeLayout.LayoutParams(250, 150);
        RelativeLayout.LayoutParams bRestart = new RelativeLayout.LayoutParams(250, 150);
        RelativeLayout.LayoutParams bExit = new RelativeLayout.LayoutParams(250, 150);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        gameButtons.setLayoutParams(params);
        gameButtons.addView(btnJump);
        gameButtons.addView(btnShoot);
        gameButtons.addView(btnPause);
        gameButtons.addView(btnRestart);
        gameButtons.addView(btnResume);
        gameButtons.addView(btnExit);

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

        btnPause.setBackground(getDrawable(R.drawable.pause_res_com));
        btnPause.setAlpha(0);
        int ID_PAUSE = 3;
        btnPause.setId(ID_PAUSE);
        bPause.topMargin = 24;
        bPause.rightMargin = 24;
        bPause.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        bPause.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        btnResume.setBackground(getDrawable(R.drawable.resume_res_com));
        int ID_RESUME = 4;
        btnResume.setId(ID_RESUME);
        bResume.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnRestart.setBackground(getDrawable(R.drawable.restart_res_com));
        int ID_RESTART = 5;
        btnRestart.setId(ID_RESTART);
        bRestart.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        btnExit.setBackground(getDrawable(R.drawable.exit_res_com));
        int ID_EXIT = 6;
        btnExit.setId(ID_EXIT);
        bExit.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        gameView.setGameOverOff();
        gameView.setResume();

        btnJump.setLayoutParams(bJump);
        btnShoot.setLayoutParams(bShoot);
        btnPause.setLayoutParams(bPause);
        btnResume.setLayoutParams(bResume);
        btnRestart.setLayoutParams(bRestart);
        btnExit.setLayoutParams(bExit);

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

        private float score = 0;
        private float randPosY;

        Bullet bullet;
        Enemy enemy;

        float timerEnemy = 0;

        ArrayList<Bullet> bullets = new ArrayList<>();
        ArrayList<Enemy> enemies = new ArrayList<>();

        Long bestScore = Long.valueOf(0);

        SoundPool soundPool;
        int bgSfx = -1;
        int shootSfx = -1;
        int enemyDieSfx = -1;
        int loseLifeID = -1;
//        int explodeID = -1;

        MediaPlayer bg;

        public GameView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            playing = true;

            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(getUsername());

            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("shoot.wav");
                shootSfx = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("hit.mp3");
                enemyDieSfx = soundPool.load(descriptor, 0);

            } catch (IOException e) {
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

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
            float min = 200, max = 650;
            randPosY = (float) Math.random() * max + min;
            parallax.update(fps);
            player.update(fps);

            if (enemies.size() > 0) {
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).update(fps);
                }
            }

            if (bullets.size() > 0) {
                for (int i = 0; i < bullets.size(); i++) {
                    bullets.get(i).update(fps);
                }
            }

            btnJump.setOnClickListener(this);
            btnShoot.setOnClickListener(this);
            btnPause.setOnClickListener(this);
            btnResume.setOnClickListener(this);
            btnRestart.setOnClickListener(this);
            btnExit.setOnClickListener(this);
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

                if (bullets.size() > 0) {
                    for (int i = 0; i < bullets.size(); i++) {
                        if (bullets.get(i) != null)
                            bullets.get(i).drawBitmap(canvas);
                    }
                }

                if (bullets.size() > 0) {
                    for (int i = 0; i < bullets.size(); i++) {
                        if (bullets.get(i).getBulletMove() > width) {
                            bullets.remove(i);
                        }
                    }
                }

                for (int i = 0; i < bullets.size(); i++) {
                    for (int j = 0; j < enemies.size(); j++) {
                        if (i < bullets.size() && j < enemies.size()) {
                            if (RectF.intersects(bullets.get(i).getCollision(), enemies.get(j).getCollision())) {
                                soundPool.play(enemyDieSfx, 1, 1, 0, 0, 1);
                                bullets.remove(i);
                                enemies.get(j).setVisible(false);
                                score++;
                            }
                        }
                    }
                }

                if (enemies.size() > 0) {
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.get(i).drawBitmap(canvas);
                    }
                }

                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).getEnemyMove() < -200) {
                        enemies.get(i).setVisible(false);
                    }
                }

                for (int i = 0; i < enemies.size(); i++) {
                    if (!enemies.get(i).getVisible()) {
                        enemies.get(i).setEnemyMove(width);
                        enemies.get(i).setVisible(true);
                        enemies.get(i).setPosYBullet(randPosY);
                    }
                }

                for (int i = 0; i < enemies.size(); i++) {
                    if (RectF.intersects(enemies.get(i).getCollision(), player.getCollision())) {
                        bg.pause();
                        paused = true;
                        isGameOver = true;
                        uploadScore();
                        setGameOverOn();
                    }
                }

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bestScore = (Long) snapshot.child("score").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                paint.setColor(Color.WHITE);
                paint.setTextSize(72);
                canvas.drawText("Score : " + (int) score, 120, 120, paint);
                canvas.drawText("Best Score : " + bestScore, 600, 120, paint);

                //canvas.drawText("FPS:" + fps, 120, 120, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void createEnemy() {
            float posAwal = 100;
            for (int i = 0; i < 6; i++) {
                enemy = new Enemy(width, height, getResources());
                enemy.setEnemyMove(width);
                enemy.setVisible(true);
                enemy.setPosYBullet(posAwal);
                enemies.add(enemy);
                posAwal += 100;
            }

        }

        public void createAndRestart() {
            bg = MediaPlayer.create(getContext(), R.raw.bg_playgame);
            bg.setLooping(true);
            bg.start();
            parallax = new Parallax(width, height, getResources());
            player = new Player(width, height, getResources());
            enemies.clear();
            bullets.clear();
            createEnemy();
            btnPause.setAlpha(1);
            score = 0;
            isGameOver = false;
            paused = false;
            setGameOverOff();
            setResume();
        }

        public void uploadScore() {
            if (!getUsername().equals("")) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (score > bestScore)
                            snapshot.getRef().child("score").setValue(Long.valueOf((long) score));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        public void setGameOverOn() {
            btnExit.setTranslationY(100);
            btnExit.setTranslationX(-200);
            btnExit.setAlpha(1);

            btnRestart.setTranslationY(100);
            btnRestart.setTranslationX(200);
            btnRestart.setAlpha(1);

            btnPause.setEnabled(false);
        }

        public void setGameOverOff() {
            btnPause.setEnabled(true);

            btnExit.setTranslationY(height);
            btnExit.setAlpha(0);

            btnRestart.setTranslationY(height);
            btnRestart.setAlpha(0);
        }

        public void setPauseOn() {
            btnExit.setTranslationY(100);
            btnExit.setTranslationX(0);
            btnExit.setAlpha(1);

            btnResume.setTranslationY(-100);
            btnResume.setAlpha(1);
        }

        public void setResume() {
            btnExit.setAlpha(0);
            btnExit.setTranslationY(height);
            btnResume.setAlpha(0);
            btnResume.setTranslationY(height);
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
            switch (v.getId()) {
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
            }

        }

        private void BtnPause() {
            paused = true;
            setPauseOn();
        }

        private void BtnResume() {
            paused = false;
            setResume();
        }

        private void BtnRestart() {
            createAndRestart();
        }

        private void BtnExit() {
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        }

        private void BtnJump() {
            player.jump(fps);
            Player.i = 0;
            Player.timer = 0;
        }

        private void BtnShoot() {
            if (bullets.size() < 10) {
                soundPool.play(shootSfx, 1, 1, 0, 0, 1);
                bullet = new Bullet(width, height, getResources());
                bullet.setBulletMove(120);
                bullet.setPosYBullet(player.getPlayerPosY());
                bullets.add(bullet);
            }
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

    String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(KEYLOGIN, MODE_PRIVATE);
        return sharedPreferences.getString(keylogin, "");
    }
}
