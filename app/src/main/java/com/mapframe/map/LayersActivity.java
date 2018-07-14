package com.mapframe.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.baidu.mapapi.map.BaiduMap;
import com.mapframe.R;

import java.util.Map;

/**
 * 演示地图图层显示的控制方法
 */
public class LayersActivity extends Activity {

    private int MAP_TYPE=0;
    private boolean Traffic = false;
    private boolean BaiduHeatMap = false;
    /**
     * MapView 是地图主控件
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layers);
    }

    /**
     * 设置底图显示模式
     *
     * @param view
     */
    public void setMapMode(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.normal:
                if (checked) {
                    MAP_TYPE = BaiduMap.MAP_TYPE_NORMAL;
                    //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.statellite:
                if (checked) {
                    MAP_TYPE= BaiduMap.MAP_TYPE_SATELLITE;
                    //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置是否显示交通图
     *
     * @param view
     */
    public void setTraffic(View view) {
        if(((CheckBox) view).isChecked()){
            Traffic = true;
        }
        //mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
    }

    /**
     * 设置是否显示百度热力图
     *
     * @param view
     */
    public void setBaiduHeatMap(View view) {
        if(((CheckBox) view).isChecked()){
            BaiduHeatMap = true;
        }
        //mBaiduMap.setBaiduHeatMapEnabled(((CheckBox) view).isChecked());
    }

    public void save(View view){
        Intent intent = new Intent(LayersActivity.this, BaseMapActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("layerType", MAP_TYPE);
        bundle.putBoolean("Traffic", Traffic);
        bundle.putBoolean("BaiduHeatMap", BaiduHeatMap);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

}
