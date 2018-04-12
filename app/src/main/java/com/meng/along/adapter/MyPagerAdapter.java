package com.meng.along.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.meng.along.ImageText;

import java.util.List;

/**
 * 项目名称：BingWallPaper
 * Created by Long on 2018/4/2.
 * 修改时间：2018/4/2 13:32
 */
public class MyPagerAdapter extends PagerAdapter {
    private List<ImageView> list;
    private Context context;


    //该方法将返回所包含的item总个数。为了实现一种循环滚动的效果，返回了基本整形的最大值
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
    //判断出去的view是否等于进来的view，如果为true直接复用
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    public MyPagerAdapter(List<ImageView> list, Context context) {
        super();
        this.list = list;
        this.context = context;
    }
    //销毁预加载以外的view对象，会把需要销毁的对象的索引位置传进来，就是position，y
    //因为imageView只要6条数据，而position会取到很大的值，所以使用取余数的方法来获取每一条数据项
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView(list.get(position%list.size()));
    }
    //装入图片
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // TODO Auto-generated method stub
        final ImageView imageView = list.get(position % list.size());
        container.addView(list.get(position%list.size()),0);
        return list.get(position%list.size());
    }
}
