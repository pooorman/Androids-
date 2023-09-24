package com.example.ggnews.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.activity.ImageDetailActivity;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.R;
import com.example.ggnews.javabean.Records;

import java.util.List;
public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ViewHolder> {

  private List<Records> mNewsData;
  private Context mContext;
  private int resourceId;

  public LoginResponse getLoginData() {
    return loginData;
  }

  public void setLoginData(LoginResponse loginData) {
    this.loginData = loginData;
  }

  private LoginResponse loginData;

  public ImageItemAdapter(Context context, int resourceId, List<Records> data) {
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
    Records item = mNewsData.get(position);
    if (item != null) {
      holder.likeCountTextView.setText(String.valueOf(item.getLikeNum()));
      Glide.with(mContext).load(item.getImageUrlList()).into(holder.imageView);
      //触发startActivity
      holder.imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(mContext, ImageDetailActivity.class);
          //Intent存登录内容
          intent.putExtra("record",item);
//          item.setUsername("");
          intent.putExtra("loginData1",loginData);
          mContext.startActivity(intent);
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
    TextView likeCountTextView;

    ViewHolder(View itemView) {
      super(itemView);
      imageView = itemView.findViewById(R.id.imageView);
      likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
    }
  }
}
