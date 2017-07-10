package ui.component.banking.separablecardview;

import android.app.Application;
import android.util.Log;

/**
 * Created by banking on 2017/7/3.
 */
public class MyApplication extends Application{




    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultUncaughtExceptionHandler();
    }

    private static void setDefaultUncaughtExceptionHandler() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    Log.e("Error", "Uncaught Exception detected in thread {}", e);
                }
            });
        } catch (SecurityException e) {
            Log.e("Error", "Uncaught Exception detected in thread {}", e);
        }
    }
}
