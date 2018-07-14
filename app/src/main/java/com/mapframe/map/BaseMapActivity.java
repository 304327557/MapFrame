package com.mapframe.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.mapframe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.COMPASS;
import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING;
import static com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;


/**
 * 演示MapView的基本用法
 * @author wzh
 */
public class BaseMapActivity extends Activity implements SensorEventListener {

    @SuppressWarnings("unused")
    private static final String LTAG = BaseMapActivity.class.getSimpleName();
    public MapView mMapView;
    public BaiduMap mBaiduMap;
    FrameLayout layout;
    private boolean mEnableCustomStyle = true;
    private static final int OPEN_ID = 0;
    private static final int CLOSE_ID = 1;
    //用于设置个性化地图的样式文件
    // 精简为1套样式模板:
    // "custom_config_dark.json"
    private static String PATH = "custom_config_dark.json";
    private static int icon_themeId = 1;

    // 定位相关
    private SensorManager mSensorManager;
    public MyLocationListenner myListener = new MyLocationListenner();
    LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    // 是否首次定位
    boolean isFirstLoc = true;

    //常量定义
    private final int BAIDU_READ_PHONE_STATE = 0x100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        MapStatus.Builder builder = new MapStatus.Builder();
        // 默认 天安门
//        LatLng center = new LatLng(39.915071, 116.403907);
        // 默认 11级
        float zoom = 11.0f;

//        /* 该Intent是OfflineDemo中查看离线地图调起的 */
//        Intent intent = getIntent();
//        if (null != intent) {
//            mEnableCustomStyle = intent.getBooleanExtra("customStyle", true);
//            center = new LatLng(intent.getDoubleExtra("y", 39.915071),
//                    intent.getDoubleExtra("x", 116.403907));
//            zoom = intent.getFloatExtra("level", 11.0f);
//        }
//        builder.target(center).zoom(zoom);


        /**
         * MapView (TextureMapView)的
         * {@link MapView.setCustomMapStylePath(String customMapStylePath)}
         * 方法一定要在MapView(TextureMapView)创建之前调用。
         * 如果是setContentView方法通过布局加载MapView(TextureMapView), 那么一定要放置在
         * MapView.setCustomMapStylePath方法之后执行，否则个性化地图不会显示
         */
//        setMapCustomFile(this, PATH);

        mMapView = findViewById(R.id.bmapView);
        //获取BaiduMap对象
        mBaiduMap = mMapView.getMap();
        initView(this);
        setLocation();


     //   MapView.setMapCustomEnable(true);

        deleteBaiduIcon();
        //setLocation();
        //startLocation();
    }
    //动态打开权限
    private void startLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(BaseMapActivity.this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
        }else{
            setLocation();
            Toast.makeText(getApplicationContext(),"有权限",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     *标记拖拽事件
     * @author wzh
     * @date 2018/6/11 9:50
     * @param
     * @return
     */
    public void markListener(){
        //调用BaiduMap对象的setOnMarkerDragListener方法设置Marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
            }
            @Override
            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
            }
        });
    }


    // 初始化View
    private void initView(Context context) {
        if(Build.VERSION.SDK_INT >= 23) {
            //Android6.0以上，需要动态申请运行时权限
            //startLocation();
        }
        else{
            //Android6.0以下，直接进行地图定位操作
        }
        //比例尺展示
        mMapView. showScaleControl(true);
        //缩放等级设置
        mBaiduMap.setMaxAndMinZoomLevel( 0, 21);
        //缩放按钮
        mMapView. showZoomControls(true);
        //地图空间设置
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //指南针
        mUiSettings.setCompassEnabled(true);
        //地图平移
        mUiSettings. setScrollGesturesEnabled(true);
        //地图手势缩放
        mUiSettings. setZoomGesturesEnabled(true);
        //俯视地图
        mUiSettings. setOverlookingGesturesEnabled(true);
        //地图旋转
        mUiSettings .setRotateGesturesEnabled(true);
        //禁止一切手势
        mUiSettings .setAllGesturesEnabled(true);
    }

    // 根据实际情况显示百度图标按钮等
    public void deleteBaiduIcon()
    {
        View child = mMapView.getChildAt(1);
        if (child !=null) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    // 指南针开启
    public void openCompass(){
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
    }
    // 显示定位
    public void setLocation(){
        //获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                COMPASS, true, null));
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        //设置地图定位模式为跟随
        mCurrentMarker = null;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                NORMAL, true, mCurrentMarker));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                NORMAL, true, null));
    }

    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MapView.setCustomMapStylePath(moduleName + "/" + PATH);

    }

    /**
     * 设置个性化icon
     *
     * @param context
     * @param icon_themeId
     */
    private void setIconCustom(Context context, int icon_themeId){

        MapView.setIconCustom(icon_themeId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // activity 销毁时同时销毁地图控件
        MapView.setMapCustomEnable(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.offline_item:
                Intent intent = new Intent(BaseMapActivity.this, OfflineActivity.class);
                startActivity(intent);
                Toast.makeText(this,"离线地图",Toast.LENGTH_SHORT).show();
                break;
            case R.id.addmap_item:
                Intent intent1 = new Intent(BaseMapActivity.this, TileOverlayActivity.class);
                startActivity(intent1);
                Toast.makeText(this,"加载离线地图",Toast.LENGTH_SHORT).show();
                break;
            case R.id.maptype_item:
                Intent intent2 = new Intent(BaseMapActivity.this, LayersActivity.class);
                startActivityForResult(intent2, RESULT_FIRST_USER);
                Toast.makeText(this,"地图类型",Toast.LENGTH_SHORT).show();
                break;
//            case R.id.location_item:
//
//                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            int map_type = bundle.getInt("layerType");
            Boolean Traffic = bundle.getBoolean("Traffic");
            Boolean BaiduHeatMap = bundle.getBoolean("BaiduHeatMap");
            mBaiduMap.setMapType(map_type);
            if(Traffic){
                mBaiduMap.setTrafficEnabled(Traffic);
            }
            if(BaiduHeatMap){
                mBaiduMap.setBaiduHeatMapEnabled(BaiduHeatMap);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    setLocation();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
