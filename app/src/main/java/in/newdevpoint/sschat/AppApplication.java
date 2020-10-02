package in.newdevpoint.sschat;

import android.app.Application;
import android.content.Context;

public class AppApplication extends Application {

    public static Context applicationContext;

    public void onCreate() {
        super.onCreate();
        AppApplication.applicationContext = getApplicationContext();
    }


}