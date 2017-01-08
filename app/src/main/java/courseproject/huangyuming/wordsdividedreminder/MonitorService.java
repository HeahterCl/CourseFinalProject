package courseproject.huangyuming.wordsdividedreminder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by huangchenling on 2017/1/7.
 */

public class MonitorService extends Service {

    private SharedPreferences sharedPreferences;

    private LocationManager mLocationManager;
    private String mProvider;

    private final IBinder binder = new ClipBoardBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ClipBoardBinder extends Binder {
        MonitorService getService() {
            return MonitorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());

        // for clipboard
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Intent intent = new Intent();
                intent.setAction(getResources().getString(R.string.clipboardservice_action));
                intent.putExtra(getResources().getString(R.string.clipboard_value), clipboardManager.getText().toString());
                sendBroadcast(intent);
            }
        });

        // for location
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mProvider = LocationManager.NETWORK_PROVIDER;
        } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProvider = LocationManager.GPS_PROVIDER;
        }

        if (checkLocationPermission()) {
            mLocationManager.requestLocationUpdates(mProvider, 0, 0, mLocationListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean checkLocationPermission() {

         return !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
    }

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String strLatitude = sharedPreferences.getString("latitude", "");
            String strLongitude = sharedPreferences.getString("longitude", "");

            if (!strLatitude.equals("") && !strLongitude.equals("")) {
                double latitude = Double.valueOf(strLatitude);
                double longitude = Double.valueOf(strLongitude);

                if (DistanceUtil.getDistance(convertDesLatLng(location), new LatLng(latitude, longitude)) < 100) {

                    // 消耗之
                    Intent intent = new Intent(MonitorService.this, MapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MonitorService.this, 0, intent, 0);

                    Notification.Builder builder = new Notification.Builder(MonitorService.this);
                    builder.setContentText("快想想附近有什么事情要做！")
                            .setTicker("快想想附近有什么事情要做！")
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setSmallIcon(R.mipmap.logo)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

                    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    Notification n = builder.build();
                    nm.notify(0, n);

                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    long [] pattern = {1000, 500, 200, 400};   // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, -1);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("latitude", "");
                    editor.putString("longitude", "");
                    editor.commit();

                }
            }

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        private LatLng convertDesLatLng(Location location) {
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
            return converter.convert();
        }

    };

}
