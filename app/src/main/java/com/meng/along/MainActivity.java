package com.meng.along;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meng.along.adapter.CardImageAdapterG;
import com.meng.along.common.Common;
import com.meng.along.adapter.CardImageAdapter;
import com.meng.along.utils.NetUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;//侧滑根标签
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private RecyclerView recyclerView;//列表控件
    private NavigationView navView;
    private GridLayoutManager gridLayoutManager;
    private CardImageAdapter adapter;
    private CardImageAdapterG adapterG;
    private Set<ImageText> imageTextList=new HashSet<>();
    private Set<ImageText> imageTextListG=new HashSet<>();
    private List<ImageText> listAdapter=new ArrayList<>();
    private List<ImageInfo> targetImageInfo=new ArrayList();//转化json之后的url
    private String[] originUrl={"http://cn.bing.com","http://cn.bing.com/HPImageArchive.aspx?format=js&idx=-1&n=8"};
    //private TextView mail;
    // private NavigationView navView;//侧栏
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawerLayout);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        setSwipeRefresh(swipeRefreshLayout);
        navView = (NavigationView) findViewById(R.id.nav_view);//侧栏
        setNavigationItemSelected();
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            //显示导航
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置导航图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        }

        recyclerView=findViewById(R.id.recyclerView);
        gridLayoutManager =new GridLayoutManager(MainActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
//        ((DefaultItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        adapter=new CardImageAdapter( listAdapter);
        adapterG=new CardImageAdapterG(listAdapter);
        checkNetWork();//检查网络
        runUpdate();


    }

    /**
     * 加载menu
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.toolbar,menu);
        Log.e("menu", "onCreateOptionsMenu: 11111111111111111111"+menu.size());
        return true;
    }
    // invalidateOptionsMenu();调用以后调用onPrepareOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
         if(navItemId==R.id.nav_update)
             menu.findItem(R.id.menu_action).setIcon(R.drawable.nav_download);
         else if(navItemId==R.id.nav_gallery)
             menu.findItem(R.id.menu_action).setIcon(R.drawable.ic_delete);
        Log.e("menu", "onPrepareOptionsMenu: 11111111111111111111"+menu.size());
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 菜单按钮点击事件
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.menu_action:
                Toast.makeText(this,"delete",Toast.LENGTH_SHORT)
                        .show();
                setMenuActionBar();
                break;
            case R.id.setting:
                Toast.makeText(this,"setting",Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
        }
        return true;
    }
    /**
     * 下载全部或者删除全部
     * **/
    public void setMenuActionBar(){
        switch (navItemId){
            case R.id.nav_gallery://删除所有
                new  AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除提示")
                        .setMessage("是否删除所有图片?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(imageTextListG.size()==0){
                                    Toast.makeText(MainActivity.this,"没有图片可删除哦！",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    int i=1;
                                    for (ImageText it:imageTextListG
                                            ) {
                                        if(it.getImageUrl()==null||it.getImageUrl()=="")
                                        {
                                            Toast.makeText(MainActivity.this, "没有图片可删除哦",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            return;
                                        }
                                        File file=new File(it.getImageUrl());
                                        if(file.exists()){
                                            try{
                                                file.delete();
                                                i++;
                                            }
                                            catch (Exception E){
                                                E.toString();
                                                continue;
                                            }
                                        }
                                    }
                                    if(i==imageTextListG.size())
                                        Toast.makeText(MainActivity.this, "全部删除成功",Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this,i+"个删除成功"+(imageTextListG.size()-i)+"个删除失败！",Toast.LENGTH_SHORT).show();
                                    iniImageListG();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
            case R.id.nav_update://下载所有
                new  AlertDialog.Builder(MainActivity.this)
                        .setTitle("下载提示")
                        .setMessage("是否下载所有图片?")
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(imageTextList.size()==0){
                                    Toast.makeText(MainActivity.this,"没有图片可下载哦！",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                  final   int []i=new int[2];
                                    i[0]=1;
                                    for (final ImageText it:imageTextList
                                            ) {

                                        try{
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this,"开始下载第"+i[0]+"个...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    final int code= Common.downloadImage(it);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(code==0)
                                                                Toast.makeText(MainActivity.this,"第"+i[0]+"个下载完成！",Toast.LENGTH_SHORT).show();
                                                            else if (code==1)
                                                                Toast.makeText(MainActivity.this,"文件已经存在啦！",Toast.LENGTH_SHORT).show();
                                                            else
                                                                Toast.makeText(MainActivity.this,"第"+i[0]+"个出错啦，稍后再试一次吧！",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                        catch (Exception E){
                                            E.toString();
                                            continue;
                                        }
                                        i[0]++;
                                    }
                                    if(i[0]==imageTextList.size())
                                        Toast.makeText(MainActivity.this, "全部下载成功",Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(MainActivity.this,(i[0]-1)+"个下载成功"+(imageTextList.size()-i[0]+1)+"个下载失败！",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
        }
    }
    /**
     * 双击退出
     * */
    private  long exitTime=0;//全局计时
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Snackbar.make(drawerLayout, "再按一次退出程序！(๑ت๑)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
                //Toast.makeText(this,"再按一次退出程序！(๑ت๑)",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }
            else
            {
                Toast.makeText(this,"欢迎下次再来！(๑`･︶･´๑)",Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0://查看图片返回之后
                switch (resultCode){
                    case 0:
                        iniImageListG();
                        adapterG.notifyDataSetChanged();
                        // runGallery();
                        break;
                    case 1:
                        //  runUpdate();
                        break;
                }
                break;
            case 1:
                switch (resultCode){
                    case 0://网络打开以后设置
                        // NET_Connect[0]=true;
                        runUpdate();
                        break;
                    case 1:
                        runGallery();
                        break;
                }
                break;
        }

    }
    /******************************重写函数分界线*********************************************/
    /**设置NavigationItem的点击事件
     * */
    private int navItemId;
    public void setNavigationItemSelected(){
        navView.setCheckedItem(R.id.nav_update);
        navItemId=R.id.nav_update;
        //点击邮箱发邮件
        View headerView=navView.inflateHeaderView(R.layout.nav_header);
        LinearLayout linearLayout=headerView.findViewById(R.id.icon_mail);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_SENDTO);
                it.putExtra(Intent.EXTRA_EMAIL, getResources().getString(R.string.nav_header_email));
                //   it.setType("text/plain");
                startActivity(Intent.createChooser(it, "Choose Email Client"));
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_update:
                        navItemId=R.id.nav_update;
                        // setTargetImageInfo();
                        //new Thread(thread_getImageInfo).start();
                        runUpdate();
                        break;
                    case R.id.nav_gallery:
                        navItemId=R.id.nav_gallery;
                        Toast.makeText(MainActivity.this,"nav_gallery",Toast.LENGTH_SHORT)
                                .show();
                        runGallery();

                        break;
                    case R.id.nav_location:
                        navItemId=R.id.nav_location;
                        Toast.makeText(MainActivity.this,"nav_location",Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.nav_mail:
                        navItemId=R.id.nav_mail;
                        Toast.makeText(MainActivity.this,"nav_mail",Toast.LENGTH_SHORT)
                                .show();
//                        Intent intent=new Intent(MainActivity.this,ViewPager_Activity.class);
//                        startActivity(intent);
                        break;
                    case R.id.nav_task:
                        Toast.makeText(MainActivity.this,"nav_task",Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.nav_donate:
                        Common.openALiPay(MainActivity.this);
                        break;
                    case R.id.contactMe:
                        Common.contactMe(MainActivity.this);
                        break;
                    case R.id.about:
                        Common.showNoticeDialog(MainActivity.this);
                        //虽然这里的参数是AlertDialog.Builder(Context context)但我们不能使用getApplicationContext()获得的Context,而必须使用Activity.this,因为只有一个Activity才能添加一个窗体。
                        break;
                    default:
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }

    final boolean[] NET_Connect =new boolean[2];
    /**
     * 检查网络连接
     * **/
    public void checkNetWork(){
        NET_Connect[0]=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(!NetUtil.iSNetStateConnect(MainActivity.this)){
                        // NetUtil.checkNetwork(MainActivity.this);
                        if(NET_Connect[0]){
                            NET_Connect[0]=false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"网络已中断(╯︵╰)",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    else{
                        if(!NET_Connect[0]){
                            NET_Connect[0]=true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"网络已恢复~(￣▽￣)~*",Toast.LENGTH_SHORT).show();
                                    runUpdate();
                                }
                            });
                        }

                    }
                    try {
                        Thread.sleep((long)2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }//while
            }//run
        }).start();
    }
    /****
     * 更新图片
     */
    public void runUpdate(){

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        dialog.setTitle("加载中");
        dialog.show();
        recyclerView.removeAllViews();
        listAdapter.clear();
        navView.setCheckedItem(R.id.nav_update);
        invalidateOptionsMenu();
        if(!NET_Connect[0]){//检查线程只是改变这个值
            NetUtil.checkNetwork(this);
            return;
        }
//        Toast.makeText(MainActivity.this,"正在努力加载中！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
//                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonString=Common.getJsonString(originUrl[1]);
                final ArrayList arrayList=Common.getImageInfors(jsonString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        targetImageInfo.clear();
                        targetImageInfo=arrayList;
                        initializeImageTextList();
                        dialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        listAdapter.addAll(imageTextList);
                        gridLayoutManager.setSpanCount(1);
                        adapter.setItemClickListener(new CardImageAdapter.MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<ImageText> list=new ArrayList<>(imageTextList);
//                                ImageText imageText = (ImageText)list.get(position);
                                Intent intent = new Intent(MainActivity.this, ViewPager_Activity.class);
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("list",(Serializable)list);
                                bundle.putInt("position",position);
                                bundle.putString("choice","update");
                                intent.putExtra("bundle",bundle);
                                startActivityForResult(intent,0);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();
                        swipeRefreshLayout.setRefreshing(false);//不在刷新，取消动画
                        Toast.makeText(MainActivity.this,"加载完成啦！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                                .show();


                    }
                });
            }
        }).start();

    }

    /***
     * 图库
     */

    public void runGallery(){
        navView.setCheckedItem(R.id.nav_gallery);
        invalidateOptionsMenu();//动态设置图标
        Toast.makeText(MainActivity.this,"正在刷新！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                .show();
        //  Environment.getDataDirectory();// /data
        // Environment.getRootDirectory();// /system
        // Environment.getExternalStorageDirectory();// /storage/emulated/0
        //  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);// /storage/emulated/0/DCIM
        new Thread(new Runnable() {
            @Override
            public void run() {
                iniImageListG();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //adapterG=new CardImageAdapterG(new ArrayList(imageTextListG));

                        Toast.makeText(MainActivity.this,"刷新完成！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                                .show();
                        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
                        recyclerView.setAdapter(adapterG);
                        adapterG.notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();//加载动画
                        swipeRefreshLayout.setRefreshing(false);//不在刷新，取消动画
                    }
                });
            }
        }).start();

    }

    public void iniImageListG(){
        listAdapter.clear();
        String localPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "BingPic";
        File file=new File(localPath);
        imageTextListG.clear();
        if (!file.exists())
            file.mkdirs();
        if(file.listFiles()!=null&&file.listFiles().length!=0) {
            gridLayoutManager.setSpanCount(2);
            for (int i = 0; i < file.list().length; i++) {
                ImageText imageText = new ImageText();
                imageText.setFile(file.listFiles()[i]);
                imageText.setImageUrl(file.listFiles()[i].getAbsolutePath());
                imageText.setFileName(file.listFiles()[i].getName());
                imageTextListG.add(imageText);
            }
            adapterG.setItemClickListener(new CardImageAdapterG.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<ImageText> list = new ArrayList<>(imageTextListG);
//                                ImageText imageText = (ImageText)list.get(position);
//                                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    Intent intent = new Intent(MainActivity.this, ViewPager_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", (Serializable) list);
                    bundle.putInt("position", position);
                    bundle.putString("choice", "gallery");
                    intent.putExtra("bundle", bundle);
                    startActivityForResult(intent, 0);
                }
            });
        }
        //如果没有图片则显示这个提示
        else if(imageTextListG.size()==0) {
            imageTextListG.add(new ImageText("亲，你还没下载过呢，点我快去下载吧！",0));
            gridLayoutManager.setSpanCount(1);
            adapterG.setItemClickListener(new CardImageAdapterG.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    navItemId=R.id.nav_update;
                    runUpdate();
                }
            });
        }

        listAdapter.addAll(imageTextListG);
    }

    public void  initializeImageTextList(){
        // imageTextList.clear();
        for (ImageInfo i:targetImageInfo
                ) {
            ImageText imageText=new ImageText();
            imageText.setImageUrl(i.getUrl());
            imageText.setText(i.getCopyright());
            imageText.setFileName(i.getFilename());
            imageTextList.add(imageText);
        }
    }

    /**设置SwipeRefreshLayout刷新操作
     * */
    public void setSwipeRefresh(SwipeRefreshLayout refreshLayout){

        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (navItemId){
                    case R.id.nav_update:
                        runUpdate();
                        break;
                    case R.id.nav_gallery:
                        runGallery();
                        break;
                    default:
                }
            }
        });
    }

    /****
     * 在handeler里初始化全局变量   ImageTextList
     */
    /****
     * ImageInfo转换ImageText
     */




}
