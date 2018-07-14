package com.mapframe.drawutil;


import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

    /*可以新增功能*/
//添加信息窗-----弹窗覆盖物
//点标记动画，利用图片切换
//加载Marker时增加动画
//绘制瓦片
//绘制openGL 3D绘制功能


/**
 * 画图类
 * @author wzh  
 * @date 2018/6/8 16:51  
 */  
public class DrawGraph {
    public BaiduMap mBaiduMap;
    public List<OverlayOptions> options = new ArrayList<OverlayOptions>();
    DrawGraph(BaiduMap mBaiduMap){
        this.mBaiduMap = mBaiduMap;
    }

    /**
     * 绘制点
     * @author wzh
     * @date 2018/6/8 16:52  
     * @param   
     * @return   
     */  
    public void drawPoint(LatLng point1, int iconMark){
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconMark);
        OverlayOptions option =  new MarkerOptions().position(point1).icon(bitmap)
                .zIndex(9)
                .draggable(true);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    /**
     * 绘制多个点
     * //清除地图上所有覆盖物，无法分成批删除
     * @author wzh
     * @date 2018/6/8 17:10
     * @param
     * @return
     */
    public void drawPoints(List<LatLng> points, int iconMark){
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(iconMark);
        for (LatLng point : points){
            OverlayOptions option =  new MarkerOptions()
                    .position(point)
                    .icon(bitmap)
                    .zIndex(9)
                    .draggable(true);
            options.add(option);
        }
        //在地图上批量添加
        mBaiduMap.addOverlays(options);
    }
    /**
     * 清除所有覆盖物
     * @author wzh
     * @date 2018/6/8 17:15
     * @param
     * @return
     */
    public void clearPoints(){
        mBaiduMap.clear();
    }

    /**
     * 直线
     * @author wzh
     * @date 2018/6/11 10:50
     * @param points:点 color:线颜色 isDottedLine:is虚线 width:线宽
     * @return null
     */
    public void drawLine(List<LatLng> points, int color,Boolean isDottedLine, int width){
        //绘制折线
        Polyline mPolyline;
        OverlayOptions ooPolyline = new PolylineOptions().width(width)
                .color(color).points(points);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        if (isDottedLine){
            mPolyline.setDottedLine(true);
        }
    }

    /**
     * 弧线
     * @author wzh
     * @date 2018/6/11 10:52
     * @param
     * @return
     */
    public void drawArc(LatLng point1, LatLng point2, LatLng point3, int color, int width){
        OverlayOptions ooArc = new ArcOptions().color(color).width(width)
        //设置颜色和透明度，均使用16进制显示，0xAARRGGBB，如 0xAA00FF00 其中AA是透明度，00FF00为颜色
                .points(point1, point2, point3);
        mBaiduMap.addOverlay(ooArc);
    }

    /**
     * 圆
     * @author wzh
     * @date 2018/6/11 10:59
     * @param
     * @return
     */
    public void drawCircle(LatLng point, int fillClolr, int width){
        //设置颜色和透明度，均使用16进制显示，0xAARRGGBB，如 0xAA00FF00 其中AA是透明度，000000为颜色
        //通过stroke属性即可设置线的颜色及粗细，new Stroke(5, 0xAA000000) 5为线宽，0xAA000000 为颜色
        OverlayOptions ooCircle = new CircleOptions().fillColor(fillClolr)
                .center(point).stroke(new Stroke(width, 0xAA000000)).radius(1400);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 绘制多边形
     * @author wzh
     * @date 2018/6/12 14:09
     * @param
     * @return
     */
    public void drawPolygon(List<LatLng> points, int color, int width){
        //构建用户绘制多边形的Option对象
        OverlayOptions polygonOption = new PolygonOptions()
                .points(points)
                .stroke(new Stroke(width, color))
                .fillColor(0xAAFFFF00);

        //在地图上添加多边形Option，用于显示
        mBaiduMap.addOverlay(polygonOption);
    }

    /**
     * 文字
     * @author wzh
     * @date 2018/6/12 14:09
     * @param
     * @return
     */
    public void drawText(LatLng point, String text, int size, int color){
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption = new TextOptions()
                .bgColor(0x00FFFF00)
                .fontSize(size)
                .fontColor(color)
                .text("百度地图SDK")
                .rotate(0)
                .position(point);
        //在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);
    }


}
