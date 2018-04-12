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
 */

public class CardImageAdapter extends RecyclerView.Adapter<CardImageAdapter.ViewHolder> implements View.OnClickListener {


    private Context mContext;

    private List<ImageText> imageTextList;
    private CardImageAdapter.MyItemClickListener mItemClickListener;


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
    public void setItemClickListener(CardImageAdapter.MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null)
            mItemClickListener.onItemClick(v,(int)v.getTag());
    }

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
    public CardImageAdapter(List<ImageText> list) {
        imageTextList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_image_text_view, parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);

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
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageText imageText = imageTextList.get(position);
        holder.text.setText(imageText.getText());
        holder.itemView.setTag(position);
        bingGlide(imageText,holder);
       // Glide.with(mContext).load(imageText.getImageId()).into(holder.image);
//        int tag=new Random(System.currentTimeMillis()).nextInt(4);
//        int pl= MyApplication.getResId("loading"+tag,R.drawable.class);
//        if(pl==1)
//            pl=R.drawable.loading1;
//        RequestOptions options = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//禁用掉Glide的缓存功能
//              //  .error(R.drawable.error)
//               // .skipMemoryCache(true)//禁用掉Glide的内存缓存功能
//
//                .placeholder(pl)
//
//                ;//占位
////        if(imageText.getFile()!=null){
////            //从本地加载
////            Glide.with(mContext)
////                    .load(imageText.getFile())
////                    .transition(withCrossFade())//带淡入淡出效果
////                    .apply(options)
////
////                    .into(holder.image);
////        }
////        else {
//            //url加载
//        Glide.with(mContext)
//                .load(imageText.getImageUrl())
//                .transition(withCrossFade())//带淡入淡出效果
//                .apply(options)
//                .into(holder.image);
//        }
    }
    public void bingGlide(ImageText imageText,final ViewHolder holder){
        int tag=new Random(System.currentTimeMillis()).nextInt(4)+1;
        int pl= MyApplication.getResId("loading"+tag,R.drawable.class);
        if(pl==1)
            pl=R.drawable.loading1;
        RequestOptions options = new RequestOptions()
              //  .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//禁用掉Glide的缓存功能
                //  .error(R.drawable.error)
                // .skipMemoryCache(true)//禁用掉Glide的内存缓存功能
                .placeholder(pl)
                .fitCenter()
                ;//占位
        Glide.with(mContext)
                .load(imageText.getImageUrl())
                .transition(withCrossFade())//带淡入淡出效果
                .apply(options)

                .into(holder.image);
    }
    @Override
    public int getItemCount() {
        return imageTextList.size();
    }

}
