package com.meng.along;
/**暂时未用**/
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ImageActivity extends AppCompatActivity implements  android.view.GestureDetector.OnGestureListener{
    private ImageView imageView;
    private ImageButton   backButton, clickButton;
    private GestureDetector gestureDetector = null;
    private  int position=0;
    private TextView all,now;
    private List<ImageText> listPath=new ArrayList<>();
    private String choice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");

        position=bundle.getInt("position",0);

        gestureDetector = new GestureDetector(this); // 声明检测手势事件
        imageView = findViewById(R.id.image);
        backButton=findViewById(R.id.back);
        clickButton=findViewById(R.id.click);
        now=findViewById(R.id.nowPosition);
        all=findViewById(R.id.allNum);

        setChoice(bundle);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        all.setText(String.valueOf(listPath.size()));
        now.setText(String.valueOf(position+1));
        bingGlide(listPath.get(position).getImageUrl());
    }

    /*****
     *根据choice设置必要项
     * @param bundle
     */
    public void setChoice( Bundle bundle){
        choice=bundle.getString("choice");
        if(choice.equals("gallery")) {
            clickButton.setImageResource(R.drawable.ic_delete);
            listPath=(List<ImageText>)bundle.getSerializable("list");
            clickButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file=new File(listPath.get(position).getImageUrl());
                    if(file.delete()) {
                        listPath.remove(position);
                        if (position<=0&&listPath.size()==0){
                            //右往左靠
                            finish();
                            return;
                        }
                        if(position>(listPath.size()-1))
                            position--;
                        bingGlide(listPath.get(position).getImageUrl());
                        Toast.makeText(ImageActivity.this, "删除完成!", Toast.LENGTH_SHORT).show();
                        all.setText(String.valueOf(listPath.size()));
                        now.setText(String.valueOf(position+1));
                    }
                }
            });
        }
        else{
            clickButton.setImageResource(R.drawable.nav_download);
//            listInfo=(List<ImageInfo>)bundle.getSerializable("list");
//            for (ImageInfo i:listInfo
//                    ) {
//                ImageText imageText=new ImageText();
//                imageText.setImageUrl(i.getUrl());
//                imageText.setText(i.getFilename());
//                listPath.add(imageText);
//            }
            listPath=(List<ImageText>)bundle.getSerializable("list");
            clickButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(ImageActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){ //表示未授权时
                        //进行授权
                        ActivityCompat.requestPermissions(ImageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        //调用x下载的方法
                      download();
                    }
                }
            });

        }

    }
    public void download(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ImageActivity.this,"开始下载第"+(position+1)+"个...！",Toast.LENGTH_SHORT).show();
                    }
                });
                final int code=Common.downloadImage(listPath.get(position));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(code==0)
                            Toast.makeText(ImageActivity.this,"第"+(position+1)+"个下载完成！",Toast.LENGTH_SHORT).show();
                        else if (code==1)
                            Toast.makeText(ImageActivity.this,"文件已经存在啦！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ImageActivity.this,"第"+(position+1)+"个出错啦，稍后再试一次吧！",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).start();
    }
    /**
     * 权限申请返回结果
     * @param requestCode 请求码
     * @param permissions 权限数组
     * @param grantResults  申请结果数组，里面都是int类型的数
     */
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

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getX() - e1.getX() > 120) { // 从左向右滑动（左进右出）
            if ( (position-1)>=0) {
                position--;
            }
            else {
                Toast.makeText(this,"已是第一张啦！",Toast.LENGTH_SHORT).show();
                // position=2;//如果用户一直左滑position会一直减小，导致右滑出错
            }

        }
        else if (e2.getX() - e1.getX() < -120) { // 从右向左滑动（右进左出）
            if ((position+1) < listPath.size()) {
                position++;//先判断有没有超出数据下界，然后继续
            }
            else
                Toast.makeText(this,"已是最后一张啦！",Toast.LENGTH_SHORT).show();
        }
        if(choice.equals("gallery")){
            File file = new File(listPath.get(position).getImageUrl());
            if (file.exists()) {
                bingGlide(listPath.get(position).getImageUrl());
            }
        }
        else
        {
            bingGlide(listPath.get(position).getImageUrl());
        }
        all.setText(String.valueOf(listPath.size()));
        now.setText(String.valueOf(position+1));
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){


            case MotionEvent.ACTION_DOWN:
                // finish();
                break;
            case  MotionEvent.BUTTON_BACK:
                finish();
                break;
        }
        return gestureDetector.onTouchEvent(event); // 注册手势事件
//        return super.onTouchEvent(event);
    }

    public void bingGlide(String url ){
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
                .into(imageView);
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
}

/****

 使用的时候。从前一个Activity传递到详情Activity以下几个基本的信息：
 Intent intent = new Intent(MainActivity.this, SpaceImageDetailActivity.class);
 intent.putExtra("images", (ArrayList<String>) datas);//非必须
 intent.putExtra("position", position);
 int[] location = new int[2];
 imageView.getLocationOnScreen(location);
 intent.putExtra("locationX", location[0]);//必须
 intent.putExtra("locationY", location[1]);//必须

 intent.putExtra("width", imageView.getWidth());//必须
 intent.putExtra("height", imageView.getHeight());//必须
 startActivity(intent);
 overridePendingTransition(0, 0);

 在详情Activity接受到这些參数，并对SmoothImageView初始化位置信息，然后就能够进行变化了。

 mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
 mPosition = getIntent().getIntExtra("position", 0);
 mLocationX = getIntent().getIntExtra("locationX", 0);
 mLocationY = getIntent().getIntExtra("locationY", 0);
 mWidth = getIntent().getIntExtra("width", 0);
 mHeight = getIntent().getIntExtra("height", 0);

 imageView = new SmoothImageView(this);
 imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
 imageView.transformIn();
 imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
 imageView.setScaleType(ScaleType.FIT_CENTER);
 setContentView(imageView);
 ImageLoader.getInstance().displayImage(mDatas.get(mPosition), imageView);




 */