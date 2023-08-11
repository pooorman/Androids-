package com.example.ggnews.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.R;
import com.example.ggnews.activity.UserDetailActivity;

import java.util.List;

public class ImageItemReAdapter extends RecyclerView.Adapter<ImageItemReAdapter.ViewHolder> {

  private List<String> mNewsData;
  private Context mContext;
  private int resourceId;

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  private int height;



  public ImageItemReAdapter(Context context, int resourceId, List<String> data) {
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;


  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    String item = mNewsData.get(position);
    if (item != null) {
      //展示图片的部分
      Glide.with(mContext).load(item).into(holder.imageView);
      ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
      layoutParams.height = getHeight(); // 这里的800是RecyclerView的高度
      holder.imageView.setLayoutParams(layoutParams);
      holder.imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // 创建一个Dialog对话框
          Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
          dialog.setContentView(R.layout.dialog_image_zoom);

          // 获取对话框中的ImageView和背景View
          ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
          View background = dialog.findViewById(R.id.background);

          // 设置放大后的图片
          Glide.with(mContext).load(item)
            .into(zoomImage);

          // 设置背景点击事件监听器
          background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // 关闭对话框
              dialog.dismiss();
            }
          });

          // 显示对话框
          dialog.show();
        }
      });


    }
  }

  @Override
  public int getItemCount() {
    return mNewsData.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;

    ViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.id_image);
    }
  }
}

