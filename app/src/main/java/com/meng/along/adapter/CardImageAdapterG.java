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
import com.meng.along.MyApplication;
import com.meng.along.R;

import java.util.List;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Long on 2018/3/22.
 */

public class CardImageAdapterG extends RecyclerView.Adapter<CardImageAdapterG.ViewHolder> {


    private Context mContext;

    private List<ImageText> imageTextList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView image;
        TextView text;
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
//        final ViewHolder holder = new ViewHolder(view);
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                ImageText imageText = imageTextList.get(position);
////                Intent intent = new Intent(mContext, FruitActivity.class);
////                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
////                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
////                mContext.startActivity(intent);
//            }
//        });
        //return holder;
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageText imageText = imageTextList.get(position);
        holder.text.setText(imageText.getText());
        // Glide.with(mContext).load(imageText.getImageId()).into(holder.image);
        int tag=new Random().nextInt(4);
        int pl= MyApplication.getResId("loading"+tag,R.drawable.class);
        if(pl==1)
            pl=R.drawable.loading1;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用掉Glide的缓存功能
                //  .error(R.drawable.error)
                // .skipMemoryCache(true)//禁用掉Glide的内存缓存功能

                .placeholder(pl)

                ;//占位
//        if(imageText.getFile()!=null){
        //从本地加载
        Glide.with(mContext)
                .load(imageText.getFile())
                .transition(withCrossFade())//带淡入淡出效果
                .apply(options)
                .into(holder.image);
//        }
//        else {
        //url加载
//        Glide.with(mContext)
//                .load(imageText.getImageUrl())
//                .transition(withCrossFade())//带淡入淡出效果
//                .apply(options)
//                .into(holder.image);
//        }
    }

    @Override
    public int getItemCount() {
        return imageTextList.size();
    }

}
