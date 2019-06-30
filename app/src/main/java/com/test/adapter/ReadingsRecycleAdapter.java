package com.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.test.fan.R;
import com.test.fan.ReadingsDisplayActivity;
import com.test.model.entity.Readings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadingsRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int IMAGE_VIEW=0,NO_IMAGE_VIEW=1;
    //当前上下文对象
    private Context context;
    //RecyclerView填充Item数据的List对象
    private List<Readings> readingsList;
    private Set<String> urlSet=new HashSet<String>();
    public ReadingsRecycleAdapter(Context context,List<Readings> readingsList){
        this.context = context;
        this.readingsList = readingsList;
    }

    @Override
    public int getItemViewType(int position) {
        if(readingsList.get(position).getImg_url().equals(""))
            return NO_IMAGE_VIEW;
        else
            return IMAGE_VIEW;
    }

    //创建ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View view;
        if(viewType==IMAGE_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.readings_item,
                    null);
            return new ImageViewHolder(view);
        }
        else
        {
            view= LayoutInflater.from(context).inflate(R.layout.readings_no_image_item,
                    null);
            return new NoImageViewHolder(view);

        }
        //返回MyViewHolder的对象

    }
    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Readings readings= readingsList.get(position);
        if(holder instanceof ImageViewHolder)
        {
            ImageViewHolder imageHolder=(ImageViewHolder)holder;
            if(urlSet.contains(readings.getUrl())) {
                imageHolder.readings_title.setTextColor(Color.GRAY);
            }
            imageHolder.readings_title.setText(readings.getTitle());
            imageHolder.readings_digest.setText(readings.getDigest());
            if(!readings.getGroup().equals(""))
                imageHolder.readings_group.setText(readings.getGroup()+"   ");
            imageHolder.readings_date.setText(readings.getDate());
            if(!readings.getImg_url().equals(""))
                Glide.with(context).load(readings.getImg_url()).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageHolder.readings_imageView);
        }
        else if(holder instanceof NoImageViewHolder)
        {
            NoImageViewHolder noImageHolder=(NoImageViewHolder)holder;
            if(urlSet.contains(readings.getUrl())) {
                noImageHolder.readings_title.setTextColor(Color.GRAY);
            }
            noImageHolder.readings_title.setText(readings.getTitle());
            noImageHolder.readings_digest.setText(readings.getDigest());
            if(!readings.getGroup().equals(""))
                noImageHolder.readings_group.setText(readings.getGroup());
            noImageHolder.readings_date.setText(readings.getDate());
        }
    }

    //返回Item的数量
    @Override
    public int getItemCount() {
        return readingsList.size();
    }

    //继承RecyclerView.ViewHolder抽象类的自定义ViewHolder
    class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView readings_title;
        ImageView readings_imageView;
        TextView readings_digest;
        TextView readings_date;
        TextView readings_group;
        public ImageViewHolder(View view) {
            super(view);
            readings_title = (TextView) view
                    .findViewById(R.id.readings_title);
            readings_imageView =(ImageView)view.findViewById(R.id.imageView);
            readings_digest =(TextView) view.findViewById(R.id.readings_content);
            readings_date=(TextView)view.findViewById(R.id.date);
            readings_group=(TextView)view.findViewById(R.id.group);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Readings readings=readingsList.get(getLayoutPosition());
                    urlSet.add(readings.getUrl());
                    Intent intent = new Intent(context, ReadingsDisplayActivity.class);
                    intent.putExtra("readings", readings);
                    context.startActivity(intent);
                }
            });
        }

    }
    //继承RecyclerView.ViewHolder抽象类的自定义ViewHolder
    class NoImageViewHolder extends RecyclerView.ViewHolder {
        TextView readings_title;
        TextView readings_digest;
        TextView readings_group;
        TextView readings_date;
        public NoImageViewHolder(View view) {
            super(view);
            readings_title = (TextView) view
                    .findViewById(R.id.readings_title);
            readings_digest =(TextView) view.findViewById(R.id.readings_content);
            readings_date=(TextView)view.findViewById(R.id.date);
            readings_group=(TextView)view.findViewById(R.id.group);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Readings readings=readingsList.get(getLayoutPosition());
                    urlSet.add(readings.getUrl());
                    Intent intent = new Intent(context, ReadingsDisplayActivity.class);
                    intent.putExtra("readings", readings);
                    context.startActivity(intent);
                }
            });
        }

    }
    //下面两个方法提供给页面刷新和加载时调用
    public void load(List<Readings> addReadingsList) {
        //增加数据
        int position = getItemCount();
        readingsList.addAll(position, addReadingsList);
        notifyItemInserted(position);
    }

    public void recommend(List<Readings> newList) {
        //推荐数据
        readingsList.addAll(0,newList);
        notifyDataSetChanged();
    }
    public void refresh(List<Readings> newList)
    {
        //刷新数据
        readingsList.clear();
        readingsList.addAll(newList);
        notifyDataSetChanged();
    }
}
