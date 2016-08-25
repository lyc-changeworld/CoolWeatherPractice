package com.example.achuan.coolweatherpractice.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by achuan on 16-8-24.
 * 功能：网络请求
 */
public class HttpUtil {
    /****发起网络请求的方法****/
    public static void sendRequestWithHttpURLConnection(final String address,final HttpCallbackListener listener)
    {
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection connection=null;
                try{
                    URL url=new URL(address);//新建一个网络地址
                    connection= (HttpURLConnection) url.openConnection();//打开网址链接
                    //设置HTTP请求
                    connection.setRequestMethod("GET");//配置请求方式为：获取数据
                    connection.setConnectTimeout(8000);//设置连接超时时间
                    connection.setReadTimeout(8000);//设置读取超时时间
                    InputStream inputStream=connection.getInputStream();//获取服务器返回的输入流
                    //对获取的输入流进行读取
                    BufferedReader reader=new BufferedReader(
                            new InputStreamReader(inputStream));
                    StringBuilder response=new StringBuilder();//创建一个字符数组来存储数据
                    String line;
                    while ((line=reader.readLine())!=null) {
                        response.append(line);//将数据逐个读取后添加到数组中
                    }
                    if(listener!=null)
                    {
                        //回调onFinish()方法
                        listener.onFinish(response.toString());//对网络请求获得的数据进行解析和存储、列表显示
                    }
                }catch (Exception e)
                {
                    if(listener!=null)
                    {
                        //回调onError()方法
                        listener.onError(e);
                    }
                }finally {
                    if(connection!=null)
                    {
                        connection.disconnect();//关闭HTTP连接
                    }
                }
            }
        }).start();
    }
}
