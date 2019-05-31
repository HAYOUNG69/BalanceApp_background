package com.example;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.location.LocationManager;
import android.os.Looper;
import com.example.GoogleMapAPI.GoogleMapActivity;


public class BootReceiver extends BroadcastReceiver {

    private final static String TAG = BootReceiver.class.getSimpleName();

//    public static final String TAG = "LOCATION STATE";
    private static String mLastState;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    // BroadcastReceiver를 상속하여 처리 해줍니다.

    @Override

    public void onReceive(final Context context, Intent intent) {

        Log.d(TAG, "onReceive()"); /** * http://mmarvick.github.io/blog/blog/lollipop-multiple-broadcastreceiver-call-state/ * 2번 호출되는 문제 해결 */
        String state = intent.getStringExtra(LocationManager.GPS_PROVIDER);

        intent = new Intent(context, GoogleMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //        if (state.equals(mLastState)) {
//            return;
//        } else {
//            mLastState = state;
//        }
//        if (LocationManager.KEY_LOCATION_CHANGED.equals(state)) {
//            String incomingNumber = intent.getStringExtra(LocationManager.GPS_PROVIDER);
//            final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);
//            Intent serviceIntent = new Intent(context, ShowMsgActivity.class);
//            serviceIntent.putExtra(ShowMsgActivityer);
//            context.startService(serviceIntent);
//        }

        // TODO Auto-generated method stub

        // 전달 받은 Broadcast의 값을 가져오기

        // androidmanifest.xml에 정의한 인텐트 필터를 받아 올 수 있습니다.

        String action = intent.getAction();

        // 전달된 값이 '부팅완료' 인 경우에만 동작 하도록 조건문을 설정 해줍니다.

        if(action == null) {
            action = "empty string";
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {

            // TODO

            // 부팅 이후 처리해야 코드 작성

            // Ex.서비스 호출, 특정 액티비티 호출등등

            Log.d(TAG, "action = " + action);


            //

            new Handler().postDelayed(new Runnable() {

                // 3초 후에 실행

                @Override
                public void run() {

                    //Toast.makeText(context, "-- BootReceiver.onReceive", Toast.LENGTH_LONG).show();


                    // BackgroundService

                    Intent serviceLauncher = new Intent(context, BackgroundService.class);
                   // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if (Build.VERSION.SDK_INT >= 0) {

                        context.startService(serviceLauncher);

                    } else {

                        context.startService(serviceLauncher);

                    }

                }

            }, 3000);

        }

    }


    public static boolean isServiceRunning(Context context, Class serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG, "ServiceRunning? = " + true);
                return true;

            }

        }

        Log.i(TAG, "ServiceRunning? = " + false);

        return false;

    }

}

