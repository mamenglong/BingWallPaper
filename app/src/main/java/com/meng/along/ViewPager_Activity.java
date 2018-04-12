package com.meng.along;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.meng.along.common.Common;
import com.meng.along.common.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ViewPager_Activity extends AppCompatActivity {
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private List<View> viewPages = new ArrayList<>();
    private List<ImageText> listPath=new ArrayList<>();
    //包裹点点的LinearLayout
    private ViewGroup group;
    private ImageView imageView;
    //定义一个ImageVIew数组，来存放生成的小园点
    private ImageView[] imageViews;
    private TextView index;
    private  int position=0;
    private String choice;
    private  ImageButton backButton, clickButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewpager_imageshow);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        position=bundle.getInt("position",0);
        choice=bundle.getString("choice");

        listPath=(List<ImageText>)bundle.getSerializable("list");
        initView();
        initViewPages(listPath.size());
        initPointer();
        initEvent();
        //设选中
        viewPager.setCurrentItem(position);
    }
    //为控件绑定事件,绑定适配器
    private void initEvent() {
        adapter = new PagerAdapter() {
            //获取当前界面个数
            @Override
            public int getCount() {
                return viewPages.size();
            }
            //判断是否由对象生成页面
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView(viewPages.get(position));
                // 这样写在删除页面的时候会报下标越界异常，原因也很容易发现，当前页面的view项已经被我们从viewList中移除了，但是destroyItem方法中仍然在访问它
                container.removeView((View)object);
            }
            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewPages.get(position);
                container.addView(view);
                return view;
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }
    //初始化ViewPager
    private void initViewPages(int size) {
        viewPages.clear();
        /**
         * 对于这几个想要动态载入的page页面，使用LayoutInflater.inflate()来找到其布局文件，并实例化为View对象
         */
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i=0;i<size;i++){
            View activity_image = inflater.inflate(R.layout.layout_viewpager_image, null);
            final PhotoView photoView;
            photoView=activity_image.findViewById(R.id.image);
            // 启用图片缩放功能
            photoView.enable();
            // 获取/设置 动画持续时间
//            photoView.setAnimaDuring(20);
            // int d = photoView.getAnimaDuring();
            // 获取/设置 最大缩放倍数
            photoView.setMaxScale(2);
            // float maxScale = photoView.getMaxScale();
            bingGlide(listPath.get(i).getImageUrl(),photoView);
            //添加到集合中
            viewPages.add(activity_image);
        }
        index.setText(viewPager.getCurrentItem()+1+"/"+listPath.size());
    }
    //绑定控件
    private void initView() {
        index=findViewById(R.id.index);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        group = (ViewGroup) findViewById(R.id.viewGroup);
        backButton= findViewById(R.id.back);
        clickButton=findViewById(R.id.click);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(choice.equals("gallery")){
            clickButton.setImageResource(R.drawable.ic_delete);
            clickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new  AlertDialog.Builder(ViewPager_Activity.this)
                            .setTitle("删除提示")
                            .setMessage("是否删除"+listPath.get(position).getFileName()+"?")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File file=new File(listPath.get(position).getImageUrl());
                                    if(file.delete()) {
                                        listPath.remove(position);
                                        // int position = viewPager.getCurrentItem();//获取当前页面位置
                                        viewPages.remove(position);//删除一项数据源中的数据
                                        adapter.notifyDataSetChanged();//通知UI更新
                                        if (position<=0&&listPath.size()==0){
                                            //右往左靠
                                            finish();
                                            return;
                                        }
                                        if(position>(listPath.size()-1))
                                            position--;
                                        initPointer();
                                        viewPager.setCurrentItem(position);
                                        index.setText(viewPager.getCurrentItem()+1+"/"+listPath.size());
                                        return;
                                    }
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

                }
            });
        }
        else
        {
            clickButton.setImageResource(R.drawable.nav_download);
            clickButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(ViewPager_Activity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){ //表示未授权时
                        //进行授权
                        ActivityCompat.requestPermissions(ViewPager_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        //调用x下载的方法
                        download();
                    }
                }
            });

        }
    }
    //初始化下面的小圆点的方法
    private void initPointer() {
        group.removeAllViews();
        //有多少个界面就new多长的数组
        imageViews=null;
        imageViews = new ImageView[viewPages.size()];
        for (int i = 0; i < imageViews.length; i++) {
            imageView = new ImageView(this);
            //设置控件的宽高
            imageView.setLayoutParams(new ViewGroup.LayoutParams(25, 25));
            //设置控件的padding属性
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;
            //初始化第一个page页面的图片的原点为选中状态
            if (i == position) {
                //表示当前图片
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                /**
                 * 在java代码中动态生成ImageView的时候
                 * 要设置其BackgroundResource属性才有效
                 * 设置ImageResource属性无效
                 */
            } else {
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            group.addView(imageViews[i]);
        }
    }
    //绑定imageview控件
    public void bingGlide(String url ,PhotoView photoView){
        int tag=new Random(System.currentTimeMillis()).nextInt( 4)+1;
        int pl= MyApplication.getResId("loading"+tag,R.drawable.class);
        if(pl==1)
            pl=R.drawable.loading1;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用掉Glide的缓存功能
                //  .error(R.drawable.error)
                // .skipMemoryCache(true)//禁用掉Glide的内存缓存功能
                .placeholder(pl)
                ;//占位
        Glide.with(this)
                .load(url)
                .transition(withCrossFade())//带淡入淡出效果
                .apply(options)
                .into(photoView);
    }

    public  void download(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewPager_Activity.this,"开始下载第"+(position+1)+"个...！",Toast.LENGTH_SHORT).show();
                    }
                });
                final int code= Common.downloadImage(listPath.get(position));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(code==0)
                            Toast.makeText(ViewPager_Activity.this,"第"+(position+1)+"个下载完成！",Toast.LENGTH_SHORT).show();
                        else if (code==1)
                            Toast.makeText(ViewPager_Activity.this,"文件已经存在啦！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ViewPager_Activity.this,"第"+(position+1)+"个出错啦，稍后再试一次吧！",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).start();
    }
    @Override
    public void finish() {
        if(choice.equals("gallery")){
            setResult(0);//gallery
        }
        else
            setResult(1);
        super.finish();//需要放在最后
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //同意权限申请
                    download();
                }else { //拒绝权限申请
                    Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
//ViewPager的onPageChangeListener监听事件，当ViewPager的page页发生变化的时候调用

    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        //页面滑动完成后执行
        @Override
        public void onPageSelected(int positio) {
            position=positio;
            index.setText(viewPager.getCurrentItem()+1+"/"+listPath.size());
            //判断当前是在那个page，就把对应下标的ImageView原点设置为选中状态的图片
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
                if (position != i) {
                    imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
        //监听页面的状态，0--静止 1--滑动  2--滑动完成
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}