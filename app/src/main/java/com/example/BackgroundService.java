package com.example;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.settings.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;


public class BackgroundService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private final static String TAG = BackgroundService.class.getSimpleName();

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();

    public static final String ACTION_LOCATION_BROADCAST = BackgroundService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private Context context = null;

    public int counter = 0;


    // 생성자1 : 반듯이 필요

    public BackgroundService() {

    }


    // 생성자2

    public BackgroundService(Context applicationContext) {

        super();

        context = applicationContext;

    }


    @Override

    public IBinder onBind(Intent intent) {

        return null;

    }


    @Override

    public void onCreate() {

        super.onCreate();

        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

        Log.d(TAG, "BackgroundService.onCreate");

    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        // 서비스가 호출될 때마다 실행

        Log.d(TAG, "BackgroundService.onStartCommand");

        //


        //LocationMonitoringService
        startTimer();
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        return START_STICKY;

    }

    @Override

    public void onTaskRemoved(Intent rootIntent) {

        Log.d(TAG, "BackgroundService.onTaskRemoved");

        //create an intent that you want to start again.

        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);

        super.onTaskRemoved(rootIntent);

    }


    @Override

    public void onDestroy() {

        super.onDestroy();

        // 서비스가 종료될 때 실행

        Log.d(TAG, "BackgroundService.onDestroy");

        //

        Intent broadcastIntent = new Intent("com.bluexmas.common.RestartService");

        sendBroadcast(broadcastIntent);

        stoptimertask();

    }


    private Timer timer;

    private TimerTask timerTask;

    long oldTime = 0;

    public void startTimer() {

        //set a new Timer

        timer = new Timer();


        //initialize the TimerTask's job

        initializeTimerTask();


        //schedule the timer, to wake up every 1 second

        timer.schedule(timerTask, 1000, 1000); //

    }


    /**
     * it sets the timer to print the counter every x seconds
     */

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                Log.i(TAG, "in timer ++++  " + (counter++));

            }

        };

    }


    /**
     * not needed
     */

    public void stoptimertask() {

        //stop the timer, if it's not already null

        if (timer != null) {

            timer.cancel();

            timer = null;

        }

    }




    //LocationMonitoringService

    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }


    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");

//        Intent intent = new Intent(this, ShowMsgActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);


        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }

    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

}
