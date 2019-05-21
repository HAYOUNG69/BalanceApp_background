package com.example.programmingknowledge.mybalance_v11;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.content.DialogInterface;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.LinearLayout;
        import android.widget.TextView;


public class GoalBalanceListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_goal_balance_list, container, false);

        //db읽기
        DBHelper helper = new DBHelper(container.getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select sleep, work, study, exercise, leisure, other, week from tb_goalbalance", null);

        //DB에 데이터가 있으면
        if(cursor.getColumnCount()>0){
            //카드뷰 생성
            LinearLayout layout = (LinearLayout)root.findViewById(R.id.linear);
            while(cursor.moveToNext()) {
               CardView card = new CardView(container.getContext());
                  card.setMinimumHeight(200);
                  card.setMinimumWidth(1050);
                  card.setContentPadding(15, 15, 15, 15);
                  card.setId(cursor.getColumnIndex("id"));

                    TextView tv = new TextView(container.getContext());
                   tv.setText(cursor.getString(6));
                 card.addView(tv);

                 //카드뷰 겹치지 않게.
                /*LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                cardLayoutParams.addRule(LinearLayout.BELOW, R.id.cardView01);
                card.setLayoutParams(cardLayoutParams);*/


                  layout.addView(card);

                //아랫줄 한줄 저거뭐지
                CardView.MarginLayoutParams margin = new CardView.MarginLayoutParams(card.getLayoutParams());
                margin.setMargins(30,30,30,0);
                CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);


                card.setLayoutParams(new LinearLayout.LayoutParams(params));
                card.setLayoutParams(new LinearLayout.LayoutParams(margin));

            }

            /*TextView tv2 = new TextView(container.getContext());
            tv2.setPadding(15, 15, 15, 15);
            cursor.moveToFirst();
            tv2.setText(cursor.getString(6));
            layout.addView(tv2);*/
        }


        //목표 밸런스 설정창으로 이동(카드뷰 눌렀을때)
        CardView cv = (CardView) root.findViewById(R.id.cardView01);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SettingGoalBalanceFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace( R.id.fragment_container, fragment );
                fragmentTransaction.commit();
            }
        });

        //목표 밸런스 설정창으로 이동(액션버튼 눌렀을때)
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.AddGoalBalance);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Fragment fragment = new SettingGoalBalanceFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace( R.id.fragment_container, fragment );
                fragmentTransaction.commit();
            }
        });

        //카드뷰 오래 누르면 카드뷰 삭제
        CardView cardview = root.findViewById(R.id.cardView01);
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(container.getContext());
        cardview.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(final View view) {
                alBuilder.setMessage("삭제하시겠습니까?");

                // "예" 버튼을 누르면 실행되는 리스너
                alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //LinearLayout relative = (LinearLayout) root.findViewById(R.id.relative);
                        //relative.removeView(view);
                        //db삭제 구현하기(아래)

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });
                alBuilder.setTitle("목표 밸런스 삭제");
                alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.

                return false;
            }

        });

        // Inflate the layout for this fragment
        return root;
    }
}