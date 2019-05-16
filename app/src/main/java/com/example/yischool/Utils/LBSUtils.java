package com.example.yischool.Utils;

import android.content.Context;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import cn.bmob.v3.datatype.BmobGeoPoint;

import static com.example.yischool.Utils.LBSUtils.LOCATE_HANDLER_WHAT;
import static com.baidu.location.BDLocation.TypeGpsLocation;
import static com.baidu.location.BDLocation.TypeNetWorkLocation;
import static com.baidu.location.BDLocation.TypeOffLineLocation;

/**
 * @author 张云天
 * date on 2019/5/15
 * describe: 百度地图帮助类
 */
public class LBSUtils {

    //Handler Message what
    public static final int LOCATE_HANDLER_WHAT = 1;
    public static final int ADDRESS_TO_POINT_HANDLER_WHAT = 2;

    /**
     * 开始定位
     * @param context
     * @param handler
     */
    public static LocationClient startLocate(Context context, Handler handler){

//        WeakReference weakReference = new WeakReference<>(context);
//        AppCompatActivity activity = (AppCompatActivity) weakReference.get();
//        if()

        //声明LocationClient类
        LocationClient mLocationClient =  new LocationClient(context);
        //注册监听函数
        MyLocationListener myListener = new MyLocationListener(handler);
        mLocationClient.registerLocationListener(myListener);

        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        /*
         * 可选，设置定位模式，默认高精度
         * LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；
         * LocationMode. Device_Sensors：仅使用设备；
         */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /*
         * 可选，设置返回经纬度坐标类型，默认GCJ02
         */
        option.setCoorType("bd09ll");
        /*
         * 可选，定位SDK内部是一个service，并放到了独立进程。
         * 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
         */
        option.setIgnoreKillProcess(false);
        /*
         * 可选，设置发起定位请求的间隔，int类型，单位ms
         * 如果设置为0，则代表单次定位，即仅定位一次，默认为0
         * 如果设置非0，需设置1000ms以上才有效
         */
        option.setScanSpan(0);
        /*
         * 可选，设置是否使用gps，默认false
         * 使用高精度和仅用设备两种定位模式的，参数必须设置为true
         */
        option.setOpenGps(true);
        /*
         * 可选，是否需要地址信息，默认为不需要，即参数为false
         */
        option.setIsNeedAddress(true);

        //将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(option);

        mLocationClient.start();

        return mLocationClient;
    }

    /**
     * 地址转经纬度
     * @param city 城市 例：北京
     * @param address 地址信息, 例: 北京上地十街10号
     */
    public static void addressToGeoPoint(String city, String address, final Handler handler){

        final String DEFAULT_ADDRESS = "市中心";//当没有address==null时使用

        //地理编码检索实例
        GeoCoder geoCoder = GeoCoder.newInstance();
        //地理编码检索监听器
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                Message message = new Message();
                message.what = ADDRESS_TO_POINT_HANDLER_WHAT;

                //完成时返回结果
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {

                    if (geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        message.obj = null;
                        handler.sendMessage(message);

                    } else {
                        double latitude = geoCodeResult.getLocation().latitude;
                        double longitude = geoCodeResult.getLocation().longitude;

                        BmobGeoPoint geoPoint = new BmobGeoPoint();
                        geoPoint.setLatitude(latitude);
                        geoPoint.setLongitude(longitude);

                        message.obj = geoPoint;
                        handler.sendMessage(message);
                    }
                }else {
                    //检索结果错误
                    message.obj = null;
                    handler.sendMessage(message);
                }
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) { }
        };

        geoCoder.setOnGetGeoCodeResultListener(listener);

        //设置GeoCodeOption，发起geoCode检索
        if(address == null){
            address = DEFAULT_ADDRESS;
        }
        geoCoder.geocode(new GeoCodeOption().city(city).address(address));
    }

}

/**
 * Abstract类型的监听接口BDAbstractLocationListener, 已实现异步
 */
class MyLocationListener extends BDAbstractLocationListener {

    private Handler handler;

    MyLocationListener(){}

    MyLocationListener(Handler handler){
        this.handler = handler;
    }
    @Override
    public void onReceiveLocation(BDLocation location){

        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        int errorCode = location.getLocType();
        Message message = new Message();
        message.what = LOCATE_HANDLER_WHAT;
        //判断定位结果
        if(errorCode == TypeGpsLocation || errorCode == TypeNetWorkLocation || errorCode == TypeOffLineLocation){
            //定位成功返回
            message.obj = location;

        }else{ //定位失败
            message.obj = null;
        }
        handler.sendMessage(message);
    }

}
