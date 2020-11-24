package com.febrian.fpgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
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
    Button btnJump,btnShoot;

    float bullet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        GameButtons = new RelativeLayout(this);
        game = new FrameLayout(this);
        btnJump = new Button(this);
        btnJump.setText("Jump");
        btnShoot = new Button(this);
        btnShoot.setText("Shoot");
        RelativeLayout.LayoutParams b1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams b2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        GameButtons.setLayoutParams(params);
        GameButtons.addView(btnJump);
        GameButtons.addView(btnShoot);
        b1.bottomMargin = 24;
        b1.leftMargin = 128;
        b1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        b1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        b2.bottomMargin = 24;
        b2.rightMargin = 128;
        b2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        b2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        btnJump.setLayoutParams(b1);
        btnShoot.setLayoutParams(b2);
        game.addView(gameView);
        game.addView(GameButtons);
        setContentView(game);
    }

    class GameView extends SurfaceView implements Runnable {
        int width, height, Xmax;

        //untuk mengakses index array bitmap
        int i = 0;
        //variable untuk set waktu saat berganti frame
        float timer;

        //variable untuk mengcek kondisi pas flip
        boolean isFlip = false;
        //variable untuk menyimpan value slip
        float flip = 1.0f;
        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // Simpan sprite pada array object bitmap
        Bitmap bitmapBob[] = {
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run0), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run1), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run2), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run3), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run4), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run5), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run6), 120, 120, false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run7), 120, 120, false)
        };

        Bitmap bg[] = new Bitmap[2];
        Bitmap player;

        // Bob starts off not moving
        boolean isMoving = false;

        // He can walk at 150 pixels per second
        float walkSpeedPerSecond = 150;

        // He starts 10 pixels from the left
        float bobXPosition = 10;

        float posX = 0,posX2 = 2100;
        float playerPosY;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);
            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            float timer;

            // Set our boolean to true - game on!
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

            bg[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg), width, height, false);
            bg[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg), width, height, false);
            player = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.run0), 120,120, false);
            playerPosY = height/2;
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                update();

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;

                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        // Everything that needs to be updated goes in here
        // In later projects we will have dozens (arrays) of objects.
        // We will also do other things like collision detection.
        public void update() {
            posX -= fps * 0.1;
            posX2 -= fps * 0.1;
            if (posX <= -2100)
                posX = 2100;
            if(posX2 <= -2100)
                posX2 = 2100;

            /*Animation Char Start*/
            timer += fps;
            if(timer > 120){
                i++;
                timer = 0;
            }

            if(i == bitmapBob.length){
                i = 0;
            }
            /*Animation Char end*/

            playerPosY += 2;

            if(playerPosY <= 0){
                playerPosY = 0;
            }

            if(playerPosY >= height-220){
                playerPosY = height-220;
            }

            //Button Jump
            btnJump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerPosY -= fps * 1f;
                }
            });
            bullet++;
            btnShoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    canvas.drawRect(120,120,120,120,null);
                }
            });
        }

        // Draw the newly updated scene
        public void draw() {
            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                //Inisialisasi object bitmap yang menyimpan method flipImage
                Bitmap bob = flipImage(bitmapBob[i]);

                //Draw Bitmap
                // canvas.drawBitmap(bob, bobXPosition, 200, paint);

               // canvas.drawBitmap(bg[0], posX, 0, null);

                //BG
                canvas.drawBitmap(bg[0], posX,0, null);
                canvas.drawBitmap(bg[1], posX2,0, null);

                //Player
          //      canvas.drawBitmap(player, 120,playerPosY, null);

                canvas.drawBitmap(bitmapBob[i], 120,playerPosY,null);

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public Bitmap flipImage(Bitmap source) {
            //deklarasi matrik untuk flip Bitmap
            Matrix matrix = new Matrix();
            //jika isFlip sama dengan true, kalikan flip dg -1
            if (isFlip)
                flip *= -1.0f;
            // set matrix prescale untuk membalikkan posisi bitmap
            matrix.preScale(flip, 1.0f);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    // Set isMoving so Bob is moved in the update method
                      isMoving = true;
                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    // Set isMoving so Bob does not move
                    isMoving = false;

                    break;
            }
            return true;
        }

    }
    // This is the end of our GameView inner class

    // More SimpleGameEngine methods will go here

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }
}