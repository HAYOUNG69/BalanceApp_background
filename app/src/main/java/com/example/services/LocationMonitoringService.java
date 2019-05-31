package com.example.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.GoogleMapAPI.GoogleMapActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.example.settings.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;


/**
 * Created by devdeeds.com on 27-09-2017.
 */

public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();


    LatLng currentPosition;
    LatLng previousPosition = null;   //추가

    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String ACTION_LOCATION_MAP = LocationMonitoringService.class.getName() + "LocationMAP";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
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


//    //to get the location change
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d(TAG, "Location changed");
////        Intent intent = new Intent(this, ShowMsgActivity.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        this.startActivity(intent);
//        if (location != null) {
//            Log.d(TAG, "== location != null");
//
//            //Send result to activities
//            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//        }
//    }




    @Override
    public void onLocationChanged(Location location) {  ////5분에 한번씩 호출
        double distance;
        double distanceMeter;
        double curlat, curlong, prelat, prelong;   //거리 오차를 줄이기 위한 변수

        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d(TAG, "onLocationChanged : ");

//        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

//        System.out.println("현재 위치: " + markerSnippet);
        //현재 위치에 마커 생성하고 이동
//        setCurrentLocation(location);   //마커 생성
        //mCurrentLocatiion = location;

        if (previousPosition == null) {  //최초 실행했을 때 (앱을 아예 처음 켰을 때??)  //이 때 가까운 place들이 뜸
//            showPlaceInformation(currentPosition);     //자동으로 place 검색
            previousPosition = currentPosition;
        } else {  //previousPosition이 null이 아니면 실행(이전 위치가 존재, 최초 실행이 아닐 때) (previousPosition != null)
            /*curlat = Math.round(currentPosition.latitude*100000)/100000.0;    //소수 5째자리 까지(오차를 줄이기 위해)
            curlong = Math.round(currentPosition.longitude*100000)/100000.0;  //소수 5째자리 까지(오차를 줄이기 위해)
            prelat = Math.round(previousPosition.latitude*100000)/100000.0;  //소수 5째자리 까지(오차를 줄이기 위해)
            prelong = Math.round(previousPosition.latitude*100000)/100000.0;  //소수 5째자리 까지(오차를 줄이기 위해)*/

            distance = SphericalUtil.computeDistanceBetween(currentPosition, previousPosition);  //이전 거리와 현재 거리 비교 (일단 10m로)
            //distanceMeter = distance(curlat, curlong, prelat, prelong);
            System.out.println("이전 위치가 존재하는 상태입니다.: " + previousPosition.latitude + " " + previousPosition.longitude + " distance: " + distance); //최초 실행때는 실행되면 안됨, 이전 위치 존재x

            if (distance >= 10) {      //추가추가    //원래 50
                System.out.println("타임라인에 입력된 장소로부터 30m를 벗어남");
                Toast toast = Toast.makeText(this, "30m를 벗어남!", Toast.LENGTH_SHORT);  //50m를 벗어났다는 알림이 핸드폰에 뜸
                toast.show();
                System.out.println("distance : " + distance);
                sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                sendMessageTOMyA();
                /*
                 *
                 * 이 때 팝업이 떠야 함
                 *
                 * 이 때 제일 가까운 장소가 타임라인에 넘어가야 함
                 * */
//                showPlaceInformation(currentPosition);     //자동으로 place 검색
//                if (sort_mlist != null) {   //검색이 성공하면
//                    setDBTimeLine();    //자동으로 db에 넣어주고 가장 가까운 장소를 타임라인으로 넘김
//                } else {
//                    Toast toast2 = Toast.makeText(this, "장소검색에 실패하였습니다.", Toast.LENGTH_SHORT);  //50m를 벗어났다는 알림이 핸드폰에 뜸
//                    toast2.show();
//                }
                previousPosition = currentPosition; //거리 범위를 넘었으니깐 현재 포지션은 다음 setCurrentLocation가 실행될 때 이전 포지션이 된다.
            } else {
                System.out.println("비슷한 위치");
            }
        }
    }


    public String getCurrentAddress(LatLng latlng) {  //현재 주소   ///1분에 6번씩 호출된다.
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {  //주소가 발견되지 않으면
            //Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();  //이게 꼭 필요할까?
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

//    public void setCurrentLocation(Location location) {  //현재 위치 가져오기
////        mMoveMapByUser = false;
////
////        if (currentMarker != null)
////            currentMarker.remove();  //현재 마커가 null값이 아니면 지우기 (새로 가져오기 위해) (이 명령문 지우면 마커 그림이 계속 쌓임)
//
//        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());  //현재 경도와 위도 가져오기
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLatLng);
////        markerOptions.title(markerTitle);
////        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//
//        currentMarker = mGoogleMap.addMarker(markerOptions);  //현재 위치로 바뀐 마커 붙이기
//
//        if (mMoveMapByAPI) {
//            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
//                    + location.getLatitude() + " " + location.getLongitude());
//            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15); //이건 뭐지?
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
//            mGoogleMap.moveCamera(cameraUpdate);
//        }
//    }

//    public void showPlaceInformation(LatLng location) {   //////////!!!!!!!
//        mGoogleMap.clear();//지도 클리어
//
//        if (previous_marker != null)   //마커가 존재하면
//            previous_marker.clear(); //지역정보 마커 클리어
//
//        new NRPlaces.Builder()
//                .listener(GoogleMapActivity.this)
//                .key("AIzaSyBzMQMBkCT4TIyu5zpqVDxWUu9yAvlJE-k")
//                .latlng(location.latitude, location.longitude)  //현재 위치
//                .radius(30) //30 미터 내에서 검색
//                //.type(PlaceType.BUS_STATION)  //모든 타입을 검색하면 시청이 검색 됨..흐규흐규...
//                .build()
//                .execute();
//    }












    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendMessageTOMyA(){
        Log.d(TAG, "Sending info...A");

        Intent intent = new Intent(ACTION_LOCATION_MAP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }


}