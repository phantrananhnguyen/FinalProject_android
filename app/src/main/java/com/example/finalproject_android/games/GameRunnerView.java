package com.example.finalproject_android.games;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.finalproject_android.R;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRunnerView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private boolean isPlaying = true;

    private Paint paint;
    private SurfaceHolder holder;
    private Bitmap playerHumanBitmap, motorcycleBitmap, carBitmap;
    private Bitmap currentPlayerBitmap;

    private Bitmap potholeBitmap, backgroundBitmap;
    private int backgroundX;  // Vị trí X của background để di chuyển

    private int screenWidth, screenHeight;
    private int playerX, playerY;  // Vị trí xe
    private int speed = 10;  // Tốc độ di chuyển chướng ngại vật
    private int score = 0;

    private ArrayList<Pothole> potholes;  // Danh sách các ổ gà
    private Random random;

    private Vibrator vibrator;  // Dùng để rung khi va chạm

    private boolean isGameOver = false;
    private Rect restartButtonRect;
    private SoundPool soundPool;
    private int hitSound, upgradeSound, scoreSound;
    private MediaPlayer backgroundMusic;



    public GameRunnerView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        holder = getHolder();
        paint = new Paint();

        // Load hình ảnh bus
        carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bus);
        // Tạo một bitmap mới có kích thước đã thay đổi
        carBitmap = Bitmap.createScaledBitmap(carBitmap, carBitmap.getWidth(), carBitmap.getHeight(), true);

        playerHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.human); // Nhân vật người
        playerHumanBitmap = Bitmap.createScaledBitmap(playerHumanBitmap, playerHumanBitmap.getWidth(), playerHumanBitmap.getHeight(), true);

        motorcycleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cycle); // Xe máy
        motorcycleBitmap = Bitmap.createScaledBitmap(motorcycleBitmap, motorcycleBitmap.getWidth(), motorcycleBitmap.getHeight(), true);

        // Tạo đối tượng BitmapFactory.Options
        BitmapFactory.Options options = new BitmapFactory.Options();

// Chỉ decode kích thước (không load bitmap thật) để lấy thông tin chiều rộng và chiều cao
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.pothole_art, options);

// Tính toán tỉ lệ giảm kích thước (sample size)
        int desiredWidth = 100;  // Chiều rộng mong muốn (pixel)
        int desiredHeight = 100; // Chiều cao mong muốn (pixel)
        int sampleSize = 1;

        while (options.outWidth / sampleSize > desiredWidth || options.outHeight / sampleSize > desiredHeight) {
            sampleSize *= 4; // Giảm kích thước theo bội số của 2
        }

// Đặt sample size và decode bitmap thật
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        potholeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pothole_art, options);


        // Điều chỉnh kích thước background
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        // Tính toán tỷ lệ
        float ratio = (float) backgroundBitmap.getHeight() / (float) backgroundBitmap.getWidth();

        // Tính chiều rộng mới dựa trên chiều cao của màn hình
        int newHeight = screenHeight;
        int newWidth = (int) (newHeight / ratio);

        // Thay đổi kích thước background
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, false);

        // Vị trí ban đầu của xe
        playerX = 200;
        int roadHeight = screenHeight / 5;  // Đoạn đường chiếm 1/5 phần dưới của màn hình
        playerY = screenHeight - roadHeight - playerHumanBitmap.getHeight();  // Đặt xe ở trên đường, cách đáy màn hình một khoảng

        // Khởi tạo danh sách chướng ngại vật
        potholes = new ArrayList<>();
        random = new Random();

        // Khởi tạo Vibrator
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Khởi tạo background di chuyển
        backgroundX = 0;

        // Khởi tạo playerBitmap
        currentPlayerBitmap = playerHumanBitmap; // Ban đầu là người

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

// Load các âm thanh từ res/raw
        hitSound = soundPool.load(getContext(), R.raw.hit, 1);
        upgradeSound = soundPool.load(getContext(), R.raw.upgrade, 1);
        scoreSound = soundPool.load(getContext(), R.raw.score, 1);


    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        // Di chuyển các chướng ngại vật
        for (int i = 0; i < potholes.size(); i++) {
            Pothole pothole = potholes.get(i);
            pothole.x -= speed;

            if (!isGameOver) {
                for (int j = 0; j < potholes.size(); j++) {
                    // Kiểm tra va chạm và dừng game
                    if (playerX < pothole.x + potholeBitmap.getWidth() && playerX + currentPlayerBitmap.getWidth() > pothole.x &&
                            playerY < pothole.y + potholeBitmap.getHeight() && playerY + currentPlayerBitmap.getHeight() > pothole.y) {
                        isGameOver = true;  // Trò chơi kết thúc
                        vibrator.vibrate(500);  // Rung 500ms
                        return;
                    }
                }
            }
            if (isGameOver) {
               // gameOver();
                return;  // Không cập nhật thêm khi game đã kết thúc
            }


            // Nếu chướng ngại vật ra khỏi màn hình thì xóa nó và tăng điểm
            if (pothole.x < 0) {
                potholes.remove(i);
                score += 10;  // Tăng điểm

                i--;
            }


        }

        // Random thêm chướng ngại vật nhưng chỉ ở khu vực "road"
        if (random.nextInt(100) < 5 && potholes.size() < 5) {  // Chỉ thêm nếu dưới 5 chướng ngại vật
            createPothole();
        }


        // Di chuyển background
        backgroundX -= 5;  // Tốc độ di chuyển của background
        if (backgroundX <= -backgroundBitmap.getWidth()) {
            backgroundX = 0;  // Reset lại background khi đã đi hết màn hình
        }



        // Nâng cấp người chơi theo điểm
        upgradePlayerWithEffect();
    }

    private void gameOver() {
        if (isGameOver) {
            // Tính toán điểm (nếu lớn hơn 1000, chia cho 10)
            int finalScore = (score > 1000) ? (int) Math.round((double) score / 10) : 0;

            if (finalScore > 0) {

                // Gửi điểm lên backend để cập nhật
                sendScoreToBackend(finalScore);
            }
        }
    }

    private void sendScoreToBackend(int finalScore) {


        // Khởi tạo ApiService
        ApiService apiService = ApiClient.getApiServiceWithToken(getContext());

        // Gửi yêu cầu cập nhật điểm lên backend
        Call<ResponseBody> call = apiService.updateScore(finalScore);

        // Thực hiện yêu cầu bất đồng bộ
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Cập nhật thành công
                    Log.d("GameOver", "Điểm đã được cập nhật thành công");
                } else {
                    // Xử lý khi response không thành công
                    Log.d("GameOver", "Cập nhật điểm thất bại");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý khi request bị lỗi
                Log.e("GameOver", "Request thất bại", t);
            }
        });
    }



    private void createPothole() {
        int roadHeight = screenHeight / 5;  // Đoạn đường chiếm 1/5 phần dưới của màn hình
        int roadTopY = screenHeight - roadHeight; // Đỉnh đường
        int roadBottomY = screenHeight - currentPlayerBitmap.getHeight() - 175; // Đáy đường hợp lệ

        // Random vị trí y trong phạm vi đường hợp lệ
        int potholeY = roadTopY + (int) (Math.random() * (roadBottomY - roadTopY));

        // Đặt pothole bắt đầu ở ngoài lề phải của màn hình
        int potholeX = screenWidth; // Bắt đầu ngoài lề phải

        // Tạo và thêm pothole mới
        potholes.add(new Pothole(potholeX, potholeY));
    }


    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE); // Xóa màn hình

            // Vẽ background (di chuyển liên tục và mượt mà)
            canvas.drawBitmap(backgroundBitmap, backgroundX, 0, paint); // Vẽ phần background đầu tiên
            canvas.drawBitmap(backgroundBitmap, backgroundX + backgroundBitmap.getWidth(), 0, paint); // Vẽ phần background thứ hai

            // Vẽ nhân vật (xe) với hiệu ứng scale nếu đang chuyển đổi
            if (isAnimating) {
                int centerX = playerX + currentPlayerBitmap.getWidth() / 2;
                int centerY = playerY + currentPlayerBitmap.getHeight() / 2;

                canvas.save();
                canvas.scale(scale, scale, centerX, centerY); // Áp dụng scale tại tâm của nhân vật
                canvas.drawBitmap(currentPlayerBitmap, playerX, playerY, paint);
                canvas.restore();
            } else {
                // Vẽ nhân vật bình thường khi không có hiệu ứng
                canvas.drawBitmap(currentPlayerBitmap, playerX, playerY, paint);
            }

            // Vẽ các chướng ngại vật (ổ gà)
            for (Pothole pothole : potholes) {
                canvas.drawBitmap(potholeBitmap, pothole.x, pothole.y, paint);
            }

            // Vẽ trạng thái Game Over và nút Restart
            if (isGameOver) {
                soundPool.play(hitSound, 1, 1, 0, 0, 1);

                // Vẽ nút Restart
                drawRestartButton(canvas);
            }

            // Hiển thị điểm
            drawScore(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    // Hiển thị điểm số đẹp hơn
    private void drawScore(Canvas canvas) {
        int padding = 30; // Padding cho phần nền điểm số
        int cornerRadius = 25; // Góc bo tròn của nền

        // Vẽ nền cho điểm số
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#80000000")); // Màu đen với độ trong suốt 50%
        int scoreBackgroundX = 40;
        int scoreBackgroundY = 40;
        int scoreBackgroundWidth = 300;
        int scoreBackgroundHeight = 120;
        RectF scoreBackgroundRect = new RectF(scoreBackgroundX, scoreBackgroundY, scoreBackgroundX + scoreBackgroundWidth, scoreBackgroundY + scoreBackgroundHeight);
        canvas.drawRoundRect(scoreBackgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // Vẽ chữ điểm số
        Paint textPaint = new Paint();
        textPaint.setColor(Color.YELLOW); // Màu vàng cho chữ
        textPaint.setTextSize(80); // Tăng kích thước chữ
        textPaint.setFakeBoldText(true); // Chữ đậm hơn
        textPaint.setShadowLayer(10, 5, 5, Color.BLACK); // Hiệu ứng bóng chữ

        // Căn chỉnh chữ bên trong nền
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textX = scoreBackgroundX + scoreBackgroundWidth / 2; // Giữa nền theo chiều ngang
        float textY = scoreBackgroundY + scoreBackgroundHeight / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2; // Giữa nền theo chiều dọc
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Score: " + score, textX, textY, textPaint);
    }


    // Vẽ nút Restart với thiết kế đẹp hơn
    private void drawRestartButton(Canvas canvas) {
        int buttonWidth = 400;
        int buttonHeight = 200;
        int buttonX = screenWidth / 2 - buttonWidth / 2;
        int buttonY = screenHeight / 2 - buttonHeight / 2;

        // Tạo RectF để vẽ nút với góc bo tròn
        RectF restartButtonRectF = new RectF(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight);

        // Vẽ gradient (chuyển màu) cho nút
        Paint buttonPaint = new Paint();
        LinearGradient gradient = new LinearGradient(
                buttonX, buttonY, buttonX, buttonY + buttonHeight,
                Color.parseColor("#FF5722"), // Màu cam sáng
                Color.parseColor("#E91E63"), // Màu hồng đậm
                Shader.TileMode.CLAMP
        );
        buttonPaint.setShader(gradient);
        buttonPaint.setStyle(Paint.Style.FILL);

        // Vẽ nền nút với góc bo tròn
        canvas.drawRoundRect(restartButtonRectF, 50, 50, buttonPaint); // Góc bo tròn 50px

        // Vẽ viền cho nút
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(8);
        canvas.drawRoundRect(restartButtonRectF, 50, 50, borderPaint);

        // Vẽ chữ "Restart"
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(70);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Căn chỉnh chữ vào giữa nút
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textX = buttonX + buttonWidth / 2; // Giữa nút theo chiều ngang
        float textY = buttonY + buttonHeight / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2; // Giữa nút theo chiều dọc
        canvas.drawText("Restart", textX, textY, textPaint);

        // Lưu vùng nút để xử lý sự kiện chạm
        restartButtonRect = new Rect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight);
    }


    private void sleep() {
        try {
            Thread.sleep(16);  // 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }




        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();

    }

    private int currentUpgradeLevel = 1;

    private void upgradePlayerWithEffect() {
        post(() -> {
            if (score >= 200 && currentUpgradeLevel == 1) {
                soundPool.play(upgradeSound, 1, 1, 0, 0, 1);
                currentUpgradeLevel = 2; // Cập nhật cấp độ nâng cấp
                speed += 3;
                animatePlayerTransition(motorcycleBitmap);
            } else if (score >= 400 && currentUpgradeLevel == 2) {
                soundPool.play(upgradeSound, 1, 1, 0, 0, 1);
                currentUpgradeLevel = 3;
                speed += 3;
                animatePlayerTransition(carBitmap);
            }

        });
    }

    private Bitmap newPlayerBitmap; // Bitmap mới khi chuyển đổi
    private float scale = 1.0f; // Tỷ lệ scale
    private boolean isAnimating = false; // Đang chuyển đổi animation


    private void animatePlayerTransition(Bitmap newBitmap) {
        // Đảm bảo rằng animation chạy trên Main Thread
        post(() -> {
            Bitmap oldBitmap = currentPlayerBitmap; // Lưu bitmap hiện tại (cũ)

            // Animation scale từ 1.0 đến 1.5
            ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.5f, 1.0f);
            animator.setDuration(500); // Thời gian hiệu ứng 500ms

            animator.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();

                // Tạo bitmap với tỷ lệ scale
                currentPlayerBitmap = Bitmap.createScaledBitmap(newBitmap,
                        (int) (newBitmap.getWidth() * scale),
                        (int) (newBitmap.getHeight() * scale),
                        true);

                invalidate(); // Vẽ lại màn hình
            });

            // Khi kết thúc animation, gán bitmap mới hoàn toàn
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentPlayerBitmap = newBitmap; // Gán bitmap mới
                    invalidate(); // Vẽ lại giao diện với bitmap mới
                }
            });

            animator.start(); // Bắt đầu animation
        });
    }


    private void restartGame() {
        score = 0;
        potholes.clear();  // Xóa toàn bộ ổ gà

        playerX = 200;
        int roadHeight = screenHeight / 5;  // Đoạn đường chiếm 1/5 phần dưới của màn hình
        playerY = screenHeight - roadHeight - playerHumanBitmap.getHeight();  // Đặt xe ở trên đường, cách đáy màn hình một khoảng

        // Reset nhân vật về trạng thái ban đầu
        currentPlayerBitmap = playerHumanBitmap;

        // Reset trạng thái trò chơi
        isGameOver = false;
        isPlaying = true;

        // Nếu bạn có các giá trị tốc độ hoặc nâng cấp, cũng cần reset chúng
        speed = 10;  // Đặt tốc độ về giá trị mặc định
        currentUpgradeLevel = 1;  // Reset cấp độ nâng cấp
    }


    private boolean isJumping = false; // Kiểm tra xem nhân vật có đang nhảy không

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            if (restartButtonRect != null && restartButtonRect.contains((int) event.getX(), (int) event.getY())) {
                restartGame(); // Hàm restart
                return true;
            }
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isJumping) { // Chỉ nhảy khi không đang nhảy
                    isJumping = true;

                    // Tạo hiệu ứng nhảy lên
                    ValueAnimator jumpUp = ValueAnimator.ofInt(playerY, playerY - 300); // Nhảy lên 300px
                    jumpUp.setDuration(300); // 300ms
                    jumpUp.addUpdateListener(animation -> playerY = (int) animation.getAnimatedValue());
                    jumpUp.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Khi nhảy lên xong, bắt đầu rơi xuống
                            fallDown();
                        }
                    });
                    jumpUp.start();
                }
                break;
        }
        return true;
    }

    private void fallDown() {
        int roadHeight = screenHeight / 5;  // Đoạn đường chiếm 1/5 phần dưới của màn hình
        // Xác định giới hạn rơi xuống hợp lệ trên đường
        int roadTopY = screenHeight - roadHeight; // Vị trí trên cùng của đường
        int roadBottomY = screenHeight - currentPlayerBitmap.getHeight() - 175; // Vị trí dưới cùng hợp lệ

        // Lựa chọn vị trí ngẫu nhiên hợp lệ trong phạm vi đường
        int targetY = roadTopY + (int) (Math.random() * (roadBottomY - roadTopY));

        // Tạo hiệu ứng rơi xuống đến vị trí hợp lệ
        ValueAnimator fallDown = ValueAnimator.ofInt(playerY, targetY);
        fallDown.setDuration(500); // 500ms
        fallDown.addUpdateListener(animation -> playerY = (int) animation.getAnimatedValue());
        fallDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isJumping = false; // Cho phép nhảy lần tiếp theo
            }
        });
        fallDown.start();
    }

    // Lớp ổ gà (chướng ngại vật)
    private static class Pothole {
        int x, y;

        public Pothole(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
