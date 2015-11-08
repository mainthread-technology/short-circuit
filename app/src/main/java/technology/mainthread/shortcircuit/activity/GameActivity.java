package technology.mainthread.shortcircuit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import technology.mainthread.shortcircuit.R;
import technology.mainthread.shortcircuit.views.BatteryView;

public class GameActivity extends Activity {

    BatteryView batteryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        batteryView = (BatteryView) findViewById(R.id.battery);

        batteryView.post(new Runnable() {
            @Override
            public void run() {
                batteryView.setCurrentBattery(0f);
            }
        });

        batteryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batteryView.setCurrentBattery(new Random().nextFloat());
            }
        });
    }
}
