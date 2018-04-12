package com.meng.along.tips;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/12.
 * 修改时间：2018/4/12 10:56
 */
public class Tips {
}
/****
 *
 *
 * 一些其他的function设置

 解释下，下面的builder叫DownloadBuilder

 DownloadBuilder builder=AllenVersionChecker
 .getInstance()
 .downloadOnly();


 or



 DownloadBuilder builder=AllenVersionChecker
 .getInstance()
 .requestVersion()
 .request()
 取消任务
 AllenVersionChecker.getInstance().cancelAllMission(this);

 静默下载
 builder.setSilentDownload(true); 默认false
 强制更新
 设置此listener即代表需要强制更新，会在用户想要取消下载的时候回调
 需要你自己关闭所有界面

 builder.setForceUpdateListener(() -> {
 forceUpdate();
 });

 下载忽略本地缓存
 如果本地有安装包缓存也会重新下载apk

 builder.setForceRedownload(true); 默认false
 是否显示下载对话框
 builder.setShowDownloadingDialog(false); 默认true
 是否显示通知栏
 builder.setShowNotification(false);  默认true
 自定义通知栏
 builder.setNotificationBuilder(
 NotificationBuilder.create()
 .setRingtone(true)
 .setIcon(R.mipmap.dialog4)
 .setTicker("custom_ticker")
 .setContentTitle("custom title")
 .setContentText(getString(R.string.custom_content_text))
 );
 是否显示失败对话框
 builder.setShowDownloadFailDialog(false); 默认true
 自定义下载路径
 builder.setDownloadAPKPath(address); 默认：/storage/emulated/0/AllenVersionPath/
 可以设置下载监听
 builder.setApkDownloadListener(new APKDownloadListener() {
@Override
public void onDownloading(int progress) {

}

@Override
public void onDownloadSuccess(File file) {

}

@Override
public void onDownloadFail() {

}
});
 自定义界面

 自定义界面使用回调方式，开发者需要返回自己定义的Dialog（父类android.app）

 所有自定义的界面必须使用listener里面的context实例化

 界面展示的数据通过UIData拿

 自定义显示更新界面
 设置CustomVersionDialogListener

 定义此界面必须有一个确定下载的按钮，按钮id必须为@id/versionchecklib_version_dialog_commit

 如果有取消按钮（没有忽略本条要求），则按钮id必须为@id/versionchecklib_version_dialog_cancel

 eg.

 builder.setCustomVersionDialogListener((context, versionBundle) -> {
 BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_dialog_one_layout);
 //versionBundle 就是UIData，之前开发者传入的，在这里可以拿出UI数据并展示
 TextView textView = baseDialog.findViewById(R.id.tv_msg);
 textView.setText(versionBundle.getContent());
 return baseDialog;
 });

 自定义下载中对话框界面
 设置CustomDownloadingDialogListener

 如果此界面要设计取消操作（没有忽略），请务必将id设置为@id/versionchecklib_loading_dialog_cancel
 builder.setCustomDownloadingDialogListener(new CustomDownloadingDialogListener() {
@Override
public Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle) {
BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_layout);
return baseDialog;
}
//下载中会不断回调updateUI方法
@Override
public void updateUI(Dialog dialog, int progress, UIData versionBundle) {
TextView tvProgress = dialog.findViewById(R.id.tv_progress);
ProgressBar progressBar = dialog.findViewById(R.id.pb);
progressBar.setProgress(progress);
tvProgress.setText(getString(R.string.versionchecklib_progress, progress));
}
});
 自定义下载失败对话框
 设置CustomDownloadFailedListener

 如果有重试按钮请将id设置为@id/versionchecklib_failed_dialog_retry

 如果有 确认/取消按钮请将id设置为@id/versionchecklib_failed_dialog_cancel

 builder.setCustomDownloadFailedListener((context, versionBundle) -> {
 BaseDialog baseDialog = new BaseDialog(context, R.style.BaseDialog, R.layout.custom_download_failed_dialog);
 return baseDialog;
 });


 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * **/