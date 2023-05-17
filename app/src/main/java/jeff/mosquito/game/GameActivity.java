package jeff.mosquito.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
    private int crabs_left;
    private int round_crabs;
    private Timer timer;
    private List<Crab> crabs = new ArrayList<>();
    

    private volatile boolean is_running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        startGame();

        // Main game loop
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (is_running) {
                    if(crabs_left <= 0){
                        round++;
                        round_crabs += 20;
                        crabs_left = round_crabs;
                    }

                    // Update game display
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            renderText();
                        }
                    });

                    try {
                        Thread.sleep(16); // Adjust this value to control the game's frame rate
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void renderText(){
        TextView tvPoints = findViewById(R.id.points);
        tvPoints.setText(Integer.toString(points));

        TextView tvRound = findViewById(R.id.round);
        tvRound.setText("Round " + round);

        TextView tvCrabsLeft = findViewById(R.id.crabsleft);
        tvCrabsLeft.setText(Integer.toString(crabs_left));
    }

    private void spawnCrabs(int count) {
        ConstraintLayout layout = findViewById(R.id.crab_display);

        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i < count; i++) {
                    Crab crab = new Crab(GameActivity.this, layout);

                    // Add click listener to remove crab when clicked
                    crab.setOnClickListener(v -> {
                        spawnCrabs(1);
                        layout.removeView(crab.getView());
                        points++;
                        crabs_left--;
                        timer.addTime(1000);

                        renderText();
                        // Perform screen shake animation
                        Animation shakeAnimation = AnimationUtils.loadAnimation(GameActivity.this, R.anim.shake_animation);
                        layout.startAnimation(shakeAnimation);
                    });

                    crabs.add(crab);
                }
            }
        });
    }

    public void startGame() {
        crabs_left = round_crabs;
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
                pbTimeLeft.setMax((int)timer.getRemainingTimeDisplay() / 1000);
                pbTimeLeft.setProgress((int)seconds);
            }

            @Override
            public void onFinish() {
                // Handle timer finish, e.g., end the game
            }
        });
        timer.start();
        spawnCrabs(5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_running = false; // Stop the game loop
        if (timer != null) {
            timer.stop();
        }
    }
}
