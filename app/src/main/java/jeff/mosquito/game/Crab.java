package jeff.mosquito.game;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Random;

public class Crab {
    private ImageView imageView;
    private Animation shakeAnimation;
    private long crabLifespan;
    private boolean clicked;


    public Crab(Context context, ConstraintLayout layout) {
        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.crab);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        clicked = false;

        layoutParams.width = 200;
        layoutParams.height = 200;

        Random random = new Random();
        int leftMargin = random.nextInt(layout.getWidth() - layoutParams.width);
        int topMargin = random.nextInt(layout.getHeight() - layoutParams.height);

        imageView.setLayoutParams(layoutParams);
        imageView.setX(leftMargin);
        imageView.setY(topMargin);

        shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake_animation);

        int startColor = Color.WHITE;
        int endColor = Color.RED;

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimator.setDuration(5000);
        colorAnimator.setInterpolator(new LinearInterpolator());
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int animatedColor = (int) animator.getAnimatedValue();
                imageView.setColorFilter(animatedColor);
            }
        });

        // Start the color animation
        colorAnimator.start();

        layout.addView(imageView);
        crabLifespan = System.currentTimeMillis();

    }

    public View getView() {
        return imageView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageView.setOnClickListener(listener);
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isClicked() {
        return clicked;
    }
    public long getLifespan() {
        return System.currentTimeMillis() - crabLifespan;
    }
}