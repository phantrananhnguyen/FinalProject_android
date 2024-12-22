package com.example.finalproject_android.games;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.example.finalproject_android.R;
import com.example.finalproject_android.network.ApiClient;
import com.example.finalproject_android.network.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameRepairView extends SurfaceView implements Runnable {
        private Pothole selectedPothole = null;
        private SurfaceHolder holder;
        private Paint paint;
        private Thread gameThread;
        private boolean isPlaying;

        private List<Pothole> potholes;
        private List<Car> cars;
        private Repairman repairman;

        private int score;
        private boolean gameOver;

        private MediaPlayer repairSound;
        private MediaPlayer backgroundMusic;
        private Bitmap backgroundBitmap;
        private Bitmap background, carBitmap;
        private Bitmap repairmanBitmap;
        private Bitmap[] carBitmaps;

        private int screenWidth, screenHeight;
        private Map<String, Bitmap[]> activityFrames;

        public Bitmap flipBitmapHorizontally(Bitmap srcBitmap) {
            // Tạo một đối tượng Matrix
            Matrix matrix = new Matrix();

            // Lật ảnh theo chiều ngang: scaleX = -1 (đảo chiều ngang)
            matrix.preScale(-1.0f, 1.0f);

            // Tạo Bitmap mới đã lật từ Bitmap gốc
            return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }


        public GameRepairView(Context context) {
            super(context);
            holder = getHolder();
            paint = new Paint();

            potholes = new ArrayList<>();
            cars = new ArrayList<>();
            score = 0;
            gameOver = false;

            // Load âm thanh
            repairSound = MediaPlayer.create(context, R.raw.repair);

            // Get screen dimensions trước khi sử dụng
            screenWidth = getResources().getDisplayMetrics().widthPixels;
            screenHeight = getResources().getDisplayMetrics().heightPixels;

// Load và thay đổi kích thước background
            backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background1);

            if (backgroundBitmap != null && backgroundBitmap.getWidth() > 0 && backgroundBitmap.getHeight() > 0) {
                // Tính tỷ lệ (aspect ratio)
                float ratio = (float) backgroundBitmap.getHeight() / (float) backgroundBitmap.getWidth();

                // Tính chiều rộng và chiều cao mới
                int newHeight = screenHeight;
                int newWidth = (int) (newHeight / ratio);

                try {
                    // Thay đổi kích thước background
                    backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newWidth, newHeight, false);
                } catch (Exception e) {
                    Log.e("GameRepairView", "Failed to scale background bitmap: " + e.getMessage());
                    backgroundBitmap = null; // Fallback khi không thay đổi kích thước được
                }
            } else {
                Log.e("GameRepairView", "Background bitmap is invalid or could not be loaded.");
                backgroundBitmap = null; // Đặt null nếu không thể tải ảnh
            }


            // Load các hình ảnh cho xe
            carBitmaps = new Bitmap[]{
                    flipBitmapHorizontally(loadBitmapSafely(R.drawable.towtruck)),
                    flipBitmapHorizontally(loadBitmapSafely(R.drawable.truck)),
                    flipBitmapHorizontally(loadBitmapSafely(R.drawable.vendor))
            };

            // Tạo các frame cho từng hoạt động
            activityFrames = new HashMap<>();
            activityFrames.put("cheer", loadFrames("character_male_adventurer_cheer", 4));
            activityFrames.put("walk", loadFrames("character_male_adventurer_walk", 8));
            activityFrames.put("run", loadFrames("character_male_adventurer_run", 3));
            activityFrames.put("repair", loadFrames("character_male_adventurer_attack", 6));

            // Tạo Repairman và thêm vào danh sách
            repairman = new Repairman(randomX(), screenHeight - 700, 10, activityFrames, 100);
            repairman.setCurrentActivity("cheer", activityFrames);


            // Initialize game objects
            spawnCar();
            spawnPothole();

        }



        // Hàm load bitmap an toàn (giảm lỗi null)
        private Bitmap loadBitmapSafely(int resourceId) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
            if (bitmap == null) {
                throw new IllegalArgumentException("Failed to load resource: " + resourceId);
            }
            return bitmap;
        }

        // Hàm load các frame animation
        private Bitmap[] loadFrames(String prefix, int frameCount) {
            Bitmap[] frames = new Bitmap[frameCount];
            for (int i = 0; i < frameCount; i++) {
                int resourceId = getResources().getIdentifier(prefix + i, "drawable", getContext().getPackageName());
                if (resourceId == 0) {
                    throw new IllegalArgumentException("Resource not found: " + prefix + "_" + i);
                }
                frames[i] = BitmapFactory.decodeResource(getResources(), resourceId);
                if (frames[i] == null) {
                    throw new IllegalArgumentException("Failed to load frame: " + prefix + "_" + i);
                }
            }
            return frames;
        }



        private void updateGame() {
            if (gameOver) {
                gameOver();
                return;
            }

            // Move cars and check for collisions
            synchronized (cars) {
                Iterator<Car> carIterator = cars.iterator();
                while (carIterator.hasNext()) {
                    Car car = carIterator.next();

                    // Chỉ di chuyển xe trong phạm vi đường
                    if (car.x >= 0 && car.x <= screenWidth) {
                        car.move(); // Di chuyển xe
                    } else {
                        carIterator.remove(); // Xóa xe nếu nó ra khỏi màn hình
                    }

                    synchronized (potholes) {
                        for (Pothole pothole : potholes) {
                            if (car.hasCollided(pothole)) {
                                // Trừ điểm khi xe va chạm pothole
                                score -= 10;
                                if (score < 0) {
                                    score = 0; // Đảm bảo điểm không bị âm
                                }
                                Log.d("Collision", "Car collided with pothole, score: " + score);

                                // Chỉ đặt gameOver nếu điểm thực sự giảm xuống 0
                                if (score == 0) {
                                    gameOver = true; // Kết thúc game nếu điểm <= 0
                                    Log.d("GameOver", "Game Over! Final score: " + score);
                                }
                            }
                        }
                    }
                }
            }

            // Repair potholes
            synchronized (potholes) {
                Iterator<Pothole> potholeIterator = potholes.iterator();
                while (potholeIterator.hasNext()) {
                    Pothole pothole = potholeIterator.next();
                    if (pothole.isBeingRepaired) {
                        pothole.repair();
                        if (pothole.isFixed) {
                            score += 50; // Thêm điểm khi sửa chữa thành công
                            pothole.isBeingRepaired = false; // Reset repair state
                            repairman.setCurrentActivity("cheer", activityFrames);

                            // Xóa pothole khỏi danh sách khi đã sửa chữa xong
                            potholeIterator.remove();
                            Log.d("Repair", "Pothole repaired, score: " + score);
                        }
                    }
                }
            }

            // Move repairman and trigger repair
            if (selectedPothole != null && repairman != null) {
                float targetX = selectedPothole.getX();
                float targetY = selectedPothole.getY();

                // Khoảng cách gần pothole theo chiều dọc (y-axis)
                float distanceToMove = 400;  // Khoảng cách gần pothole (có thể thay đổi tùy ý)

                // Chỉ thay đổi vị trí theo chiều dọc (y-axis)
                float newY = targetY - distanceToMove; // Di chuyển lên 400 pixels
                float newX = targetX - 100; // Di chuyển theo chiều ngang

                // Log the position of repairman and pothole
                Log.d("RepairmanMove", "Repairman moving to X: " + targetX + ", Y: " + newY);
                Log.d("RepairmanPosition", "Repairman current position X: " + repairman.getX() + ", Y: " + repairman.getY());

                // Di chuyển repairman đến điểm gần pothole trên chiều dọc
                if (!repairman.hasReached(newX, newY)) {
                    repairman.moveTo(newX, newY);
                } else if (!selectedPothole.isFixed) {
                    repairman.setCurrentActivity("repair", activityFrames);
                    selectedPothole.isBeingRepaired = true;
                }
            }
        }

    private void gameOver() {
        if (gameOver) {
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




        private void spawnPothole() {
            new Handler().postDelayed(() -> {
                // Kiểm tra xem số lượng pothole hiện tại có nhỏ hơn 4 hay không
                if (potholes.size() < 4) {
                    // Tính toán lại vị trí Y của pothole trong phạm vi đường
                    float roadTop = screenHeight - 240;
                    float roadBottom = screenHeight - 170; // Đáy của đường

                    // Kiểm tra xem chiều cao của đường có hợp lệ hay không (cần phải > 0)
                    if (roadBottom > roadTop) {
                        float randomX = randomX(); // Đảm bảo randomX cung cấp giá trị X hợp lệ
                        float randomY = roadTop + new Random().nextInt((int) (roadBottom - roadTop)); // Đảm bảo Y nằm trong vùng đường
                        int potholeSize = randomSize(); // Chọn kích thước pothole ngẫu nhiên

                        potholes.add(new Pothole(randomX, randomY, potholeSize)); // Tạo pothole mới
                    }
                }

                spawnPothole();  // Tiếp tục gọi để tạo potholes mới liên tục
            }, 5000); // Delay giữa các lần tạo
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (gameOver) {
                // Kiểm tra xem người chơi có nhấn vào nút Restart không
                float restartButtonX = (screenWidth - paint.measureText("Restart")) / 2;
                float restartButtonY = screenHeight * 0.6f;
                float buttonWidth = paint.measureText("Restart");
                float buttonHeight = 60; // Chiều cao của nút

                // Kiểm tra nếu người chơi nhấn vào khu vực nút Restart
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();

                    if (x >= restartButtonX - 20 && x <= restartButtonX + buttonWidth + 20 &&
                            y >= restartButtonY - 50 && y <= restartButtonY + 10) {
                        restartGame(); // Nếu nhấn vào nút Restart, gọi hàm restartGame()
                        return true;
                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                // Kiểm tra nếu người chơi nhấn vào một pothole
                for (Pothole pothole : potholes) {
                    if (pothole.contains(touchX, touchY) && !pothole.isFixed) {
                        selectedPothole = pothole;
                        repairSound.start();

                        // Chỉ duy nhất 1 repairman di chuyển đến gần pothole
                        // Thay vì di chuyển đến chính xác vị trí của pothole, tính toán một điểm gần đó
                        float potholeX = pothole.getX();
                        float potholeY = pothole.getY();

                        // Tính toán một điểm gần hố (ví dụ: cách hố 50px)
                        float distanceToMove = 400;  // Khoảng cách gần hố
                        float newY = potholeY - distanceToMove;
                        float newX = potholeX - 100; // Di chuyển theo chiều ngang

                        repairman.moveTo(newX, newY);  // Di chuyển đến điểm gần hố
                        repairman.setCurrentActivity("walk", activityFrames);
                        break;
                    }
                }
            }
            return true;
        }


        private void drawGame(Canvas canvas) {
            if (canvas == null) return;

            // Kiểm tra backgroundBitmap trước khi vẽ
            if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
                canvas.drawBitmap(backgroundBitmap, 0, 0, null);
            } else {
                if (backgroundBitmap == null) {
                    Log.e("GameRepairView", "Background is null");
                    return; // Dừng vẽ nếu background không hợp lệ
                }
                Log.e("GameRepairView", "Background is recycled");
            }


            // Vẽ potholes
            List<Pothole> potholeSnapshot;
            synchronized (potholes) {
                potholeSnapshot = new ArrayList<>(potholes);
            }
            for (Pothole pothole : potholeSnapshot) {
                if (pothole != null) {
                    pothole.draw(canvas, paint);
                }
            }

            // Vẽ cars
            List<Car> carSnapshot;
            synchronized (cars) {
                carSnapshot = new ArrayList<>(cars);
            }
            for (Car car : carSnapshot) {
                if (car != null && car.type >= 0 && car.type < carBitmaps.length && carBitmaps[car.type] != null) {
                    car.draw(canvas, carBitmaps[car.type], paint);
                }
            }


            // Vẽ repairman duy nhất
            if (repairman != null) {
                repairman.draw(canvas);
            }

            if (score >= 0) {
                // Hiển thị điểm số với phong cách hiện đại
                paint.reset();
                paint.setColor(Color.WHITE);
                paint.setTextSize(80);
                paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
                paint.setShadowLayer(8, 0, 0, Color.parseColor("#00FFFF")); // Hiệu ứng phát sáng neon cyan
                String scoreText = "Score: " + score;
                canvas.drawText(scoreText, 50, 100, paint);
            }

// Game Over giao diện
            if (gameOver) {
                // Nền mờ với gradient hiện đại
                Paint backgroundPaint = new Paint();
                backgroundPaint.setShader(new LinearGradient(0, 0, 0, screenHeight,
                        Color.argb(230, 50, 50, 50), Color.argb(100, 0, 0, 0), Shader.TileMode.CLAMP));
                canvas.drawRect(0, 0, screenWidth, screenHeight, backgroundPaint);

                // "Game Over" hiện đại với kích thước nhỏ và glow nhẹ
                String gameOverText = "GAME OVER";
                paint.reset(); // Reset cài đặt cũ
                paint.setTextSize(120); // Giảm kích thước chữ
                paint.setColor(Color.parseColor("#FFFFFF")); // Màu trắng hiện đại
                paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
                paint.setShadowLayer(12, 0, 0, Color.parseColor("#00FFFF")); // Glow màu xanh neon
                float textWidthGO = paint.measureText(gameOverText);
                canvas.drawText(gameOverText, (screenWidth - textWidthGO) / 2, screenHeight * 0.4f, paint);


                // Nút Restart hiện đại
                String restartText = "RESTART";
                paint.setTextSize(70);
                paint.setColor(Color.WHITE);
                paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                float textWidthRestart = paint.measureText(restartText);
                float buttonX = (screenWidth - textWidthRestart) / 2;
                float buttonY = screenHeight * 0.6f;

                // Vẽ nút nền với gradient mềm mại
                Paint buttonPaint = new Paint();
                buttonPaint.setShader(new LinearGradient(buttonX - 50, buttonY - 80,
                        buttonX + textWidthRestart + 50, buttonY + 30,
                        Color.parseColor("#6200EE"), Color.parseColor("#BB86FC"), Shader.TileMode.CLAMP));
                canvas.drawRoundRect(buttonX - 50, buttonY - 80, buttonX + textWidthRestart + 50, buttonY + 30, 50, 50, buttonPaint);

                // Vẽ chữ "RESTART"
                paint.setColor(Color.WHITE);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(restartText, buttonX, buttonY, paint);
            }




        }




        private void spawnCar() {
            // Kiểm tra nếu số lượng xe dưới giới hạn (giới hạn 5 xe)
            if (cars.size() < 5) {
                new Handler().postDelayed(() -> {
                    int carType = new Random().nextInt(carBitmaps.length);
                    cars.add(new Car(randomLane(), carType, screenWidth));
                    spawnCar();
                }, 5000);
            }
        }

        private void restartGame() {
            // Reset lại tất cả các tham số trò chơi
            score = 0;
            gameOver = false;

            // Xóa tất cả các đối tượng game hiện tại
            cars.clear();
            potholes.clear();
            selectedPothole = null;

            // Khởi tạo lại các đối tượng
            // Tạo lại xe
            spawnCar();

            // Tạo lại pothole
            spawnPothole();

            // Khởi tạo lại repairman
            repairman = new Repairman(randomX(), screenHeight - 700, 10, activityFrames, 100);
            repairman.setCurrentActivity("cheer", activityFrames); // Khởi động với hoạt động "cheer"

            // Đảm bảo reset background hoặc các đối tượng khác nếu cần thiết
            // (Nếu bạn có bất kỳ thay đổi nào liên quan đến background hoặc các đối tượng, bạn có thể gọi lại các phương thức tương ứng ở đây)

            // Cập nhật lại trạng thái của game, cho phép tiếp tục chơi
            gameOver = false;
            isPlaying = true;
        }


        @Override
        public void run() {
            long lastTime = System.nanoTime();
            long targetTime = 1000000000 / 60; // 60 FPS

            while (isPlaying) {
                if (!holder.getSurface().isValid()) continue;

                long now = System.nanoTime();
                long elapsedTime = now - lastTime;

                // Nếu đã đủ thời gian, tiếp tục xử lý
                if (elapsedTime >= targetTime) {
                    lastTime = now;

                    // Cập nhật và vẽ trò chơi trực tiếp trong vòng lặp
                    Canvas canvas = holder.lockCanvas();
                    if (canvas != null) {
                        updateGame();  // Cập nhật trạng thái trò chơi
                        drawGame(canvas);  // Vẽ lại những thay đổi trên màn hình
                        holder.unlockCanvasAndPost(canvas);
                    }
                }

                // Chờ một khoảng thời gian để giảm tải CPU
                // Điều chỉnh độ trễ giúp giảm việc lãng phí tài nguyên CPU
                try {
                    long sleepTime = (targetTime - (System.nanoTime() - lastTime)) / 1000000; // Tính toán thời gian ngủ cần thiết
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);  // Chờ một thời gian để giúp hạn chế việc sử dụng CPU quá mức
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        public void pause() {
            isPlaying = false;
            try {
                gameThread.join();  // Đảm bảo gameThread đã dừng lại khi pause
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void resume() {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        private void spawnRepairman() {
            new Handler().postDelayed(() -> {
                Map<String, Bitmap[]> activityFrames = new HashMap<>();
                activityFrames.put("cheer", loadFrames("character_male_adventurer_cheer", 4));
                activityFrames.put("walk", loadFrames("character_male_adventurer_walk", 8));
                activityFrames.put("run", loadFrames("character_male_adventurer_run", 3));
                activityFrames.put("repair", loadFrames("character_male_adventurer_attack", 6));

                repairman = new Repairman(randomX(), screenHeight - 700, 10, activityFrames, 100);
                repairman.setCurrentActivity("cheer", activityFrames);

                spawnRepairman();
            }, 7000);
        }


        private int randomLane() {
            return screenHeight - 200; // Single lane
        }

        private int randomX() {
            return new Random().nextInt(screenWidth - 200) + 100;
        }

        private int randomSize() {
            return new Random().nextInt(3) + 1;
        }


        public class Repairman {
            private float x, y; // Vị trí của Repairman
            private float speed; // Tốc độ di chuyển
            private Bitmap[] frames; // Frame hiện tại của hoạt động
            private int frameIndex = 0; // Chỉ số frame hiện tại
            private long lastFrameTime = 0; // Thời gian cuối cùng frame thay đổi
            private int frameDuration; // Thời gian hiển thị mỗi frame (ms)

            private Map<String, Integer> activities; // Hoạt động và số lượng frame
            private String currentActivity; // Hoạt động hiện tại
            private Map<String, Bitmap[]> activityFrames; // Các frame cho từng hoạt động

            public Repairman(float startX, float startY, float speed, Map<String, Bitmap[]> activityFrames, int frameDuration) {
                this.x = startX;
                this.y = startY;
                this.speed = speed;
                this.frameDuration = frameDuration;
                this.activities = new HashMap<>();

                // Lưu trữ frame theo từng hoạt động
                activities = new HashMap<>();
                for (String activity : activityFrames.keySet()) {
                    activities.put(activity, activityFrames.get(activity).length);
                }

            }

            // Phương thức để thay đổi hoạt động
            public void setCurrentActivity(String activity, Map<String, Bitmap[]> activityFrames) {
                if (activities.containsKey(activity)) {
                    currentActivity = activity;
                    frames = activityFrames.get(activity);
                    frameIndex = 0; // Reset frame
                }
            }

            public boolean hasReached(float targetX, float targetY) {
                return x == targetX && y == targetY;
            }

            // Phương thức di chuyển đến vị trí
            public void moveTo(float targetX, float targetY) {
                if (x < targetX) {
                    x += speed;
                    if (x > targetX) x = targetX;
                } else if (x > targetX) {
                    x -= speed;
                    if (x < targetX) x = targetX;
                }

                if (y < targetY) {
                    y += speed;
                    if (y > targetY) y = targetY;
                } else if (y > targetY) {
                    y -= speed;
                    if (y < targetY) y = targetY;
                }
            }

            // Phương thức vẽ nhân vật lên Canvas
            public void draw(Canvas canvas) {
                // Chuyển frame dựa vào thời gian
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFrameTime >= frameDuration) {
                    frameIndex = (frameIndex + 1) % frames.length; // Chuyển sang frame tiếp theo
                    lastFrameTime = currentTime;
                }

                // Vẽ frame hiện tại
                canvas.drawBitmap(frames[frameIndex], x, y, null);
            }


            public Map<String, Bitmap[]> getActivityFrames() {
                return activityFrames;
            }



            // Getter vị trí
            public float getX() {
                return x;
            }

            public float getY() {
                return y;
            }
        }


        public class Pothole {
        public float x, y, size;
        public boolean isFixed;
        public boolean isBeingRepaired;
        private int repairProgress;

        public Pothole(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.isFixed = false;
            this.isBeingRepaired = false;
            this.repairProgress = 0;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void repair() {
            repairProgress += 5;
            if (repairProgress >= size * 100) {
                isFixed = true;
                isBeingRepaired = false;
            }
        }

        public boolean contains(float touchX, float touchY) {
            return touchX >= x && touchX <= x + size * 50 &&
                    touchY >= y && touchY <= y + size * 50;
        }

            public void draw(Canvas canvas, Paint paint) {
                if (!isFixed) {
                    // Tải hình ảnh từ drawable
                    Bitmap potholeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pothole);

                    // Tạo Matrix để thay đổi kích thước và dẹp hình ảnh
                    Matrix matrix = new Matrix();
                    float scaleX = 0.1f; // Tỷ lệ giảm theo chiều ngang (dẹp)
                    float scaleY = 0.1f; // Tỷ lệ giảm theo chiều dọc (nhỏ lại)
                    matrix.postScale(scaleX, scaleY); // Áp dụng tỷ lệ cho hình ảnh

                    // Tạo Bitmap mới từ Matrix đã chỉnh sửa
                    Bitmap scaledBitmap = Bitmap.createBitmap(potholeBitmap, 0, 0, potholeBitmap.getWidth(), potholeBitmap.getHeight(), matrix, true);

                    // Vẽ hình ảnh lên Canvas (vị trí tại (x, y))
                    canvas.drawBitmap(scaledBitmap, x - scaledBitmap.getWidth() / 2, y - scaledBitmap.getHeight() / 2, paint);
                }
            }


        }

    public class Car {
        public int x, lane, speed, type;

        public Car(int lane, int type, int screenWidth) {
            this.lane = lane;
            this.type = type;
            this.speed = 3;
            this.x = screenWidth;
        }

        public void move() {
            x -= speed;
        }

        public boolean hasCollided(Pothole pothole) {
            // Xác định kích thước của xe
            float carLeft = this.x;
            float carRight = this.x + 100;
            float carTop = this.lane - 20;
            float carBottom = this.lane + 20;

            // Xác định kích thước của ổ gà
            float potholeLeft = pothole.x - pothole.size / 2;
            float potholeRight = pothole.x + pothole.size / 2;
            float potholeTop = pothole.y - pothole.size / 2;
            float potholeBottom = pothole.y + pothole.size / 2;

            // Kiểm tra va chạm: nếu bounding box của xe và pothole giao nhau
            return carRight > potholeLeft && carLeft < potholeRight &&
                    carBottom > potholeTop && carTop < potholeBottom && !pothole.isFixed;
        }


        public void draw(Canvas canvas, Bitmap carBitmap, Paint paint) {
            // Sử dụng Bitmap để vẽ hình ô tô
            canvas.drawBitmap(carBitmap, x, lane - 20, null);

        }

    }




}
