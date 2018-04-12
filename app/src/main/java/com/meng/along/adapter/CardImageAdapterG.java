package com.meng.along.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.meng.along.ImageText;
import com.meng.along.common.MyApplication;
import com.meng.along.R;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Long on 2018/3/22.
 * 本地图片adapter
 */

public class CardImageAdapterG extends RecyclerView.Adapter<CardImageAdapterG.ViewHolder> implements View.OnClickListener {


    private Context mContext;

    private List<ImageText> imageTextList;
    private MyItemClickListener mItemClickListener;


    /**
     * 创建一个回调接口
     */
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
     *
     * @param myItemClickListener
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            mItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView image;
        private TextView text;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            image = (ImageView) view.findViewById(R.id.image);
            text = (TextView) view.findViewById(R.id.text);

        }


    }
    public CardImageAdapterG(List<ImageText> list) {
        imageTextList = list;
    }
    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_gallery_image_text_view, parent, false);

        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                ImageText imageText = imageTextList.get(position);
//                Intent intent = new Intent(mContext, ImageActivity.class);
//                intent.putExtra("url",imageText.getImageUrl());
//              //  mContext.startActivity(intent);
//                mContext.startActivity(intent);
//
//            }
//        });
        return holder;
        // return new ViewHolder(view);
    }
    public void removeData(int position) {
        imageTextList.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(  ViewHolder holder, int position) {
        ImageText imageText = imageTextList.get(position);
        holder.text.setText(imageText.getFileName());
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        // Glide.with(mContext).load(imageText.getImageId()).into(holder.image);
        bingGlide(imageText,holder);
    }
    public void bingGlide(ImageText imageText,  ViewHolder holder){
        int tag=new Random(System.currentTimeMillis()).nextInt(4)+1;
        int pl= MyApplication.getResId("loading"+tag,R.drawable.class);
        if(pl==1)
            pl=R.drawable.loading1;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)//禁用掉Glide的缓存功能
                //  .error(R.drawable.error)
                // .skipMemoryCache(true)//禁用掉Glide的内存缓存功能
                .placeholder(pl)
                ;//占位
        if(imageText.getFile()!=null)
        Glide.with(mContext)
                .load(imageText.getFile())
                .transition(withCrossFade())//带淡入淡出效果
                .apply(options)
                .into(holder.image);
    }
    @Override
    public int getItemCount() {
        return imageTextList.size();
    }

}

























