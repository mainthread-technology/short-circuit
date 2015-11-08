package technology.mainthread.shortcircuit.views;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import technology.mainthread.shortcircuit.R;

public class BatteryView extends FrameLayout {

    SpringSystem springSystem;
    Spring spring;

    public BatteryView(Context context) {
        super(context);
        init();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        View view = inflate(getContext(), R.layout.view_battery, this);

        final View green = view.findViewById(R.id.green);

        springSystem = SpringSystem.create();


        spring = springSystem.createSpring();

        spring.setSpringConfig(SpringConfig.fromBouncinessAndSpeed(80, 1));

        final float density = getResources().getDisplayMetrics().density;

        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float height = density * 330f;
                double current = height - (spring.getCurrentValue() * height);
                green.setTranslationY((float) current);

                double velocity = spring.getVelocity();
                if (spring.getCurrentValue() < spring.getStartValue() && velocity < 0) {
                    velocity = Math.abs(velocity);
                    if (velocity > 1) {
                        velocity = 1;
                    }
                    green.setBackgroundColor((int) new ArgbEvaluator().evaluate((float) (velocity), Color.GREEN, Color.RED));
                } else {
                    green.setBackgroundColor(Color.GREEN);
                }
            }
        });
    }

    public void setCurrentBattery(float percent) {
        spring.setEndValue(percent);
    }
}
