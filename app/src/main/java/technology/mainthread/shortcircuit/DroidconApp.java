package technology.mainthread.shortcircuit;

import android.app.Application;

import timber.log.Timber;

public class DroidconApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }

}
