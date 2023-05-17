package jeff.mosquito.game;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Random;

public class Crab {
    private ImageView imageView;
    private Animation shakeAnimation;

    public Crab(Context context, ConstraintLayout layout) {
        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.crab);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.width = 200;
        layoutParams.height = 200;

        Random random = new Random();
        int leftMargin = random.nextInt(layout.getWidth() - layoutParams.width);
        int topMargin = random.nextInt(layout.getHeight() - layoutParams.height);

        imageView.setLayoutParams(layoutParams);
        imageView.setX(leftMargin);
        imageView.setY(topMargin);

        shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake_animation);

        layout.addView(imageView);
    }

    public View getView() {
        return imageView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageView.setOnClickListener(listener);
    }
}