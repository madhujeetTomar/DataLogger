package com.embitel.datalogger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.embitel.datalogger.base.AppContainer;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;


/**
 * The type App.
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static App mInstance;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    public AppContainer appContainer;
 //  private RefWatcher refWatcher;
    private String TAG = "App";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
      //  MultiDex.install(this);
    }

    public static Context getContext(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContainer=new AppContainer();
        registerActivityLifecycleCallbacks(this);
        mInstance = this;

        BleManager.getInstance().init(mInstance);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(SharedPreferencesManager.getBleRetryValue(), 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized App getInstance() {
        return mInstance;
    }

    /**
     * Gets firebase analytics.
     *
     * @return the firebase analytics
     */


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
       // networkCheck();
    }

    @Override
    public void onActivityStarted(Activity activity) {


    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        try {
            isActivityChangingConfigurations = activity.isChangingConfigurations();
            if (--activityReferences == 0 && !isActivityChangingConfigurations) {

               /* if(!(activity instanceof SplashActivity) && !(activity instanceof CheckoutActivity) && !(activity instanceof LoginActivity))
                    Toast.makeText(activity.getBaseContext(),"Background",Toast.LENGTH_SHORT).show();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }



}
