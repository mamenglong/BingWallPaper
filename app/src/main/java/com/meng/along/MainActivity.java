package com.meng.along;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.meng.along.common.Common;
import com.meng.along.adapter.CardImageAdapter;
import com.meng.along.adapter.CardImageAdapterG;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;//侧滑根标签
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private RecyclerView recyclerView;//列表控件
    GridLayoutManager gridLayoutManager;
    private CardImageAdapter adapter;
    private CardImageAdapterG adapterG;
  //  private List<ImageText> imageTextList=new ArrayList<>();
    private Set<ImageText> imageTextList=new HashSet<>();
    private Set<ImageText> imageTextListG=new HashSet<>();
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
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);//侧栏
        setNavigationItemSelected(navView);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        runUpdate();

    }
//    public void createTem(){
//    imageTextList.clear();
//    for (int i = 0; i < new Random().nextInt(20); i++) {
//        Random random = new Random();
//        int index = random.nextInt(imageTexts.length);
//        imageTextList.add(imageTexts[index]);
//    }
//}


    /**
     * 加载menu
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
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
            case R.id.backup:
                Toast.makeText(this,"backup",Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.delete:
                Toast.makeText(this,"delete",Toast.LENGTH_SHORT)
                        .show();
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
/******************************重写函数分界线*********************************************/
    /**设置NavigationItem的点击事件
     * */
    private int navItemId;
    public void setNavigationItemSelected(NavigationView navView){
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

    /****
     * 更新图片
     */
    public void runUpdate(){
        Toast.makeText(MainActivity.this,"正在努力加载中！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                .show();
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

                        gridLayoutManager.setSpanCount(1);
                      //  ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(true); //取消RecyclerView的动画效果
//                        adapter.notifyItemRangeChanged(0,imageTextList.size());
                        adapter=new CardImageAdapter( new ArrayList(imageTextList));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
        recyclerView.removeAllViews();
        Toast.makeText(MainActivity.this,"正在刷新！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                .show();
        //  Environment.getDataDirectory();// /data
        // Environment.getRootDirectory();// /system
        // Environment.getExternalStorageDirectory();// /storage/emulated/0
        //  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);// /storage/emulated/0/DCIM
        new Thread(new Runnable() {
            @Override
            public void run() {
//                final List<ImageText> localList=new ArrayList();
                final Set<ImageText> localList=new HashSet<>();
                String localPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "BingPic";
                File file=new File(localPath);
//                localList.clear();
                for(int i=0;i<file.list().length;i++)
                {
                    ImageText imageText=new ImageText();
                    imageText.setFile(file.listFiles()[i]);
                    imageText.setImageUrl(file.listFiles()[i].getAbsolutePath());
                    imageText.setText("");
                    localList.add(imageText);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // imageTextList.clear();
                        //imageTextList=localList;
                        imageTextListG.addAll(localList);
                        gridLayoutManager.setSpanCount(2);

                        adapterG=new CardImageAdapterG(new ArrayList(imageTextListG));
//                          ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(true); //取消RecyclerView的动画效果
//                        adapter.notifyItemRangeChanged(0,imageTextList.size());
                        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
                        recyclerView.setAdapter(adapterG);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//不在刷新，取消动画
                        Toast.makeText(MainActivity.this,"刷新完成！٩(๑>◡<๑)۶ ",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        }).start();

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

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                createTem();//重新生成
//                                adapter.notifyDataSetChanged();//通知adapter数据改变
//                                swipeRefreshLayout.setRefreshing(false);//不在刷新，取消动画
//                                Toast.makeText(MainActivity.this,"刷新完成！",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }).start();
            }
        });
    }

    /****
     * 在handeler里初始化全局变量   ImageTextList
     */
    /****
     * ImageInfo转换ImageText
     */

    public void  initializeImageTextList(){
       // imageTextList.clear();
        for (ImageInfo i:targetImageInfo
             ) {
            ImageText imageText=new ImageText();
            imageText.setImageUrl(i.getUrl());
            imageText.setText(i.getCopyright());
            imageTextList.add(imageText);
        }
    }


}
