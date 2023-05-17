package jeff.mosquito.game;

import android.os.CountDownTimer;

public class Timer {
    private long remainingTimeDisplay;
    private long remainingTimeMillis;
    private CountDownTimer countDownTimer;


    private TimerCallback callback;

    public Timer(long durationMillis, TimerCallback callback) {
        this.remainingTimeMillis = durationMillis;
        this.callback = callback;
    }

    public void start() {
        countDownTimer = new CountDownTimer(remainingTimeMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                callback.onTick(millisUntilFinished);
            }


            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                callback.onFinish();
            }
        };
        remainingTimeDisplay = remainingTimeMillis;
        countDownTimer.start();
    }

    public void stop() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void addTime(long milliseconds) {

        remainingTimeMillis += milliseconds;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(remainingTimeMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                callback.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                callback.onFinish();
            }
        };
        remainingTimeDisplay = remainingTimeMillis;
        countDownTimer.start();
    }

    public long getRemainingTimeDisplay() {
        return remainingTimeDisplay;
    }

    public interface TimerCallback {
        void onTick(long remainingTimeMillis);

        void onFinish();
    }
}
