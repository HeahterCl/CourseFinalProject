package courseproject.huangyuming.wordsdividedreminder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by huangyuming on 17-1-8.
 */

public class MapActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private Button confirm;

    private LatLng lastChosen;

    private MapView mMapView;
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;
    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;

    private String mProvider;

    private LatLng mDesLatLng;

    private boolean scrollTo = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent().getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this)
                    .setMessage("附近一定有什么忘了的事")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            vibrator.cancel();
                        }
                    });
            builder.create().show();
        }

        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        confirm = (Button)findViewById(R.id.confirm);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mProvider = LocationManager.NETWORK_PROVIDER;
        }
        else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProvider = LocationManager.GPS_PROVIDER;
        }

//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        mProvider = mLocationManager.getBestProvider(criteria, true);

//        Toast.makeText(MainActivity.this, mLocationManager.getLastKnownLocation(provider).getLongitude()+"", Toast.LENGTH_SHORT).show();


        mMapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomTo(19));

        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pointer), 100, 100, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMapView.getMap().setMyLocationEnabled(true);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, bitmapDescriptor);
        mMapView.getMap().setMyLocationConfigeration(configuration);

        if (checkLocationPermission() && mLocationManager.getLastKnownLocation(mProvider) != null) {
            LatLng desLatLng = convertDesLatLng(mLocationManager.getLastKnownLocation(mProvider));
            movePointerToDes(desLatLng);
            moveScreenToDes(desLatLng);
            mDesLatLng = desLatLng;
        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                scrollTo = b;

                if (b) {
                    if (checkLocationPermission() && mLocationManager.getLastKnownLocation(mProvider) != null) {
                        LatLng desLatLng = convertDesLatLng(mLocationManager.getLastKnownLocation(mProvider));
                        movePointerToDes(desLatLng);
                        moveScreenToDes(desLatLng);
                    }
                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lastChosen != null) {

                    double latitude = lastChosen.latitude;
                    double longitude = lastChosen.longitude;

                    SharedPreferences sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("latitude", latitude + "");
                    editor.putString("longitude", longitude + "");
                    editor.commit();

                    finish();
                }
                else {
                    Toast.makeText(MapActivity.this, "你还没有选定位置", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mMapView.getMap().setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (scrollTo) {
                            toggleButton.setChecked(false);
                            scrollTo = false;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        mMapView.getMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lastChosen = latLng;
                mMapView.getMap().clear();
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.pin_map_32);
                MarkerOptions options = new MarkerOptions().position(lastChosen).icon(bitmap);
                mMapView.getMap().addOverlay(options);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mLocationManager.requestLocationUpdates(mProvider, 0, 0, mLocationListener);
        }

        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();

        mSensorManager.unregisterListener(mSensorEventListener);
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

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float [] accValues;
        float [] magValues;

        float newRotationDegree = 0;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = sensorEvent.values;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magValues = sensorEvent.values;
                    break;
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                newRotationDegree = 0;
                if (accValues != null && magValues != null) {
                    float[] R = new float[9];
                    float[] values = new float[3];

                    SensorManager.getRotationMatrix(R, null, accValues, magValues);
                    SensorManager.getOrientation(R, values);

                    newRotationDegree = (float) Math.toDegrees(values[0]);

                    MyLocationData.Builder builder = new MyLocationData.Builder();
                    builder.direction(newRotationDegree);
                    builder.latitude(mDesLatLng.latitude);
                    builder.longitude(mDesLatLng.longitude);
                    mMapView.getMap().setMyLocationData(builder.build());

                }

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

//            if (checkLocationPermission() && mLocationManager.getLastKnownLocation(mProvider) != null) {
//                LatLng desLatLng = convertDesLatLng(mLocationManager.getLastKnownLocation(mProvider));
//                movePointerToDes(desLatLng);
//                mDesLatLng = desLatLng;
//
//                if (scrollTo) {
//                    moveScreenToDes(desLatLng);
//                }
//            }

        }
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private LatLng convertDesLatLng(Location location) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(location.getLatitude(), location.getLongitude()));
        return converter.convert();
    }

    private void movePointerToDes(LatLng desLatLng) {
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(desLatLng.latitude);
        builder.longitude(desLatLng.longitude);

        mMapView.getMap().setMyLocationData(builder.build());
    }

    private void moveScreenToDes(LatLng desLatLng) {
        MapStatus mapStatus = new MapStatus.Builder().target(desLatLng).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mMapView.getMap().setMapStatus(mapStatusUpdate);
    }

}
