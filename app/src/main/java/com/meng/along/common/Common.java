package com.meng.along.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.meng.along.ImageActivity;
import com.meng.along.ImageInfo;
import com.meng.along.ImageText;
import com.meng.along.MyApplication;
import com.meng.along.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Long on 2018/3/23.
 */

public class Common {


    /**
     * 判断支付宝客户端是否已安装，建议调用转账前检查
     * @return 支付宝客户端是否已安装
     */
    public static   boolean hasInstalledAlipayClient() {
        String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
        PackageManager pm = MyApplication.getContext().getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ALIPAY_PACKAGE_NAME, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 支付宝转账
     * @param activity
     * **/
    public static void openALiPay(Activity activity){
         String url1="intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fa6x076306bxhk8outhwdr67%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
        //String url1=activity.getResources().getString(R.string.alipay);
        Intent intent = null;
        Toast.makeText(MyApplication.getContext(),"感谢您的捐赠！٩(๑❛ᴗ❛๑)۶",Toast.LENGTH_SHORT).show();
        if(hasInstalledAlipayClient()){
            try {
                intent = Intent.parseUri(url1 ,Intent.URI_INTENT_SCHEME );
                activity.startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(MyApplication.getContext(),"出错啦",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MyApplication.getContext(),"您未安装支付宝哦！(>ω･* )ﾉ",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 版本信息提示
     * @param mContext
     * **/
    public static void showNoticeDialog(final Context mContext) {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_about_infor, null);
        Button button=view.findViewById(R.id.check_update);
        TextView tips=view.findViewById(R.id.tips);
        tips.setText("测试测试手册！ヾ(=･ω･=)oヾ(=･ω･=)o");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"已是最新！(*/ω＼*)",Toast.LENGTH_SHORT).show();
                //虽然这里的参数是AlertDialog.Builder(Context context)但我们不能使用getApplicationContext()获得的Context,而必须使用Activity.this,因为只有一个Activity才能添加一个窗体。
            }
        });
        dialog.setView(view,0,0,0,0);// 设置边距为0,保证在2.x的版本上运行没问题

        dialog.show();
    }

    /***
     * qq联系我
     * @param activity
     * **/
    public static void contactMe(Activity activity){
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + activity.getResources().getString(R.string.qq_number);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    /**
     * 通过url获取json字符串
     * @param urlString
     */
    public static String getJsonString(String urlString) {


        BufferedReader bufferedReader;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(urlString);//json地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");//使用get方法接收
            InputStream inputStream = connection.getInputStream();//得到一个输入流
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTf-8"));
            String sread = null;
            while ((sread = bufferedReader.readLine()) != null) {
                stringBuffer.append(sread);
                // stringBuffer.append("\r\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (stringBuffer.length() == 0)
            ;
         //   Toast.makeText(new MainActivity(), "网络连接失败！请检查网络设置或稍后再试！", Toast.LENGTH_SHORT).show();

        return stringBuffer.toString();
    }
    // 使用JSONObject

    /**
     * 获取照片信息
     * @param jsonData
     */
    public static ArrayList getImageInfors(String jsonData) {

        ArrayList arrayList = new ArrayList();
        try {
            //字符转换json
            JSONObject jo = new JSONObject(jsonData);
            //获取第一个json数组
            JSONArray jsonArray = jo.getJSONArray("images");
            for (int i = 0; i < jsonArray.length(); i++) {
                ImageInfo imageInfo = new ImageInfo();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                imageInfo.setCopyright(jsonObject.getString("copyright"));
                imageInfo.setEnddate(jsonObject.getString("enddate"));
                imageInfo.setStartdate(jsonObject.getString("startdate"));
                imageInfo.setUrl("http://cn.bing.com" + jsonObject.getString("url"));
                imageInfo.setFilename("Bing" + imageInfo.getStartdate().toString() + ".jpg");
                arrayList.add(imageInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * 下载图片
     */
    public static int downloadImage(ImageInfo imageInfo) {
        //  Environment.getDataDirectory();// /data
        // Environment.getRootDirectory();// /system
        // Environment.getExternalStorageDirectory();// /storage/emulated/0
        //  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);// /storage/emulated/0/DCIM
        int result=0;
        String phonePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "BingPic";
        File file = new File(phonePath);
        if (!file.exists())
            file.mkdirs();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String date=sdf.format(new java.util.Date());
        String imageName =imageInfo.getFilename();//"Bing"+ date+".jpg" ;
        String filepath = phonePath + File.separator + imageName;
        if (new File(filepath).exists())
            return 1;
        URL httpUrl = null;
        try {
            httpUrl = new URL(imageInfo.getUrl());
            DataInputStream dataInputStream = new DataInputStream(httpUrl.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(phonePath, File.separator + imageName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            result=2;
            e.printStackTrace();
        } catch (IOException ie) {
            result=2;
            ie.printStackTrace();
        }
        return result;
    }
    //动态获取内存存储权限
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission

        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    activity.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
}
