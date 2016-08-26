package com.example.achuan.coolweatherpractice.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.coolweatherpractice.R;
import com.example.achuan.coolweatherpractice.db.CoolWeatherDB;
import com.example.achuan.coolweatherpractice.model.City;
import com.example.achuan.coolweatherpractice.model.County;
import com.example.achuan.coolweatherpractice.model.Province;
import com.example.achuan.coolweatherpractice.util.HttpCallbackListener;
import com.example.achuan.coolweatherpractice.util.HttpUtil;
import com.example.achuan.coolweatherpractice.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achuan on 16-8-24.
 */
public class ChooseAreaActivity extends AppCompatActivity{
    //定义省\市\县的标号
    public static final int LEVEL_PROVINCE=0,LEVEL_CITY=1,LEVEL_COUNTY=2;
    //显示控件相关
    private ProgressDialog mProgressDialog;
    private TextView titleText;
    private ListView mListView;
    //数据相关
    private ArrayAdapter<String> mAdapter;
    private CoolWeatherDB mCoolWeatherDB;
    private List<String> dataList=new ArrayList<String>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取跳转前的那个活动传递过来的数据,这里传递过来一个标志位,代表从天气界面跳转过来的
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
        //先拿到本地存储文件操作实例
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        //如果city_selected为true说明当前已经选择过城市了
        //已经选择了城市并且不是从WeatherActivity跳转过来的,才可以直接跳转到天气信息显示的活动
        if(preferences.getBoolean("city_selected",false)&&!isFromWeatherActivity)
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.choose_area);
        //加载控件
        titleText= (TextView) findViewById(R.id.title_text);
        mListView= (ListView) findViewById(R.id.list_view);
        //设置列表显示适配器
        mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);//为列表添加适配器
        /**通过静态加载方法创建,保证只创建一个封装类实例**/
        mCoolWeatherDB=CoolWeatherDB.getInstance(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 if(currentLevel==LEVEL_PROVINCE)
                 {
                     selectedProvince=provinceList.get(i);
                     //查询下一级别：市
                     queryCities();
                 }else if(currentLevel==LEVEL_CITY){
                     selectedCity=cityList.get(i);
                     //查询下一级别：县
                     queryCounties();
                 }
                else if(currentLevel==LEVEL_COUNTY)
                 {
                     String countyCode=countyList.get(i).getCountyCode();//拿到点击的县对应的县级代号
                     Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                     intent.putExtra("county_code",countyCode);//将县级代号传递到天气信息显示的那个活动去
                     startActivity(intent);//启动跳转
                     finish();//结束当前活动
                 }
            }
        });
        //默认开启活动时先加载所有省份的消息
        queryProvinces();
    }
    /**
    * 查询全国所有的省,优先从数据库查询,如果没有查询到再去服务器上查询
    * */
    private void queryProvinces(){
        provinceList=mCoolWeatherDB.loadProvinces();//从数据库中读取信息
        //如果身份列表中已经有信息了,就从数据库中加载,否则就去服务器上加载
        if(provinceList.size()>0)
        {
            dataList.clear();//将显示列表中的集合清空
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();//刷新显示
            mListView.setSelection(0);//将列表显示定位到第一数据的位置
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            //刚安装软件时从服务器读取数据,进行解析后将其存储到本地数据库中,以后就不需要再去服务器访问了
            //从服务器中查询（仅执行一次）
            queryFromServer(null, "province");
        }
    }
    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList = mCoolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }
    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        countyList = mCoolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }
    /**
     * 根据代号和类型从服务器来查询省/市/县
     * */
    private void queryFromServer(final String code,final String type) {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            //根据代号查询省中所有城市的信息或者市中所有县的信息
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }
        else//如果没有代号,那就查询所有省份的信息
        {
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();//查询的时候显示加载进度
        //启动网络请求,获得返回的数据
        HttpUtil.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;//先假定未解析,解析成功后会变成ture
                if("province".equals(type))
                {
                    result= Utility.handleProvincesResponse(mCoolWeatherDB,response);
                }
                else if("city".equals(type))
                {
                    result=Utility.handleCitiesResponse(mCoolWeatherDB,response,selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result=Utility.handleCountiesResponse(mCoolWeatherDB,response,selectedCity.getId());
                }
                //解析成功后,所有的数据就存储到了数据库中,接下来就要去数据中查询了
                if(result)
                {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭加载显示
                            if("province".equals(type))
                            {
                                queryProvinces();//查询后,更新列表显示
                            }
                            else if("city".equals(type))
                            {
                                queryCities();
                            }
                            else if("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();//关闭加载显示
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
    }
    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);//设置进度框为不能取消
        }
        mProgressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
