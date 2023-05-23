package jeff.mosquito.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private int round;
    private int points;
    private int crabsLeft;
    private int roundCrabs;
    private Timer timer;
    private List<Crab> crabs = new ArrayList<>();
    private long lastSpawnTime;
    private long spawnInterval = 1000;
    private volatile boolean isRunning = true;

    private SharedPreferences sharedPreferences;
    private static final String HIGH_SCORE_KEY = "high_score";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        startGame();

        // Main game loop
        new Thread(() -> {
            while (isRunning) {
                if (crabsLeft <= 0) {
                    round++;
                    spawnInterval -= 200;
                    roundCrabs += 20;
                    crabsLeft = roundCrabs;
                }

                runOnUiThread(this::renderText);
                gameOver();

                try {
                    Thread.sleep(16);
                    spawnCrabs(1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void renderText() {
        TextView tvPoints = findViewById(R.id.points);
        tvPoints.setText(Integer.toString(points));

        TextView tvRound = findViewById(R.id.round);
        tvRound.setText("Round " + round);

        TextView tvCrabsLeft = findViewById(R.id.crabsleft);
        tvCrabsLeft.setText(Integer.toString(crabsLeft));

        removeExpiredCrabs();
    }

    private void removeExpiredCrabs() {
        ConstraintLayout layout = findViewById(R.id.crab_display);

        List<Crab> expiredCrabs = new ArrayList<>(); // Store expired crabs
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        for (Crab crab : crabs) {
            long lifespan = crab.getLifespan();
            if (lifespan >= 5000 && !crab.isClicked()) {
                expiredCrabs.add(crab);
                layout.removeView(crab.getView());
                timer.removeTime(10000);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        }

        crabs.removeAll(expiredCrabs); // Remove the expired crabs from the main list
    }

    private void spawnCrabs(int count) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= spawnInterval) {
            lastSpawnTime = currentTime;
            ConstraintLayout layout = findViewById(R.id.crab_display);

            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    for (int i = 0; i < count; i++) {
                        Crab crab = new Crab(GameActivity.this, layout);

                        // Add click listener to remove crab when clicked
                        crab.setOnClickListener(v -> {
                            if(!isRunning){
                                return;
                            }
                            layout.removeView(crab.getView());
                            points++;
                            crabsLeft--;
                            timer.addTime(200);

                            crab.setClicked(true); // Mark the crab as clicked

                            // Perform screen shake animation
                            Animation shakeAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.shake_animation);
                            layout.startAnimation(shakeAnimation);
                        });

                        crabs.add(crab);
                    }
                }
            });
        }
    }

    public void gameOver() {
        TextView tvGameOver = findViewById(R.id.gameOverText);
        TextView tvHighScore = findViewById(R.id.highScore);

        if (timer.getRemainingTimeMillis() <= 0) {
            isRunning = false;
            tvGameOver.setVisibility(View.VISIBLE);
            tvHighScore.setVisibility(View.VISIBLE);

            int currentHighScore = sharedPreferences.getInt(HIGH_SCORE_KEY, 0);

            if (points > currentHighScore) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(HIGH_SCORE_KEY, points);
                editor.apply();
                currentHighScore = points;
            }

            tvHighScore.setText(String.valueOf(currentHighScore));

            tvGameOver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restart the game when the game over screen is clicked
                    startGame();
                    tvGameOver.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            tvGameOver.setVisibility(View.INVISIBLE);
            tvHighScore.setVisibility(View.INVISIBLE);
        }
    }

    public void startGame() {
        crabsLeft = roundCrabs;
        round = 0;
        points = 0;
        timer = new Timer(60000, new Timer.TimerCallback() {
            @Override
            public void onTick(long elapsedTimeMillis) {
                double seconds = elapsedTimeMillis / 1000.0;
                String formattedTime = String.format("%.1f", seconds);

                // Update the timer display
                TextView tvTimeLeft = findViewById(R.id.timer);
                tvTimeLeft.setText(formattedTime);

                ProgressBar pbTimeLeft = findViewById(R.id.timeLeft);
                pbTimeLeft.setProgressTintList(ColorStateList.valueOf(Color.RED));
                pbTimeLeft.setMax((int) timer.getRemainingTimeDisplay() / 1000);
                pbTimeLeft.setProgress((int) seconds);
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isRunning = false;
                        gameOver();
                    }
                });
            }
        });
        timer.start();
        spawnCrabs(5);
    }

    private void restartGame() {
        round = 0;
        points = 0;
        spawnInterval = 1000;
        roundCrabs = 20;
        crabsLeft = roundCrabs;
        crabs.clear();
        timer.start();
        isRunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false; // Stop the game loop
        if (timer != null) {
            timer.stop();
        }
    }
}