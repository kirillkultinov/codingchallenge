package com.kirillkultinov.saucelabschallenge;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * SauceService class is an extension of the Service class used to logcat the information requested
 * in the coding assignment.
 * Extends Android's Service class
 * The base of the class derived from official Android's documentation @ https://developer.android.com/guide/components/services
 * service can be started by using the following command: adb shell am startservice com.kirillkultinov.saucelabschallenge/.SauceService
 */
public class SauceService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    /**
     * ServiceHandler class is used to perform a work on a thread from the main thread
     * without this class, the actions performed within the defined service will be a part of the main thread
     * which can cause slow down of the program
     */
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        /**
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            //use java reflection to obtain information about hidden classes and methods
            try {
                //get Class object
                Class serviceManagerClass = Class.forName("android.os.ServiceManager");
                //get all the methods of the class and list them to the logcat for investigation
                Method[] smMethods = serviceManagerClass.getDeclaredMethods();
                for(Method m : smMethods){
                    Log.w("ServiceManager method", "name is " + m.toGenericString());
                    //invoke the hidden listServices() method
                    if(m.toGenericString().contains("listServices")){
                        String[] results = (String[]) m.invoke("listServices", null);
                        //print all services
                        for(String result : results){
                            Log.w("Service found", "" + result);
                        }

                    }
                }


                //get battery stats
                try {
                    //get the getService method which is used for obtaining an IBinder interface of a given service
                    Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
                    //invoke the getService method with a paramater batterystats
                    Object result = getServiceMethod.invoke(serviceManagerClass, "batterystats");
                    Constructor constructor = result.getClass().getConstructor();
                    if(result != null){
                        Method[] methods = result.getClass().getDeclaredMethods();
                        for(Method m : methods){
                            Log.w("Battery method", "" + m);
                        }

                    }




                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                Log.w("SauceService", "thread handled message");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // create a Looper for thread Handler
        serviceLooper = thread.getLooper();
        //create a service handler in order avoid performance overhead by doing work separately from the main thread
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("SauceService", "starting service");

        //send a message to thread handler when registering a service
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);


        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
