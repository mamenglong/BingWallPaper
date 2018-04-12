package com.meng.along.update;

import android.content.Context;

import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.meng.along.R;

import java.io.File;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/12.
 * 修改时间：2018/4/12 10:58
 */


public class MyCheckVersionLib {


    /*****
     * 上下文   更新信息，url，title，content
     * @param mContext
     * @param UpDateInfo
     */
    public static void checkUpdate(Context mContext, String[] UpDateInfo){
        StringBuffer stringBuffer=new StringBuffer();
        final NotificationBuilder notificationBuilder=NotificationBuilder.create();
        DownloadBuilder builder= AllenVersionChecker
                .getInstance()
                .downloadOnly(UIData.create().setDownloadUrl(UpDateInfo[0]).setTitle(UpDateInfo[1]).setContent(UpDateInfo[2]))
                .setSilentDownload(false)//静默下载默认false
                .setForceRedownload(true)//下载忽略本地缓存 默认false
                .setShowDownloadingDialog(true)//是否显示下载对话框 默认true
                .setShowNotification(true)//是否显示通知栏 默认true
                .setNotificationBuilder(
                        notificationBuilder
                                .setRingtone(true)
                                .setIcon(R.drawable.nav_download)
                                .setTicker("custom_ticker")
                                .setContentTitle("下载中")
                                .setContentText("请稍后"))//自定义通知栏
                .setShowDownloadFailDialog(true)// 默认true // 是否显示失败对话框
              //  .setDownloadAPKPath("")//自定义下载路径 默认 /storage/emulated/0/AllenVersionPath/
                .setApkDownloadListener(new APKDownloadListener() {
                    @Override
                    public void onDownloading(int progress) {
//                        ALog.e("111111111111111111111+  "+progress+"   "+notificationBuilder.getContentText());
//                        notificationBuilder.setContentText(String.valueOf(progress));
                    }

                    @Override
                    public void onDownloadSuccess(File file) {

                    }

                    @Override
                    public void onDownloadFail() {

                    }
                })//可以设置下载监听
                ;// AllenVersionChecker.getInstance().cancelAllMission(this);//取消任务
             builder. excuteMission(mContext);
    }
}
