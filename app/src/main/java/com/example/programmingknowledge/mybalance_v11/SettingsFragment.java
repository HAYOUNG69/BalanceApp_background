package com.example.programmingknowledge.mybalance_v11;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.GoogleMapAPI.GoogleMapActivity;


public class SettingsFragment extends Fragment {

    //popup
    Button btn_map;
    TextView text_map;

    Button btn_noti;



        @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            //네..드디어 됩니다 미친 이거하느라 몇시간썼냐
            View root = inflater.inflate(R.layout.fragment_settings, container, false);
            TextView tv = (TextView) root.findViewById(R.id.GoToGoalBalanceListTv);

            //popup
            btn_map = (Button)root.findViewById(R.id.btn_map);
            text_map = (TextView)root.findViewById(R.id.text_map);

            //noti
            btn_noti = (Button)root.findViewById(R.id.btn_noti);

//            btn_noti.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    notification();
//                }
//            });

            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(container.getContext(), GoogleMapActivity.class);

                    startActivityForResult(intent, 1);



//                    startActivity(new Intent(container.getContext(),GoogleMapActivity.class ));
//                    Dialog dialog = new Dialog(container.getContext());
//
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.activity_googlemap);
//                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//
//                    params.width=700;
//                    params.height=600;
//                    dialog.getWindow().setAttributes(params);
//                    dialog.show();

//                    AlertDialog.Builder ad = new AlertDialog.Builder(container.getContext());
//                    ad.setIcon(R.mipmap.ic_launcher);
//                    ad.setTitle("제목");
//                    ad.setMessage("하영이는 존예?");
//
//                    final EditText et = new EditText(container.getContext());
//                    ad.setView(et);
//                    ad.setPositiveButton("ㅇㅈ", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            String result = et.getText().toString();
//                            text_map.setText(result);
//                            dialog.dismiss();
//                        }
//                    });
//
//                    ad.setNegativeButton("ㅇㅇㅇㅇㅇㅇ[변화x", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    ad.show();
                }
            });


            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new GoalBalanceListFragment();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace( R.id.fragment_container, fragment );
                    fragmentTransaction.commit();
                }
            });

            TextView tv2 = (TextView)root.findViewById(R.id.GoToMap);
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), GoogleMapActivity.class);   //newactivity로 값 넘기기
                    startActivity(intent);
                }
            });   //google map activity 실행하기

            return root;
    }

//    private void notification() {
////
////        Resources res = getResources();
////
////        Intent notificationIntent = new Intent(getContext(), GoogleMapActivity.class);
////        notificationIntent.putExtra("notificationId", 9999); //전달할 값
////        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////
////        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),"default");
////
////        builder.setContentTitle("Notification")
////                .setContentText("이게 바로 노티다 캬햐ㅑ햐햐")
////                .setTicker("상태바 한줄 메시지")
////                .setSmallIcon(R.mipmap.ic_launcher)
////                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
////                .setContentIntent(contentIntent)
////                .setAutoCancel(true)
////                .setWhen(System.currentTimeMillis())
////                .setDefaults(Notification.DEFAULT_ALL);
////
////
////
////        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
////            builder.setCategory(Notification.CATEGORY_MESSAGE)
////                    .setPriority(Notification.PRIORITY_HIGH)
////                    .setVisibility(Notification.VISIBILITY_PUBLIC);
////        }
////
////        NotificationManager nm = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
////        nm.notify(1234, builder.build());
////    }


}
